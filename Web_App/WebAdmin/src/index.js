import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { syncHistoryWithStore } from 'react-router-redux'

import { Router, browserHistory } from 'react-router'
import Routes from './routes';
import Store from './store';

ReactDOM.render(
  <Provider store={Store}>
    <Router routes={Routes} history={syncHistoryWithStore(browserHistory, Store)} />
  </Provider>
  , document.querySelector('.container'));
