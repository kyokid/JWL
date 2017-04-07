import React from 'react'
import * as Types from '../constants/action-type'

const INITIAL_STATE = {
	all: [],
	account: null,
	message: ""
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case Types.FETCH_ACCOUNTS:
			return {
				...state,
				all: action.payload.data.data,
				message: action.payload.data.textMessage
			}

		case Types.FETCH_ACCOUNT:
			return { ...state, account: action.payload.data.data }

		case Types.CREATE_ACCOUNT:
			if (!action.payload.data) {
				return state
			}
			if (!action.payload.data.data) {
				return {
					...state,
					message: action.payload.data.textMessage
				}
			}
			return {
				...state,
				all: [action.payload.data.data, ...state.all],
				message: action.payload.data.textMessage
			}

		case Types.DELETE_ACCOUNT_COPY:
			return {
				...state,
				account: {
					...state.account,
					usableBalance: state.account.usableBalance + action.meta.deletedCopyCautionMoney,
					borrowedBookCopies: action.payload.data.data
				}
			}

		case Types.FETCH_ADDED_COPY:
			console.log("reducer Types.FETCH_ADDED_COPY called!")
			console.log("reducer: " + action.payload.data)
			if (!action.payload.data) {
				return state
			}
			return {
				...state,
				account: {
					...state.account,
					usableBalance: state.account.usableBalance - action.payload.data.cautionMoney,
					borrowedBookCopies: [action.payload.data, ...state.account.borrowedBookCopies]
				}
			}

		case Types.CANCEL_ADDING_COPY:
			if (!action.payload) {
				return state
			}
			return { ...state, account: action.payload }

		case Types.CHECKOUT:
			if (!action.payload) {
				return state
			}

			let borrowedBookCopies = [...state.account.borrowedBookCopies]
			let updatedBorrowedBookCopies = action.payload.data.data
			borrowedBookCopies.splice(0, updatedBorrowedBookCopies.length)
			return {
				...state,
				account: {
					...state.account,
					borrowedBookCopies: [...updatedBorrowedBookCopies, ...borrowedBookCopies]
				}
			}

		case Types.FETCH_TOTAL_BALANCE:
			if (!action.payload) {
				return state
			}

			return {
				...state,
				account: {
					...state.account,
					totalBalance: action.payload.data.data.totalBalance,
					usableBalance: action.payload.data.data.usableBalance
				}
			}

		case Types.FETCH_ACTIVATED:
			if (!action.payload) {
				return state
			}

			return {
				...state,
				all: state.all.map(account =>
					account.userId === action.meta.userId ?
						{ ...account, activated: action.payload.data.data } : account)
			}
	}
	return state
}
