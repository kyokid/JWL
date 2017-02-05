import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'
import UsersReducer from './UsersReducer'

const rootReducer = combineReducers({
	routing: routerReducer,
	users: UsersReducer
});

export default rootReducer;
