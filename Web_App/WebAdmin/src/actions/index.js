import axios from 'axios';
import * as Api from '../constants/Api';
import * as Types from '../constants/Types';

export function getAllUsers() {
	const request = axios.get(`${Api.ROOT_URL}${Api.USERS}`);

	return {
		type: Types.FETCH_USERS,
		payload: request
	};
}

export function getUserByUsername(term) {
	const request = axios.get(`${Api.ROOT_URL}${Api.USERS}?username=${term}`);

	return {
		type: Types.FETCH_USERS,
		payload: request
	}
}
