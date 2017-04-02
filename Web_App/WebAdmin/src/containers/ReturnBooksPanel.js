import React, { Component } from 'react'
import { connect } from 'react-redux'
import { commitReturnCopies, cancelReturnCopies } from "../actions/BookReturnAction"

class ReturnBooksPanel extends Component {
	constructor(props) {
		super(props)

		this.renderBookList = this.renderBookList.bind(this)
		this.renderBook = this.renderBook.bind(this)
	}

	render() {
		const { librarianId, userId, books, returningBooks, isReturningBooks } = this.props

		return (
			<div>
				{!isReturningBooks && <p className="returned-books-title">Last user transaction:</p>}
				<div className="panel panel-default" style={{ width: "100%" }}>
					<div className="panel-heading">
						{isReturningBooks && <h3 className="panel-title">Returning books for user {userId}</h3>}
						{!isReturningBooks && <h3 className="panel-title">Returned books of user {userId}</h3>}
					</div>
					<div className="panel-body">
						{this.renderBookList(librarianId, books, returningBooks, isReturningBooks)}
					</div>
				</div>
			</div>
		)
	}

	renderBookList(librarianId, books, returningBooks, isReturningBooks) {
		return (
			<div>
				<table className="table table-striped">
					<thead>
					<tr>
						<th>No.</th>
						<th>Rfid</th>
						<th>Title</th>
						<th>Borrowed Date</th>
						<th>Dealine Date</th>
						<th>Returned Date</th>
					</tr>
					</thead>
					<tbody>
					{books.map((book, index) => this.renderBook(book, index))}
					</tbody>
				</table>
				<div className={isReturningBooks ? "" : "hidden"}>
					<button
						className={`cancel-add-book-btn btn btn-default ${returningBooks ? '' : 'hidden'}`}
						onClick={() => this.props.cancelReturnCopies(librarianId)}
						style={{ marginRight: "5px" }}>
						Cancel
					</button>
					<button
						className="btn btn-primary"
						onClick={() => this.props.commitReturnCopies(librarianId)}>
						Submit
					</button>
				</div>
			</div>
		)
	}

	renderBook(book, index) {
		const returnedCopyRfid = book.bookCopyRfid

		return (
			<tr key={returnedCopyRfid}>
				<td>{index + 1}</td>
				<td>{returnedCopyRfid}</td>
				<td>{book.bookCopyBookTitle}</td>
				<td>{book.borrowedDate}</td>
				<td>{book.deadlineDate}</td>
				<td>{book.returnDate ? book.returnDate : "pending..."}</td>
			</tr>
		)
	}
}

export default connect(null, { commitReturnCopies, cancelReturnCopies })(ReturnBooksPanel)