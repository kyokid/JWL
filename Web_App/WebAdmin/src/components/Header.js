import React from 'react'

export default function () {
	return (
		<header className="navbar navbar-fixed-top navbar-inverse">
			<div className="container">
				<a id="logo" href="#">JWL</a>
				<nav>
					<ul className="nav navbar-nav navbar-left">
						<li className="active"><a href="#">Manage Accounts</a></li>
						<li><a href="#">Manage Books</a></li>
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
