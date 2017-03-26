import React, { Component } from "react"
import { connect } from "react-redux"
import { Link } from "react-router"

import { getBookDetail } from "../actions/BooksAction"
import { switchStateNavBar } from '../actions/RouteAction'
import { BOOK_LIST } from '../constants/url-path'

class BookDetail extends Component {
	constructor(props) {
		super(props)

		this.renderBorrowedBooks = this.renderBorrowedBooks.bind(this)
		this.renderBorrowedBookPanel = this.renderBorrowedBookPanel.bind(this)
	}

	componentWillMount() {
		this.props.getBookDetail(this.props.params.id)
		this.props.switchStateNavBar(this.props.route.path)
	}

	render() {
		const { book } = this.props

		if (!book) {
			return <div>Loading...</div>
		}

		return (
			<div className="book-detail">
				<Link to={BOOK_LIST} className="back">Back</Link>
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
							<p>Price: {book.price}</p>
							<p>Type: {book.bookType.name}</p>
							<p>
								<span>Categories: </span>
								{this.renderCategories(book.bookCategories)}
							</p>
							<p>Position: shelf {book.bookPosition.shelf}, {book.bookPosition.floor}</p>
							<p>Description: {book.description}</p>
						</div>
						<div className="col-md-6 col-sm-6">
							<img className="book-img" src={book.thumbnail} alt="Book thumbnail" />
						</div>
					</div>
				</div>

				{/*{this.renderBorrowedBookPanel(userId, book.borrowedBookCopies)}*/}

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
					<th>RFID</th>
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
				<td>{borrowedCopyRfid}</td>
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
}

function mapStateToProps(state) {
	return { book: state.books.book }
}

export default connect(mapStateToProps, { getBookDetail, switchStateNavBar })(BookDetail)
