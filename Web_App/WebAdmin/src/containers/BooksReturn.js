import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
	fetchAddedReturnedCopyFromCart,
	fetchCommittedReturnCopies,
	commitReturnCopies,
	cancelReturnCopies,
	fetchCancelReturnCopies
} from "../actions/BookReturnAction"

import * as Socket from "../helpers/Socket"

class BooksReturn extends Component {
	constructor(props) {
		super(props)

		this.state = {
			isReturningBooks: false,
			librarianId: ''
		}

		this.connectToChannel = this.connectToChannel.bind(this)
		this.disconnectFromChannel = this.disconnectFromChannel.bind(this)
		this.renderReturnedBooks = this.renderReturnedBooks.bind(this)
		this.renderReturnedBook = this.renderReturnedBook.bind(this)
		this.renderConfirmation = this.renderConfirmation.bind(this)
	}
	componentWillMount() {
		this.connectToChannel()
		// TODO: get librarianId from current librarian
		this.setState({ librarianId: "1" })
	}

	componentWillReceiveProps(nextProps) {
		if (nextProps.returnedBooks.length > 0) {
			this.setState({ isReturningBooks: true })
		}

		if (nextProps.responseCode == "405") {
			$(this.refs.modal).modal('show')
		}
	}

	render() {
		let { userId, returnedBooks, responseMsg, responseCode, returnedBookOfAnotherUser } = this.props

		if (returnedBooks.length == 0) {
			return (
				<div>
					<h1>Scan a returned book to start.</h1>
				</div>
			)
		}

		return (
			<div>
				{responseCode != "" && responseCode == "400" ? <h3>{responseMsg}</h3> : ""}
				{this.renderConfirmation(userId, returnedBookOfAnotherUser)}
				<div className="panel panel-default" style={{ width: "100%" }}>
					<div className="panel-heading">
						<h3 className="panel-title">Returning books for user {userId}</h3>
					</div>
					<div className="panel-body">
						{this.renderReturnedBooks(returnedBooks)}
					</div>
				</div>
			</div>
		)
	}

	renderReturnedBooks(returnedBooks) {
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
					{returnedBooks.map((returnedBook, index) => this.renderReturnedBook(returnedBook, index))}
					</tbody>
				</table>
				<button
					className={`cancel-add-book-btn btn btn-default ${this.state.isReturningBooks ? '' : 'hidden'}`}
					onClick={() => this.props.cancelReturnCopies(this.state.librarianId)}
					style={{ marginLeft: "10px" }}>
					Cancel
				</button>
				<button
					className="btn btn-primary"
					onClick={() => this.props.commitReturnCopies(this.state.librarianId)}>
					Submit
				</button>
			</div>
		)
	}

	renderReturnedBook(returnedBook, index) {
		const returnedCopyRfid = returnedBook.bookCopyRfid

		return (
			<tr key={returnedCopyRfid}>
				<td>{index + 1}</td>
				<td>{returnedCopyRfid}</td>
				<td>{returnedBook.bookCopyBookTitle}</td>
				<td>{returnedBook.borrowedDate}</td>
				<td>{returnedBook.deadlineDate}</td>
				<td>{returnedBook.returnDate ? returnedBook.returnDate : "pending..."}</td>
			</tr>
		)
	}

	renderConfirmation(currentUserId, returnedBookOfAnotherUser) {
		let newUserId = ""
		let rfid = ""
		let title = ""

		if (returnedBookOfAnotherUser) {
			newUserId = returnedBookOfAnotherUser.accountUserId
			rfid = returnedBookOfAnotherUser.bookCopyRfid
			title = returnedBookOfAnotherUser.bookCopyBookTitle
		}

		return (
			<div className="modal fade" data-backdrop="static" data-keyboard="false" tabIndex="-1" role="dialog" ref="modal">
				<div className="modal-dialog" role="document">
					<div className="modal-content">
						<div className="modal-header">
							<h4 className="modal-title">Confirmation Dialog</h4>
						</div>
						<div className="modal-body">
							<p>
								{`Found book ${title}, rfid ${rfid}, of user ${newUserId}, `}
								{`while returning book(s) for user ${currentUserId}.`}
							</p>
							<h4>
								{`Commit returning book(s) for user ${currentUserId} and start new process for user ${newUserId}?`}
							</h4>
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">Cancel</button>
							<button type="button" className="btn btn-primary">Commit</button>
						</div>
					</div>
				</div>
			</div>
		)
	}

	connectToChannel() {
		const self = this
		this.socketClient = Socket.initSocket()
		this.socketClient.connect({}, frame => {
			console.log('Connected: ' + frame)
			self.socketClient.subscribe('/socket/return/books', returnedData => {
				console.log("Socket Called!!")
				console.log(returnedData)
				const returnedJson = JSON.parse(returnedData.body)
				if (returnedJson.data == null) {
					self.props.fetchCancelReturnCopies(returnedJson)
				} else if (returnedJson.data.constructor != Array) {
					self.props.fetchAddedReturnedCopyFromCart(returnedJson)
				} else {
					self.props.fetchCommittedReturnCopies(returnedJson)
				}

			})
		})
	}

	disconnectFromChannel() {
		if (this.socketClient != null) {
			this.socketClient.disconnect()
		}
		console.log("Disconnected")
	}
}

function mapStateToProps(state) {
	return {
		userId: state.booksReturnData.userId,
		returnedBooks: state.booksReturnData.returnedBooks,
		returnedBookOfAnotherUser: state.booksReturnData.returnedBookOfAnotherUser,
		responseMsg: state.booksReturnData.responseMsg,
		responseCode: state.booksReturnData.responseCode
	}
}

export default connect(mapStateToProps, {
	fetchAddedReturnedCopyFromCart,
	fetchCommittedReturnCopies,
	commitReturnCopies,
	cancelReturnCopies,
	fetchCancelReturnCopies
})(BooksReturn)
