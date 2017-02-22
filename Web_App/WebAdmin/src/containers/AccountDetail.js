import React, { Component, PropTypes } from "react"
import { connect } from "react-redux"
import { Link, browserHistory } from "react-router"

import { getAccountDetail, deleteBorrowedCopy } from "../actions/AccountsAction"
import { initBorrow, checkout } from "../actions/BookBorrowAction"
import { USERS } from "../constants/api"

class AccountDetail extends Component {
	constructor(props) {
		super(props)

		this.renderBorrowedBooks = this.renderBorrowedBooks.bind(this)
		this.onClickDeleteCopy = this.onClickDeleteCopy.bind(this)
		this.onClickStartAddBooks = this.onClickStartAddBooks.bind(this)

		this.state = {
			isAddingBook: false
		}
	}

	componentWillMount() {
		this.props.getAccountDetail(this.props.params.id)
	}

	render() {
		const { account } = this.props

		if (!account) {
			return <div>Loading...</div>
		}

		return (
			<div>
				<Link to="/">Back</Link>
				<h4>{account.userId}</h4>
				<h5>{account.profile.fullname}</h5>
				<br />
				<button
					className="btn btn-primary"
					onClick={this.onClickStartAddBooks}>
					{this.state.isAddingBook? "Stop Adding Books" : "Start Adding Books"}
				</button>
				<h4>Borrowing Book List</h4>
				{this.renderBorrowedBooks(account.borrowedBookCopies)}
			</div>
		)
	}

	onClickStartAddBooks() {
		if (!this.state.isAddingBook) {
			this.props.initBorrow("SE61476", "123")
		} else {
			this.props.checkout("SE61476", "123")
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
		const userId = borrowedBook.accountUserId
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
			.then(() => {
				browserHistory.push(`/${USERS}/${userId}`)
			})
	}
}

function mapStateToProps(state) {
	return { account: state.accounts.account }
}

export default connect(
	mapStateToProps,
	{ getAccountDetail, deleteBorrowedCopy, initBorrow, checkout })
(AccountDetail)
