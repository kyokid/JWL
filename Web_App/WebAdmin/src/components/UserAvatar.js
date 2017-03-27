import React from 'react'
import { DEFAULT_IMG } from '../constants/common'

export default function ({ imgUrl, userId, onClickLogout }) {
	return (
		<div className="profile-header-container">
			<div className="profile-header-img dropdown">
				<a className="dropdown-toggle"
					 data-toggle="dropdown"
					 href="#"
					 role="button"
					 aria-haspopup="true"
					 aria-expanded="false">
					<img className="img-circle" src={imgUrl || DEFAULT_IMG} />
					<span className="account-fullname">{userId}</span>
				</a>

				<ul className="dropdown-menu">
					<a onClick={() => onClickLogout()}>Logout</a>
				</ul>
			</div>
		</div>
	)
}
