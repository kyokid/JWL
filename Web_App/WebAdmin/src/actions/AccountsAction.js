import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export function getAllAccounts() {
	// const request = axios.get(`${Api.ROOT_URL}${Api.USERS}`);
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL_LOCAL}${Api.USERS}`
		// Headers can be added here
	})

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function getUserByUsername(term) {
	const request = axios.get(`${Api.ROOT_URL}${Api.USERS}?username=${term}`);

	return {
		type: Types.FETCH_ACCOUNTS,
		payload: request
	}
}

export function submitImg() {
	const fileInput = document.getElementById("inputImg")
	const formData = new FormData()
	formData.append("img", fileInput.files[0])

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
