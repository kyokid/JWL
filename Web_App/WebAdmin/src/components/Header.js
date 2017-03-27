import React, { Component } from 'react'
import { browserHistory } from 'react-router'
import { connect } from 'react-redux'
import { switchStateNavBar } from '../actions/RouteAction'
import * as path from '../constants/url-path'
import * as constant from '../constants/common'
import UserAvatar from '../components/UserAvatar'

class Header extends Component {
	constructor(props) {
		super(props)

		this.onClickActive = this.onClickActive.bind(this)
	}

	render() {
		const { activeManageAccounts, activeManageBooks, activeReturnBooks } = this.props
		const { userId, userRole } = localStorage
		
		if (userRole === constant.ROLE_ADMIN) {
			return (
				<header className="navbar navbar-fixed-top navbar-inverse">
					<div className="container">
						<a id="logo" href="#">Just Walk Out Library</a>
						<nav>
							<ul className="nav navbar-nav navbar-left">
								<li className={activeManageAccounts ? 'active' : ''}
										onClick={() => this.onClickActive(constant.MANAGE_ACCOUNTS, path.ACCOUNT_LIST)}>
									<a href="#">Manage Accounts</a>
								</li>
							</ul>
						</nav>
						<UserAvatar userId={userId}
												userRole={userRole}
												onClickLogout={this.onClickLogout} />
					</div>
				</header>
			)
		}

		return (
			<header className="navbar navbar-fixed-top navbar-inverse">
				<div className="container">
					<a id="logo" href="#">Just Walk Out Library</a>
					<nav>
						<ul className="nav navbar-nav navbar-left">
							<li className={activeManageAccounts ? 'active' : ''}
									onClick={() => this.onClickActive(constant.MANAGE_ACCOUNTS, path.ACCOUNT_LIST)}>
								<a href="#">Manage Borrowers</a>
							</li>
							<li className={activeManageBooks ? 'active' : ''}
									onClick={() => this.onClickActive(constant.MANAGE_BOOKS, path.BOOK_LIST)}>
								<a href="#">Manage Books</a>
							</li>
							<li className={activeReturnBooks ? 'active' : ''}
									onClick={() => this.onClickActive(constant.RETURN_BOOKS, path.BOOKS_RETURN)}>
								<a href="#">Return Books</a>
							</li>
						</ul>
					</nav>
					<UserAvatar userId={userId}
											userRole={userRole}
											onClickLogout={this.onClickLogout} />
				</div>
			</header>
		)
	}

	onClickActive(status, pathName) {
		this.props.switchStateNavBar(status)
		browserHistory.push(pathName)
	}

	onClickLogout() {
		localStorage.userId = ""
		localStorage.userRole = ""
		localStorage.imgUrl = ""
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
