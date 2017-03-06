import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function initBorrow(userId, ibeaconId) {
	const request = axios.post(
		`${Api.ROOT_URL}/${Api.INIT_BORROW_BY_LIBRARIAN}`,
		{
			userId: userId,
			ibeaconId: ibeaconId
		}
	)

	return {
		type: Types.INIT_BORROW,
		payload: request
	}
}

export function checkout(userId, ibeaconId) {
	axios.post(`${Api.ROOT_URL}/${Api.CHECKOUT}`,
		{
			userId: userId,
			ibeaconId: ibeaconId
		}
	)

	return {
		type: Types.CHECKOUT
	}
}

export function deleteBorrowedCopy(userId, borrowedCopyRfid) {
	const request = axios({
		method: 'DELETE',
		url: `${Api.ROOT_URL}${Api.DELETE_COPY}`,
		data: {
			"bookCopyRfid": borrowedCopyRfid,
			"accountUserId": userId
		}
	})

	return {
		type: Types.DELETE_ACCOUNT,
		payload: request
	}
}

export function fetchCopyFromCart(borrowedCopyData) {
	console.log("fetchSaveBorrowedCopy called!!!")
	console.log("action: " + borrowedCopyData)
	return {
		type: Types.FETCH_ADDED_COPY,
		payload: borrowedCopyData
	}
}

export function cancelAddingCopies(userId, ibeaconId, stateBeforeAdd) {
	const request = axios.post(
		`${Api.ROOT_URL}/${Api.CANCEL_ADDING_COPIES}`,
		{
			userId: userId,
			ibeaconId: ibeaconId
		}
	)

	return {
		type: Types.CANCEL_ADDING_COPY,
		payload: stateBeforeAdd
	}
}
