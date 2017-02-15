import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import AccountsReducer from './AccountsReducer'
import ImgsReducer from './ImgsReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	accounts: AccountsReducer,
	img: ImgsReducer
})

export default rootReducer
