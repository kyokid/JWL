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
		this.renderBorrowedBookPanel = this.renderBorrowedBookPanel.bind(this)
		this.onClickDeleteCopy = this.onClickDeleteCopy.bind(this)
		this.onClickStartAddBooks = this.onClickStartAddBooks.bind(this)
		this.onClickCancel = this.onClickCancel.bind(this)
		this.beforeUnload = this.beforeUnload.bind(this)
		this.onUnload = this.onUnload.bind(this)

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
			<div className="account-detail">
				<Link to="/" className="back">Back</Link>
				<div className="panel panel-primary" style={{ width: "100%" }}>
					<div className="panel-heading">
						<h3 className="panel-title">Detail of User {userId}.</h3>
					</div>
					<div className="panel-body">
						<div className="col-md-6 col-sm-6">
							<h4>User ID: {userId}</h4>
							<p>Full name: {account.profile.fullname}</p>
							<p>Phone Number: {account.profile.phoneNo}</p>
							<p>Email: {account.profile.email}</p>
							<p>Address: {account.profile.address}</p>
							<p>Place of Work: {account.profile.placeOfWork}</p>
							<p>Date of Birth: {account.profile.dateOfBirth}</p>
						</div>
						<div className="col-md-6 col-sm-6">
							<img className="user-img" src={account.profile.imgUrl} alt="User Image" />
						</div>
					</div>
				</div>
				<button
					className={`cancel-add-book-btn btn btn-default ${this.state.isAddingBook ? '' : 'hidden'}`}
					onClick={() => this.onClickCancel()}
					style={{ marginLeft: "10px" }}>
					Cancel
				</button>
				<button
					className="btn btn-primary"
					onClick={() => this.onClickStartAddBooks(userId, ibeaconId)}>
					{this.state.isAddingBook? "Stop Adding Books" : "Start Adding Books"}
				</button>

				{this.renderBorrowedBookPanel(userId, account.borrowedBookCopies)}

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
			// should not checkout when librarian unload
			// this.props.checkout(this.state.userId, this.state.ibeaconId)
			event.returnValue = "unload"
		}
	}

	onClickCancel() {
		this.disconnectFromChannel()
		this.props.cancelAddingCopies(this.state.userId, this.state.ibeaconId, this.state.beforeAdd)
		this.setState({ isAddingBook: false })
	}

	connectToChannel() {
		const self = this
		this.socketClient = Socket.initSocket()
		this.socketClient.connect({}, frame => {
			console.log('Connected: ' + frame)
			self.socketClient.subscribe('/socket/add/books', returnedData => {
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
	}

	renderBorrowedBookPanel(userId, borrowedBookCopies) {
		if (!borrowedBookCopies || borrowedBookCopies.length == 0) {
			return (
				<div style={{ marginTop: "50px" }}>
					<h3>User {userId} is not borrowing any books.</h3>
				</div>
			)
		}
		return (
			<div className="panel panel-default" style={{ width: "100%" }}>
				<div className="panel-heading">
					<h3 className="panel-title">Borrowing Books of user {userId}</h3>
				</div>
				<div className="panel-body">
					{this.renderBorrowedBooks(borrowedBookCopies)}
				</div>
			</div>
		)
	}

	renderBorrowedBooks(borrowedBookCopies) {
		return (
			<table className="table table-striped">
				<thead>
					<tr>
						<th>No.</th>
						<th>Title</th>
						<th>Borrowed Date</th>
						<th>Dealine Date</th>
						<th>Tools</th>
					</tr>
				</thead>
				<tbody>
					{borrowedBookCopies.map((borrowedBook, index) => this.renderBorrowedBook(borrowedBook, index))}
				</tbody>
			</table>
		)
	}

	renderBorrowedBook(borrowedBook, index) {
		const userId = this.state.userId
		const borrowedCopyRfid = borrowedBook.bookCopyRfid

		return (
			<tr key={borrowedCopyRfid}>
				<td>{index + 1}</td>
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
