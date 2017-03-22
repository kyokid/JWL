import React, { Component } from 'react'
import { browserHistory } from 'react-router'
import { connect } from 'react-redux'
import { switchStateNavBar } from '../actions/RouteAction'
import * as path from '../constants/UrlPath'

class Header extends Component {
	constructor(props) {
		super(props)

		this.onClickActive = this.onClickActive.bind(this)

		this.state = {
			userId: ""
		}
	}

	render() {
		const { activeManageAccounts, activeManageBooks, activeReturnBooks } = this.props
		const { userId } = localStorage

		return (
			<header className="navbar navbar-fixed-top navbar-inverse">
				<div className="container">
					<a id="logo" href="#">Just Walk Out Library</a>
					<nav>
						<ul className="nav navbar-nav navbar-left">
							<li className={activeManageAccounts ? 'active' : ''}
									onClick={() => this.onClickActive(path.ACCOUNT_LIST)}>
								<a href="#">Manage Accounts</a>
							</li>
							<li className={activeManageBooks ? 'active' : ''}>
								<a href="#">Manage Books</a>
							</li>
							<li className={activeReturnBooks ? 'active' : ''}
									onClick={() => this.onClickActive(path.BOOKS_RETURN)}>
								<a href="#">Return Books</a>
							</li>
						</ul>
					</nav>
					<div className="profile-header-container">
						<div className="profile-header-img dropdown">
							<a className="dropdown-toggle"
								 data-toggle="dropdown"
								 href="#"
								 role="button"
								 aria-haspopup="true"
								 aria-expanded="false">
								<img className="img-circle" src="/bep.jpg" />
								<span className="account-fullname">{userId}</span>
							</a>

							<ul className="dropdown-menu">
								<a onClick={() => this.onClickLogout()}>Logout</a>
							</ul>
						</div>
					</div>
				</div>
			</header>
		)
	}

	onClickActive(pathName) {
		this.props.switchStateNavBar(pathName)
		browserHistory.push(pathName)
	}

	onClickLogout() {
		localStorage.userId = ""
		browserHistory.push(path.LOGIN)
	}
}

function mapStateToProps(state) {
	return {
		activeManageAccounts: state.navBarState.activeManageAccounts,
		activeManageBooks: state.navBarState.activeManageBooks,
		activeReturnBooks: state.navBarState.activeReturnBooks
	}
}

export default connect(mapStateToProps, { switchStateNavBar })(Header)
