import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import { reducer as formReducer } from 'redux-form'

import AccountsReducer from './AccountsReducer'
import BooksReturnReducer from './BooksReturnReducer'
import NavBarStateReducer from './NavBarStateReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	form: formReducer,
	accounts: AccountsReducer,
	booksReturnData: BooksReturnReducer,
	navBarState: NavBarStateReducer
})

export default rootReducer
