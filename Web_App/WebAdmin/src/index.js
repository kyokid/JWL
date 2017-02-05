import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { syncHistoryWithStore } from 'react-router-redux'

import { Router, browserHistory } from 'react-router'
import Routes from './Routes';
import Store from './Store';

ReactDOM.render(
  <Provider store={Store}>
    <Router routes={Routes} history={syncHistoryWithStore(browserHistory, Store)} />
  </Provider>
  , document.querySelector('.container'));
