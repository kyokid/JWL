import React from 'react'
import { Route, IndexRoute } from 'react-router'
import * as path from './constants/url-path'

import App from './components/app'
import AccountIndex from './components/AccountsIndex'
import AccountDetail from './containers/AccountDetail'
import AccountNew from './containers/AccountNew'
import BooksReturn from './containers/BooksReturn'
import Login from './containers/Login'
import BookList from './containers/BookList'
import BookDetail from './containers/BookDetail'

export default (
	<div>
		<Route path="/" component={App}>
			<IndexRoute component={AccountIndex} />
			<Route path={path.ACCOUNT_LIST} component={AccountIndex} />
			<Route path={path.ACCOUNT_DETAIL} component={AccountDetail} />
			<Route path={path.ACCOUNT_NEW} component={AccountNew} />
			<Route path={path.BOOKS_RETURN} component={BooksReturn} />
			<Route path={path.BOOK_LIST} component={BookList} />
			<Route path={path.BOOK_DETAIL} component={BookDetail} />
		</Route>
		<Route path={path.LOGIN} component={Login} />
	</div>

)
