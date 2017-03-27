import React, { Component } from 'react'
import _ from 'lodash'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { browserHistory, Link } from "react-router"

import { getAllBooks } from '../actions/BooksAction'
import { switchStateNavBar } from '../actions/RouteAction'
import { MANAGE_BOOKS } from '../constants/common'
import { checkLibrarian } from '../helpers/Authorization'

class BookList extends Component {
	constructor(props) {
		super(props)

		this.state = { isLibrarian: true }
	}

	componentWillMount() {
		if (!checkLibrarian()) {
			this.setState({ isLibrarian: false })
			return
		}

		this.props.switchStateNavBar(MANAGE_BOOKS)
		this.props.getAllBooks()
	}

	render() {
		if (!this.state.isLibrarian) return <div />

		const { books, message } = this.props
		if (!books) {
			return (
				<div className="table-book-container">
					<h2>{message}</h2>
				</div>
			)
		}

		let sortedBooks = _.sortBy(books, ['title'])

		return (
			<div className="table-book-container">
				{/*<Link className="book-new-btn" to="new/book">*/}
					{/*<span className="glyphicon glyphicon-plus" />*/}
				{/*</Link>*/}
				<table className="table table-striped table-books">
					<thead>
					<tr>
						<th>No.</th>
						<th>Title</th>
						<th>Authors</th>
						<th>Publisher</th>
						<th>PublishYear</th>
						<th>Number of Copies</th>
						{/*<th>Tools</th>*/}
					</tr>
					</thead>
					<tbody>
					{sortedBooks.map((book, index) => this.renderBook(book, index))}
					</tbody>
				</table>
			</div>
		)
	}

	renderBook(book, index) {
		const bookId = book.id
		return (
			<tr key={bookId}>
				<td>{index + 1}</td>
				<td className="clickable" onClick={() => browserHistory.push(`books/${bookId}`)}>{book.title}</td>
				<td>
					{book.bookAuthors.map((author) => <p style={{ fontSize: "inherit" }} key={author.id}>{author.authorName}</p>)}
				</td>
				<td>{book.publisher}</td>
				<td>{book.publishYear}</td>
				<td>{book.numberOfCopies}</td>
				{/*<td>*/}
					{/*<a href="#"><span className="glyphicon glyphicon-remove" aria-hidden="true" /></a>*/}
				{/*</td>*/}
			</tr>
		)
	}
}

function mapStateToProps(state) {
	return {
		books: state.books.all,
		message: state.books.message
	}
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getAllBooks, switchStateNavBar }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(BookList)
