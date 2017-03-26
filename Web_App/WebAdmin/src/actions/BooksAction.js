import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function getAllBooks() {
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL}${Api.BOOKS}`
	})

	return {
		type: Types.FETCH_BOOKS,
		payload: request
	}
}

export function getBookDetail(bookId) {
	const request = axios.get(`${Api.ROOT_URL}${Api.BOOKS}/${bookId}`)

	return {
		type: Types.FETCH_BOOK,
		payload: request
	}
}

export function getBorrowingCopies(bookId) {
	const request = axios.get(`${Api.ROOT_URL}${Api.BOOKS}/${bookId}/${Api.BORROWING_COPIES_PATH}`)

	return {
		type: Types.FETCH_BORROWING_COPIES,
		payload: request
	}
}
