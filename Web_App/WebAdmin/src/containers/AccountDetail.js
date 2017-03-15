import React, { Component, PropTypes } from "react"
import { connect } from "react-redux"
import { Link } from "react-router"

import { getAccountDetail } from "../actions/AccountsAction"
import { initBorrow, checkout, deleteBorrowedCopy, fetchCopyFromCart, cancelAddingCopies } from "../actions/BookBorrowAction"
import * as Socket from "../helpers/Socket"

class AccountDetail extends Component {
	librarianId = 1

	constructor(props) {
		super(props)

		this.renderBorrowedBooks = this.renderBorrowedBooks.bind(this)
		this.onClickDeleteCopy = this.onClickDeleteCopy.bind(this)
		this.onClickStartAddBooks = this.onClickStartAddBooks.bind(this)
		this.onClickCancel = this.onClickCancel.bind(this)
		this.beforeUnload = this.beforeUnload.bind(this)
		this.onUnload = this.onUnload.bind(this)
		// this.onPageHide = this.onPageHide.bind(this)

		this.state = {
			userId: this.props.params.id,
			ibeaconId: this.librarianId,	// Each librarian should have a RFID Reader -> pair reader-borrowCart with his id,
																		// but for demo purpose, we pair with ibeaconId = 1
			isAddingBook: false,
			beforeAdd: {}
		}
	}

	componentWillMount() {
		this.props.getAccountDetail(this.props.params.id)
		// init close openning socket
		this.disconnectFromChannel()
		// no need to delete pending cart -> server handle this.
		// this.props.cancelAddingCopies(this.state.userId, this.state.ibeaconId)

		this.props.router.setRouteLeaveHook(this.props.route, () => {
			if (this.state.isAddingBook) {
				return 'You have unsaved information, are you sure you want to leave this page?'
			}
		})

		window.addEventListener("beforeunload", this.beforeUnload)
		window.addEventListener("unload", this.onUnload)
	}

	componentWillUnmount() {
		if (this.state.isAddingBook) {
			this.onUnload()

			// window.removeEventListener("beforeunload", this.beforeUnload)
			// window.removeEventListener("unload", this.onUnload)
		}

	}

	render() {
		const { account } = this.props

		if (!account) {
			return <div>Loading...</div>
		}

		const userId = this.state.userId
		const ibeaconId = this.state.ibeaconId

		return (
			<div>
				<Link to="/">Back</Link>
				<h4>{userId}</h4>
				<h5>{account.profile.fullname}</h5>
				<br />
				<button
					className="btn btn-primary"
					onClick={() => this.onClickStartAddBooks(userId, ibeaconId)}>
					{this.state.isAddingBook? "Stop Adding Books" : "Start Adding Books"}
				</button>
				<button
					className={`btn btn-default ${this.state.isAddingBook ? '' : 'hidden'}`}
					onClick={() => this.onClickCancel()}>
					Cancel</button>
				<h4>Borrowing Book List</h4>
				{this.renderBorrowedBooks(account.borrowedBookCopies)}
			</div>
		)
	}

	beforeUnload(event) {
		if (this.state.isAddingBook) {
			console.log("beforeUnload")
			event.returnValue = "beforeUnload"
		}
	}

	onUnload(event) {
		if (this.state.isAddingBook) {
			console.log("onUnload")
			this.disconnectFromChannel()
			// this.props.cancelAddingCopies(this.state.userId, this.state.ibeaconId, this.state.beforeAdd)
			// should not checkout when librarian unload
			this.props.checkout(this.state.userId, this.state.ibeaconId)
			event.returnValue = "unload"
		}
	}

	// onPageHide() {
	// 	console.log("pagehide")
	// 	this.disconnectFromChannel()
	// 	this.props.checkout(this.state.userId, this.state.ibeaconId)
	// }

	onClickCancel() {
		this.disconnectFromChannel()
		this.props.cancelAddingCopies(this.state.userId, this.state.ibeaconId, this.state.beforeAdd)
		this.setState({ isAddingBook: false })

		// window.removeEventListener("beforeunload", this.beforeUnload)
		// if ("onpagehide" in window) {
		// 	window.removeEventListener("pagehide", this.onPageHide, false)
		// } else {
		// window.removeEventListener("unload", this.onUnload)
		// }
	}

	connectToChannel() {
		const self = this
		this.socketClient = Socket.initSocket()
		this.socketClient.connect({}, frame => {
			console.log('Connected: ' + frame)
			self.socketClient.subscribe('/socket', returnedData => {
				console.log("Socket Called!!")
				console.log(returnedData)
				self.props.fetchCopyFromCart(JSON.parse(returnedData.body))
			})
		})
	}

	disconnectFromChannel() {
		if (this.socketClient != null) {
			this.socketClient.disconnect()
		}
		console.log("Disconnected")
	}

	onClickStartAddBooks(userId, ibeaconId) {
		// const addingStatusBeforeClick = this.state.isAddingBook
		if (!this.state.isAddingBook) {
			this.setState({ beforeAdd: this.props.account })
			this.props.initBorrow(userId, ibeaconId)
			this.connectToChannel()
		} else {
			console.log("On Disconnect Socket: " + userId + " " + ibeaconId)
			this.props.checkout(userId, ibeaconId)
			this.disconnectFromChannel()
		}

		this.setState({ isAddingBook: !this.state.isAddingBook })

		// if (!addingStatusBeforeClick) {
			// window.addEventListener("beforeunload", this.beforeUnload)
			// if ("onpagehide" in window) {
			// 	window.addEventListener("pagehide", this.onPageHide)
			// } else {
			// window.addEventListener("unload", this.onUnload)
			// }
		// }
	}

	renderBorrowedBooks(borrowedBookCopies) {
		return (
			<table className="table table-striped">
				<thead>
					<tr>
						<th>RFID</th>
						<th>Title</th>
						<th>Borrowed Date</th>
						<th>Dealine Date</th>
						<th>Tools</th>
					</tr>
				</thead>
				<tbody>
					{borrowedBookCopies.map(e => this.renderBorrowedBook(e))}
				</tbody>
			</table>
		)
	}

	renderBorrowedBook(borrowedBook) {
		const userId = this.state.userId
		const borrowedCopyRfid = borrowedBook.bookCopyRfid

		return (
			<tr key={borrowedCopyRfid}>
				<td>{borrowedBook.bookCopyRfid}</td>
				<td>{borrowedBook.bookCopyBookTitle}</td>
				<td>{borrowedBook.borrowedDate}</td>
				<td>{borrowedBook.deadlineDate}</td>
				<td>
					<a
						className={`${this.state.isAddingBook ? "disable" : ""}`}
						onClick={() => this.onClickDeleteCopy(userId, borrowedCopyRfid)}>
						<span className="glyphicon glyphicon-remove" aria-hidden="true" />
					</a>
				</td>
			</tr>
		)
	}

	onClickDeleteCopy(userId, borrowedCopyRfid) {
		this.props.deleteBorrowedCopy(userId, borrowedCopyRfid)
	}
}

function mapStateToProps(state) {
	return { account: state.accounts.account }
}

export default connect(
	mapStateToProps,
	{ getAccountDetail, deleteBorrowedCopy, initBorrow, checkout, fetchCopyFromCart, cancelAddingCopies })
(AccountDetail)
