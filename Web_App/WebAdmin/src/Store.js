import reducers from './reducers'
import reduxThunk from 'redux-thunk'
import ReduxPromise from 'redux-promise'
import { createStore, applyMiddleware } from 'redux'

const storeWithMiddleware = applyMiddleware(ReduxPromise)(createStore)
const store = storeWithMiddleware(reducers)

export default store;
