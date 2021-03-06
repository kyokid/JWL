import React, { Component } from 'react'
import _ from 'lodash'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { browserHistory, Link } from "react-router"

import { getAllAccounts, getAllBorrowers, setActivate } from '../actions/AccountsAction'
import { switchStateNavBar } from '../actions/RouteAction'
import { MANAGE_ACCOUNTS, ROLE_ADMIN, ROLE_LIBRARIAN } from '../constants/common'

class AccountList extends Component {
	constructor(props) {
		super(props)

		this.renderActivateButtons = this.renderActivateButtons.bind(this)
	}

	componentWillMount() {
		const userRole = localStorage.userRole
		if (userRole === ROLE_ADMIN) {
			this.props.getAllAccounts()
		} else if (userRole === ROLE_LIBRARIAN) {
			this.props.getAllBorrowers()
		}

		this.props.switchStateNavBar(MANAGE_ACCOUNTS)
	}

	render() {
		if (!this.props.accounts) {
			return (
				<div style={{ marginTop: "100px" }}>
					<h2 style={{ fontSize: "35px" }}>{this.props.message}</h2>
				</div>
			)
		}

		let accounts = _.sortBy(this.props.accounts, ['userId'])

		return (
			<div>
				<Link className="account-new-btn" to="new/user">
					<span className="glyphicon glyphicon-plus" />
				</Link>
				<table className="table table-striped table-users">
					<thead>
						<tr>
							<th>No.</th>
							<th>UserID</th>
							<th>Full name</th>
							<th>Email</th>
							<th>Is in Library</th>
							<th>Is Activated</th>
							<th>Tools</th>
						</tr>
					</thead>
					<tbody>
						{accounts.map((account, index) => this.renderAccount(account, index))}
					</tbody>
				</table>
			</div>
		)
	}

	renderAccount(account, index) {
		const userId = account.userId
		return (
			<tr key={userId}>
				<td>{index + 1}</td>
				<td className="clickable" onClick={() => browserHistory.push(`users/${userId}`)}>{userId}</td>
				<td>{account.profileFullname}</td>
				<td>{account.profileEmail}</td>
				<td>{account.inLibrary? "True" : "False"}</td>
				<td>{this.renderActivateButtons(userId, account.activated)}</td>
				<td>
					<a href="#"><span className="glyphicon glyphicon-pencil" aria-hidden="true" /></a>
					<a href="#"><span className="glyphicon glyphicon-remove" aria-hidden="true" /></a>
				</td>
			</tr>
		)
	}

	renderActivateButtons(userId, isActivated) {
		const { setActivate } = this.props
		if (isActivated) {
			return (
				<a style={{ color: "green" }} onClick={() => setActivate(userId, false)}>
					<span className="glyphicon glyphicon-ok" aria-hidden="true" />
				</a>
			)
		}
		return (
			<a style={{ color: "gray" }} onClick={() => setActivate(userId, true)}>
				<span className="glyphicon glyphicon-remove" aria-hidden="true" />
			</a>
		)
	}
}

function mapStateToProps(state) {
	return {
		accounts: state.accounts.all,
		message: state.accounts.message
	}
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getAllAccounts, switchStateNavBar, getAllBorrowers, setActivate }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(AccountList)
