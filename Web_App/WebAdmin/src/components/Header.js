import React, { Component } from 'react'
import { Link } from 'react-router'

const MANAGE_ACCOUNTS = "manage accounts"
const MANAGE_BOOKS = "manage books"
const RETURN_BOOKS = "return books"

export default class Header extends Component {
	constructor(props) {
		super(props)

		this.state = {
			activeManageAccounts: true,
			activeManageBooks: false,
			activeReturnBooks: false
		}

		this.onClickActive = this.onClickActive.bind(this)
	}
	render() {
		return (
			<header className="navbar navbar-fixed-top navbar-inverse">
				<div className="container">
					<a id="logo" href="#">Just Walk Out Library</a>
					<nav>
						<ul className="nav navbar-nav navbar-left">
							<li className={this.state.activeManageAccounts ? 'active' : ''}
									onClick={() => this.onClickActive(MANAGE_ACCOUNTS)}>
								<Link to="/users">Manage Accounts</Link>
							</li>
							<li className={this.state.activeManageBooks ? 'active' : ''}
									onClick={() => this.onClickActive(MANAGE_BOOKS)}>
								<a href="#">Manage Books</a>
							</li>
							<li className={this.state.activeReturnBooks ? 'active' : ''}
									onClick={() => this.onClickActive(RETURN_BOOKS)}>
								<Link to="/return/books">Return Books</Link>
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
								<span className="account-fullname">Bep</span>
							</a>

							<ul className="dropdown-menu">
								<a>Logout</a>
							</ul>
						</div>
					</div>
				</div>
			</header>
		)
	}

	onClickActive(tabName) {
		switch (tabName) {
			case MANAGE_ACCOUNTS:
				this.setState({
					activeManageAccounts: true,
					activeManageBooks: false,
					activeReturnBooks: false
				})
				return
			case MANAGE_BOOKS:
				this.setState({
					activeManageAccounts: false,
					activeManageBooks: true,
					activeReturnBooks: false
				})
				return
			case RETURN_BOOKS:
				this.setState({
					activeManageAccounts: false,
					activeManageBooks: false,
					activeReturnBooks: true
				})
				return
		}
	}
}
