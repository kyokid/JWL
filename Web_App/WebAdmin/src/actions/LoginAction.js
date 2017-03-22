import axios from 'axios'
import * as Api from '../constants/api'
import * as Types from '../constants/action-type'

export default function (loginData) {
	const loginUrl = `${Api.ROOT_URL}${Api.LOGIN}`
	const request = axios.post(loginUrl, loginData)

	return {
		type: Types.LOGIN,
		payload: request
	}
}