import React from 'react'
import * as Types from '../constants/action-type'

const INITIAL_STATE = {
	returningBooks: [],
	returnedBooks: [],
	returnedBookOfAnotherUser: {},
	responseMsg: '',
	responseCode: ''
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Types.FETCH_ADDED_RETURN_COPY:
			console.log("reducer Types.FETCH_ADDED_RETURN_COPY called!")
			console.log("reducer: " + action.payload.data)
			if (!action.payload.data) {
				return {
					...state,
					responseMsg: action.payload.textMessage,
					responseCode: action.payload.code
				}
			}
			if (action.payload.code == "405") {
				return {
					...state,
					returnedBookOfAnotherUser: action.payload.data,
					responseMsg: action.payload.textMessage,
					responseCode: action.payload.code
				}
			}
			return {
				...state,
				returningBooks: [action.payload.data, ...state.returningBooks],
				responseMsg: action.payload.textMessage,
				responseCode: action.payload.code
			}

		case Types.FETCH_COMMITTED_RETURN_COPIES:
			if (action.payload.code != "200" || !action.payload.data) {
				return {
					...state,
					responseMsg: action.payload.textMessage,
					responseCode: action.payload.code
				}
			}
			return {
				...state,
				returningBooks: [],
				returnedBooks: action.payload.data,
				responseMsg: action.payload.textMessage,
				responseCode: action.payload.code
			}

		case Types.FETCH_CANCEL_RETURN_COPIES:
			if (action.payload.code != "200") {
				return {
					...state,
					responseMsg: action.payload.textMessage,
					responseCode: action.payload.code
				}
			}
			return {
				...state,
				returningBooks: [],
				responseMsg: action.payload.textMessage,
				responseCode: action.payload.code
			}
	}
	return state
}
