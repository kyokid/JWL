import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
	fetchAddedReturnedCopyFromCart,
	fetchCommittedReturnCopies,
	commitReturnCopies,
	cancelReturnCopies,
	addReturnCopyToCart,
	fetchCancelReturnCopies
} from '../actions/BookReturnAction'
import { browserHistory } from 'react-router'

import { LOGIN } from '../constants/url-path'
import { UNDEFINED, RETURN_BOOKS } from '../constants/common'
import { switchStateNavBar } from '../actions/RouteAction'
import ReturnBooksPanel from './ReturnBooksPanel'
import isLoggedIn from '../helpers/Authentication'
import * as Socket from '../helpers/Socket'

class BooksReturn extends Component {
	constructor(props) {
		super(props)

		// TODO: get librarianId from current librarian
		this.state = {
			returningBooks: false,
			librarianId: '1'
		}

		this.connectToChannel = this.connectToChannel.bind(this)
		this.disconnectFromChannel = this.disconnectFromChannel.bind(this)
		this.renderConfirmation = this.renderConfirmation.bind(this)
		this.confirmAndSwitchUser = this.confirmAndSwitchUser.bind(this)
		this.beforeUnload = this.beforeUnload.bind(this)
	}

	componentWillMount() {
		// need to check before connectToChannel
		// if not, connectToChannel will prevent this page from redirecting to login
		if (!isLoggedIn()) {
			browserHistory.push(LOGIN)
			return
		}
		this.connectToChannel()
		this.props.cancelReturnCopies(this.state.librarianId)

		window.addEventListener("beforeunload", this.beforeUnload)

		this.props.router.setRouteLeaveHook(this.props.route, () => {
			if (this.state.returningBooks) {
				return 'You have unsaved information, are you sure you want to leave this page?'
			}
		})

		this.props.switchStateNavBar(RETURN_BOOKS)
	}

	componentWillReceiveProps(nextProps) {
		if (nextProps.returningBooks.length > 0) {
			this.setState({ returningBooks: true })
		} else {
			this.setState({ returningBooks: false })
		}

		if (nextProps.responseCode === "405") {
			$(this.refs.modal).modal('show')
		}
	}

	componentWillUnmount() {
		this.disconnectFromChannel()
	}

	render() {
		let { returningBooks, returnedBooks, responseMsg, responseCode, returnedBookOfAnotherUser } = this.props

		if (returningBooks.length === 0) {
			return (
				<div>
					<h1 style={{ marginTop: "40px" }}>Scan a returned book to start.</h1>
					{returnedBooks.length !== 0 && <ReturnBooksPanel librarianId={this.state.librarianId}
																													 userId={returnedBooks[0].accountUserId}
																													 books={returnedBooks}
																													 returningBooks={this.state.returningBooks}
																													 isReturningBooks={false} />}
				</div>
			)
		}

		const currentUserId = returningBooks[0].accountUserId
		const lastUserId = returnedBooks.length !== 0 ? returnedBooks[0].accountUserId : "last User ID"

		return (
			<div>
				{/*{responseCode != "" && responseCode == "400" ? <h3>{responseMsg}</h3> : ""}*/}
				{this.renderConfirmation(currentUserId, returnedBookOfAnotherUser)}
				{returningBooks && <p className="returning-books-title">Current user transaction:</p>}
				<ReturnBooksPanel librarianId={this.state.librarianId}
													userId={currentUserId}
													books={returningBooks}
													returningBooks={this.state.returningBooks}
													isReturningBooks={true} />

				{returnedBooks.length !== 0 && <ReturnBooksPanel librarianId={this.state.librarianId}
																												 userId={lastUserId}
																												 books={returnedBooks}
																												 returningBooks={this.state.returningBooks}
																												 isReturningBooks={false} />}
			</div>
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
						<div className="modal-body" style={{ paddingLeft: "30px" }}>
							<p>Found new user: <strong>{newUserId}</strong></p>
							<p>Book title: <strong>{title}</strong></p>
							<p>Book RFID: <strong>{rfid}</strong></p>
							<p>
								Commit returning book(s) for user <strong>{currentUserId} </strong>
								and start new process for user <strong>{newUserId}</strong>?
							</p>
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">Cancel</button>
							<button type="button"
											className="btn btn-primary"
											data-dismiss="modal"
											onClick={() => this.confirmAndSwitchUser(this.state.librarianId, rfid)}>
								Commit
							</button>
						</div>
					</div>
				</div>
			</div>
		)
	}

	confirmAndSwitchUser(librarianId, rfid) {
		const self = this
		this.props.commitReturnCopies(this.state.librarianId).then(() => {
			self.props.addReturnCopyToCart(librarianId, rfid)
		})
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
				if (returnedJson.data === null) {
					self.props.fetchCancelReturnCopies(returnedJson)
				} else if (returnedJson.data.constructor === Array) {
					self.props.fetchCommittedReturnCopies(returnedJson)
				} else {
					self.props.fetchAddedReturnedCopyFromCart(returnedJson)
				}
			})
		})
	}

	disconnectFromChannel() {
		if (typeof this.socketClient !== UNDEFINED) {
			this.socketClient.disconnect()
		}
		console.log("Disconnected")
	}

	beforeUnload(event) {
		console.log(this.state.returningBooks)
		if (this.state.returningBooks) {
			event.returnValue = "before unload"
		}
	}
}

function mapStateToProps(state) {
	return {
		returningBooks: state.booksReturnData.returningBooks,
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
	fetchCancelReturnCopies,
	addReturnCopyToCart,
	switchStateNavBar
})(BooksReturn)
