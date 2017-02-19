import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import { reducer as formReducer } from 'redux-form'

import AccountsReducer from './AccountsReducer'
import ImgsReducer from './ImgsReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	form: formReducer,
	accounts: AccountsReducer,
	imgData: ImgsReducer
})

export default rootReducer
