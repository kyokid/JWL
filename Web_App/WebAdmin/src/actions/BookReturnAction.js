import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function fetchAddedReturnedCopyFromCart(returnedCopyData) {
	return {
		type: Types.FETCH_ADDED_RETURN_COPY,
		payload: returnedCopyData
	}
}

export function commitReturnCopies(librarianId) {
	const commitReturnUrl = `${Api.ROOT_URL}librarian/${librarianId}${Api.COMMIT_RETURN_COPIES_PATH}`
	const request = axios.get(commitReturnUrl)

	return {
		type: Types.COMMIT_RETURN_COPIES,
		payload: request
	}
}

export function fetchCommittedReturnCopies(returnedCopyData) {
	return {
		type: Types.FETCH_COMMITTED_RETURN_COPIES,
		payload: returnedCopyData
	}
}

export function cancelReturnCopies(librarianId) {
	const cancelReturnUrl = `${Api.ROOT_URL}librarian/${librarianId}${Api.CANCEL_RETURN_COPIES_PATH}`
	const request = axios.get(cancelReturnUrl)

	return {
		type: Types.FETCH_CANCEL_RETURN_COPIES,
		payload: request
	}
}

export function fetchCancelReturnCopies(canceledData) {
	return {
		type: Types.FETCH_CANCEL_RETURN_COPIES,
		payload: canceledData
	}
}
