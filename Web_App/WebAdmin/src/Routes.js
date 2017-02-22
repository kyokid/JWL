import React from 'react'
import { Route, IndexRoute } from 'react-router'

import App from './components/app'
import AccountIndex from './components/AccountsIndex'
import AccountDetail from './containers/AccountDetail'

export default (
	<Route path="/" component={App}>
		<IndexRoute component={AccountIndex} />
		<Route path="users/:id" component={AccountDetail} />
	</Route>
)
