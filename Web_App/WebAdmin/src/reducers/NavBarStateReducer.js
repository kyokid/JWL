import * as path from '../constants/url-path'

const INITIAL_STATE = {
	activeManageAccounts: true,
	activeManageBooks: false,
	activeReturnBooks: false
}

export default function (state = INITIAL_STATE, action) {
	switch (action.type) {
		case path.ACCOUNT_LIST:
		case path.ACCOUNT_DETAIL:
		case path.ACCOUNT_NEW:
			return INITIAL_STATE

		case path.BOOKS_RETURN:
			return {
				activeManageAccounts: false,
				activeManageBooks: false,
				activeReturnBooks: true
			}

		case path.BOOK_LIST:
		case path.BOOK_DETAIL:
			return {
				activeManageAccounts: false,
				activeManageBooks: true,
				activeReturnBooks: false
			}
	}
	return state
}
