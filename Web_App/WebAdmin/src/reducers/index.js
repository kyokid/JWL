import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import { reducer as formReducer } from 'redux-form'

import AccountsReducer from './AccountsReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	form: formReducer,
	accounts: AccountsReducer
})

export default rootReducer
