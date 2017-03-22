import React from 'react'
import { Route, IndexRoute } from 'react-router'
import * as path from './constants/UrlPath'

import App from './components/app'
import AccountIndex from './components/AccountsIndex'
import AccountDetail from './containers/AccountDetail'
import AccountNew from './containers/AccountNew'
import BooksReturn from './containers/BooksReturn'

export default (
	<Route path="/" component={App}>
		<IndexRoute component={AccountIndex} />
		<Route path={path.ACCOUNT_LIST} component={AccountIndex} />
		<Route path={path.ACCOUNT_DETAIL} component={AccountDetail} />
		<Route path={path.ACCOUNT_NEW} component={AccountNew} />
		<Route path={path.BOOKS_RETURN} component={BooksReturn} />
	</Route>
)
