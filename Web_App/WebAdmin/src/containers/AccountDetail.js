import React, { Component, PropTypes } from "react"
import { connect } from "react-redux"
import { Link } from "react-router"

import { getAccountDetail } from "../actions/AccountsAction"
import { initBorrow, checkout, deleteBorrowedCopy, fetchSaveBorrowedCopy } from "../actions/BookBorrowAction"
import * as Socket from "../helpers/Socket"

class AccountDetail extends Component {
	librarianId = 1

	constructor(props) {
		super(props)

		this.renderBorrowedBooks = this.renderBorrowedBooks.bind(this)
		this.onClickDeleteCopy = this.onClickDeleteCopy.bind(this)
		this.onClickStartAddBooks = this.onClickStartAddBooks.bind(this)

		this.state = {
			userId: this.props.params.id,
			ibeaconId: this.librarianId,	// Each librarian should have a RFID Reader -> pair reader-borrowCart with his id,
																		// but for demo purpose, we pair with ibeaconId = 1
			isAddingBook: false
		}
	}

	componentWillMount() {
		this.props.getAccountDetail(this.props.params.id)
	}

	componentWillUnmount() {
		this.props.checkout(this.state.userId, this.state.ibeaconId)
		this.disconnectFromChannel()
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
				<h4>Borrowing Book List</h4>
				{this.renderBorrowedBooks(account.borrowedBookCopies)}
			</div>
		)
	}

	connectToChannel() {
		const self = this
		this.socketClient = Socket.initSocket()
		this.socketClient.connect({}, frame => {
			console.log('Connected: ' + frame)
			self.socketClient.subscribe('/socket', returnedData => {
				console.log("Socket Called!!")
				console.log(returnedData)
				self.props.fetchSaveBorrowedCopy(JSON.parse(returnedData.body))
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
			this.props.initBorrow(userId, ibeaconId)
			this.connectToChannel()
		} else {
			console.log("On Disconnect Socket: " + userId + " " + ibeaconId)
			this.props.checkout(userId, ibeaconId)
			this.disconnectFromChannel()
		}

		this.setState({ isAddingBook: !this.state.isAddingBook })
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
		const borrowedCopyId = borrowedBook.id

		return (
			<tr key={borrowedCopyId}>
				<td>{borrowedBook.bookCopyRfid}</td>
				<td>{borrowedBook.bookCopyBookTitle}</td>
				<td>{borrowedBook.borrowedDate}</td>
				<td>{borrowedBook.deadlineDate}</td>
				<td>
					<a onClick={() => this.onClickDeleteCopy(userId, borrowedCopyId)}>
						<span className="glyphicon glyphicon-remove" aria-hidden="true" />
					</a>
				</td>
			</tr>
		)
	}

	onClickDeleteCopy(userId, borrowedCopyId) {
		this.props.deleteBorrowedCopy(userId, borrowedCopyId)
	}
}

function mapStateToProps(state) {
	return { account: state.accounts.account }
}

export default connect(
	mapStateToProps,
	{ getAccountDetail, deleteBorrowedCopy, initBorrow, checkout, fetchSaveBorrowedCopy })
(AccountDetail)
