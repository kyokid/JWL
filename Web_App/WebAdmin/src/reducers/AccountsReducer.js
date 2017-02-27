import React from 'react'
import * as Types from '../constants/action-type'

const INITIAL_STATE = {
	all: [],
	account: null
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Types.FETCH_ACCOUNTS:
			return { ...state, all: action.payload.data }
		case Types.FETCH_ACCOUNT:
			return { ...state, account: action.payload.data.data }
		case Types.DELETE_ACCOUNT:
			return {
				...state,
				account: {
					...state.account,
					borrowedBookCopies: action.payload.data.data
				}
			}
		case Types.FETCH_SAVED_COPY:
			console.log("reducer Types.FETCH_SAVED_COPY called!")
			console.log("reducer: " + action.payload.data)
			if (!action.payload.data) {
				return state;
			}

			return {
				...state,
				account: {
					...state.account,
					borrowedBookCopies: [action.payload.data ,...state.account.borrowedBookCopies]
				}
			}
	}
	return state
}
