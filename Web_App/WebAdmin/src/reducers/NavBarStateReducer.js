import { MANAGE_BOOKS, MANAGE_ACCOUNTS, RETURN_BOOKS } from '../constants/common'

const INITIAL_STATE = {
	activeManageAccounts: true,
	activeManageBooks: false,
	activeReturnBooks: false
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case MANAGE_ACCOUNTS:
			return INITIAL_STATE

		case RETURN_BOOKS:
			return {
				activeManageAccounts: false,
				activeManageBooks: false,
				activeReturnBooks: true
			}

		case MANAGE_BOOKS:
			return {
				activeManageAccounts: false,
				activeManageBooks: true,
				activeReturnBooks: false
			}
	}
	return state
}
