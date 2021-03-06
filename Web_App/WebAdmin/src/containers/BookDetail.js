import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link, browserHistory } from 'react-router'

import { getBookDetail, getBorrowingCopies } from '../actions/BooksAction'
import { switchStateNavBar } from '../actions/RouteAction'
import { MANAGE_BOOKS, DEFAULT_BOOK_IMG } from '../constants/common'
import { checkLibrarian } from '../helpers/Authorization'
import formatMoney from '../helpers/CurrencyFormatter'

class BookDetail extends Component {
	constructor(props) {
		super(props)

		this.state = { isLibrarian: true }

		this.renderBorrowingBooks = this.renderBorrowingBooks.bind(this)
		this.renderBorrowingBookPanel = this.renderBorrowingBookPanel.bind(this)
	}

	componentWillMount() {
		if (!checkLibrarian()) {
			this.setState({ isLibrarian: false })
			return
		}

		const bookId = this.props.params.id
		this.props.getBookDetail(bookId)
		this.props.getBorrowingCopies(bookId)
		this.props.switchStateNavBar(MANAGE_BOOKS)
	}

	render() {
		if (!this.state.isLibrarian) return <div />

		const { book, borrowingCopiesOfBook } = this.props

		if (!book) {
			return <div>Loading...</div>
		}

		const formattedPrice = formatMoney(book.price)
		const formattedCautionMoney = formatMoney(book.cautionMoney)

		return (
			<div className="book-detail">
				<a onClick={browserHistory.goBack} className="back">Back</a>
				<div className="panel panel-primary" style={{ width: "100%" }}>
					<div className="panel-heading">
						<h3 className="panel-title">Detail of {book.title}.</h3>
					</div>
					<div className="panel-body">
						<div className="col-md-6 col-sm-6">
							<p>
								<span>Authors: </span>
								{this.renderBookAuthors(book.bookAuthors)}
							</p>
							<p>Publisher: {book.publisher}</p>
							<p>Published Year: {book.publishYear}</p>
							<p>Number of Copies: {book.numberOfCopies}</p>
							<p>Number of Pages: {book.numberOfPages}</p>
							<p>Price: {formattedPrice}</p>
							<p className="caution-money">Caution Money: {formattedCautionMoney}</p>
							<p className="caution-day">Max number of late days: {book.bookType.lateDaysLimit}</p>
							<p>Type: {book.bookType.name}</p>
							<p>
								<span>Categories: </span>
								{this.renderCategories(book.bookCategories)}
							</p>
							<p>Position: shelf {book.bookPosition.shelf}, {book.bookPosition.floor}</p>
							<p>Description: {book.description}</p>
						</div>
						<div className="col-md-6 col-sm-6">
							<img className="book-img" src={book.thumbnail || DEFAULT_BOOK_IMG} alt="Book thumbnail" />
						</div>
					</div>
				</div>

				{this.renderBorrowingBookPanel(borrowingCopiesOfBook)}

			</div>
		)
	}

	renderBookAuthors(authors) {
		return authors.map((author, index) => {
			if (index < authors.length - 1) {
				return <span style={{fontSize: "inherit"}} key={author.id}>{author.authorName}, </span>
			}
			return <span style={{fontSize: "inherit"}} key={author.id}>{author.authorName}</span>
		})
	}

	renderCategories(categories) {
		return categories.map((category, index) => {
			if (index < categories.length - 1) {
				return <span style={{ fontSize: "inherit" }} key={category.id}>{category.categoryName}, </span>
			}
			return <span style={{ fontSize: "inherit" }} key={category.id}>{category.categoryName}</span>
		})
	}

	renderBorrowingBookPanel(borrowingBookCopies) {
		if (!borrowingBookCopies || borrowingBookCopies.length === 0) {
			return
		}
		return (
			<div className="panel panel-default" style={{ width: "100%" }}>
				<div className="panel-heading">
					<h3 className="panel-title">Borrowing Copies of this book:</h3>
				</div>
				<div className="panel-body">
					{this.renderBorrowingBooks(borrowingBookCopies)}
				</div>
			</div>
		)
	}

	renderBorrowingBooks(borrowingBookCopies) {
		return (
			<table className="table table-striped">
				<thead>
				<tr>
					<th>No.</th>
					<th>RFID</th>
					<th>Title</th>
					<th>Borrower</th>
					<th>Borrowed Date</th>
					<th>Dealine Date</th>
				</tr>
				</thead>
				<tbody>
				{borrowingBookCopies.map((borrowingBook, index) => this.renderBorrowingBook(borrowingBook, index))}
				</tbody>
			</table>
		)
	}

	renderBorrowingBook(borrowingBook, index) {
		const borrowedCopyRfid = borrowingBook.bookCopyRfid
		const userId = borrowingBook.accountUserId

		return (
			<tr key={borrowedCopyRfid}>
				<td>{index + 1}</td>
				<td>{borrowedCopyRfid}</td>
				<td>{borrowingBook.bookCopyBookTitle}</td>
				<td className="clickable" onClick={() => browserHistory.push(`/users/${userId}`)}>{userId}</td>
				<td>{borrowingBook.borrowedDate}</td>
				<td>{borrowingBook.deadlineDate}</td>
			</tr>
		)
	}
}

function mapStateToProps(state) {
	return {
		book: state.books.book,
		borrowingCopiesOfBook: state.books.borrowingCopiesOfBook
	}
}

export default connect(mapStateToProps, {
	getBookDetail,
	getBorrowingCopies,
	switchStateNavBar
})(BookDetail)
