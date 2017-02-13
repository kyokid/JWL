import axios from 'axios';
import * as Api from '../constants/api';
import * as Types from '../constants/action-type';

export function getAllUsers() {
	// const request = axios.get(`${Api.ROOT_URL}${Api.USERS}`);
	const request = axios({
		method: 'GET',
		url: `${Api.ROOT_URL}${Api.USERS}`
		// Headers can be added here
	});

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
