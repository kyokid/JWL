import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function initBorrow(userId, ibeaconId) {
	const request = axios.post(
		`${Api.ROOT_URL}/${Api.INIT_BORROW}`,
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
	const request = axios.post(`${Api.ROOT_URL}/${Api.CHECKOUT}`,
		{
			userId: userId,
			ibeaconId: ibeaconId
		}
	)

	return {
		type: Types.CHECKOUT,
		payload: request
	}
}

export function deleteBorrowedCopy(userId, borrowedCopyId) {
	const request = axios({
		method: 'DELETE',
		url: `${Api.ROOT_URL}${Api.DELETE_COPY}`,
		data: {
			"id": borrowedCopyId,
			"accountUserId": userId
		}
	})

	return {
		type: Types.DELETE_ACCOUNT,
		payload: request
	}
}
