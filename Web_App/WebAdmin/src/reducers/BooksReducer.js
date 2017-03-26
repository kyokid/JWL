import React from 'react'
import * as Types from '../constants/action-type'

const INITIAL_STATE = {
	all: [],
	book: null,
	borrowingCopiesOfBook: [],
	message: ""
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Types.FETCH_BOOKS:
			return {
				...state,
				all: action.payload.data.data,
				message: action.payload.data.textMessage
			}

		case Types.FETCH_BOOK:
			return { ...state, book: action.payload.data.data }

		case Types.FETCH_BORROWING_COPIES:
			return {
				...state,
				borrowingCopiesOfBook: action.payload.data.data
			}
	}

	return state
}
