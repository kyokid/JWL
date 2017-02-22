import React from 'react'
import { FETCH_ACCOUNTS, FETCH_ACCOUNT, DELETE_ACCOUNT } from '../constants/action-type'

const INITIAL_STATE = {
	all: [],
	account: null
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case FETCH_ACCOUNTS:
			return { ...state, all: action.payload.data }
		case FETCH_ACCOUNT:
			return { ...state, account: action.payload.data.data }
		case DELETE_ACCOUNT:
			debugger
			let newAccount = state.account
			newAccount.borrowedBookCopies = action.payload.data.data
			return{ ...state, account: newAccount }
	}
	return state
}
