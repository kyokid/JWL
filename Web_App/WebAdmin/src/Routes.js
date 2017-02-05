import React from 'react';
import { Route, IndexRoute } from 'react-router';

import App from './components/app';
import UserIndex from './components/UserIndex';

export default (
	<Route path="/" component={App}>
		<Route path="users" component={UserIndex} />
	</Route>
);
