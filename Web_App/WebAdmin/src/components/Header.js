import React, { Component } from 'react'
import { Link } from 'react-router'

export default class Header extends Component {
	render() {
		return (
			<header className="navbar navbar-fixed-top navbar-inverse">
				<div className="container">
					<a id="logo" href="#">Just Walk Out Library</a>
					<nav>
						<ul className="nav navbar-nav navbar-left">
							<li className="active"><Link to="/users">Manage Accounts</Link></li>
							<li><a href="#">Manage Books</a></li>
							<li className><Link to="/return/books">Return Books</Link></li>
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

}
