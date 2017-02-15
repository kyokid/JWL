import React from 'react'
import { Route, IndexRoute } from 'react-router'

import App from './components/app'
import UserIndex from './components/AccountsIndex'

export default (
	<Route path="/" component={App}>
		<IndexRoute component={UserIndex} />
	</Route>
)
