import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import { reducer as formReducer } from 'redux-form'

import AccountsReducer from './AccountsReducer'
import BooksReturnReducer from './BooksReturnReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	form: formReducer,
	accounts: AccountsReducer,
	booksReturnData: BooksReturnReducer
})

export default rootReducer
