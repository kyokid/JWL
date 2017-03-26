import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import { reducer as formReducer } from 'redux-form'

import AccountsReducer from './AccountsReducer'
import BooksReturnReducer from './BooksReturnReducer'
import NavBarStateReducer from './NavBarStateReducer'
import BooksReducer from './BooksReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	form: formReducer,
	accounts: AccountsReducer,
	booksReturnData: BooksReturnReducer,
	navBarState: NavBarStateReducer,
	books: BooksReducer
})

export default rootReducer
