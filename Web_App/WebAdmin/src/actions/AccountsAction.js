import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function getAllAccounts() {
	// const request = axios.get(`${Api.ROOT_URL}${Api.USERS}`);
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL}${Api.USERS}`
	})

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function getAllBorrowers() {
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL}${Api.BORROWERS}`
	})

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function getAccountDetail(id) {
	const request = axios.get(`${Api.ROOT_URL}${Api.USERS}/${id}`)

	return {
		type: Types.FETCH_ACCOUNT,
		payload: request
	}
}

export function getUserByUsername(term) {
	const request = axios.get(`${Api.ROOT_URL}${Api.USERS}/search?term=${term}`)

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function getBorrowerByUsername(term) {
	const request = axios.get(`${Api.ROOT_URL}${Api.BORROWERS}/search?term=${term}`)

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function submitImg(imgFile) {
	const formData = new FormData()
	formData.append("img", imgFile)

	const request = axios({
		method: 'POST',
		url: Api.IMG_UPLOAD,
		data: formData
	})

	return {
		type: Types.ACC_IMG_URL,
		payload: request
	}
}

export function createAccount(newAccData) {
	const request = axios({
		method: 'POST',
		url: `${Api.ROOT_URL}${Api.USERS}`,
		data: newAccData
	})

	return {
		type: Types.CREATE_ACCOUNT,
		payload: request
	}
}

export function updateTotalBalance(userId, newTotalBalance) {
	const request = axios({
		method: 'POST',
		url: `${Api.ROOT_URL}${Api.UPDATE_TOTAL_BALANCE}`,
		data: {
			"userId": userId,
			"totalBalance": newTotalBalance
		}
	})

	return {
		type: Types.FETCH_TOTAL_BALANCE,
		payload: request
	}
}

export function setActivate(userId, isActivated) {
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL}${Api.USERS}/${userId}/${Api.SET_ACTIVATE_PATH}/${isActivated}`
	})

	return {
		type: Types.FETCH_ACTIVATED,
		payload: request,
		meta: { userId: userId }
	}
}
