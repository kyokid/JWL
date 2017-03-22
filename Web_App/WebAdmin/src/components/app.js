import React, { Component } from 'react'
import { browserHistory } from 'react-router'

import * as path from '../constants/UrlPath'
import Header from './Header'
import isLoggedIn from '../helpers/Authentication'

export default class App extends Component {
	componentWillMount() {
		if (!isLoggedIn()) {
			browserHistory.push(path.LOGIN)
		}
	}

  render() {
    return (
      <div>
        <Header />
        {this.props.children}
      </div>
    )
  }
}
