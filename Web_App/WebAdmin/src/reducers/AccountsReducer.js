import React from 'react'
import { FETCH_ACCOUNTS, FETCH_ACCOUNT, DELETE_ACCOUNT, CHECKOUT } from '../constants/action-type'

const INITIAL_STATE = {
	all: [],
	account: null
}

export default function (state = INITIAL_STATE, action) {
	let newAccount = state.account

	switch (action.type) {
		case FETCH_ACCOUNTS:
			return { ...state, all: action.payload.data }
		case FETCH_ACCOUNT:
			return { ...state, account: action.payload.data.data }
		case DELETE_ACCOUNT:
			newAccount.borrowedBookCopies = action.payload.data.data
			return { ...state, account: newAccount }
		case CHECKOUT:
			newAccount.borrowedBookCopies = [...action.payload.data.data, ...newAccount.borrowedBookCopies]
			return { ...state, account: newAccount }
	}
	return state
}
