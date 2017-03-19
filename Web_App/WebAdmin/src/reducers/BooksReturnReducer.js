import React from 'react'
import * as Types from '../constants/action-type'

const INITIAL_STATE = {
	userId: "",
	returnedBooks: [],
	returnedBookOfAnotherUser: {},
	responseMsg: "",
	responseCode: ""
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Types.FETCH_ADDED_RETURN_COPY:
			debugger
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
				userId: action.payload.data.accountUserId,
				returnedBooks: [action.payload.data ,...state.returnedBooks],
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
				...INITIAL_STATE,
				responseMsg: action.payload.textMessage,
				responseCode: action.payload.code
			}
	}
	return state
}
