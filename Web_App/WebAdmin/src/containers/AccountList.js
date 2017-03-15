import React, { Component } from 'react'
import _ from 'lodash'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { browserHistory, Link } from "react-router"
import { getAllAccounts } from '../actions/AccountsAction'

class AccountList extends Component {
	constructor(props) {
		super(props)
	}

	componentWillMount() {
		this.props.getAllAccounts()
	}

	render() {
		if (!this.props.accounts) {
			return (
				<div>

					<h2>{this.props.message}</h2>
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

	renderAccount(account) {
		const userId = account.userId
		return (
			<tr key={userId} onClick={() => browserHistory.push(`users/${userId}`)}>
				<td>{userId}</td>
				<td>{account.profileFullname}</td>
				<td>{account.profileEmail}</td>
				<td>{account.inLibrary? "True" : "False"}</td>
				<td>{account.activated? "True" : "False"}</td>
				<td>
					<a href="#"><span className="glyphicon glyphicon-remove" aria-hidden="true" /></a>
				</td>
			</tr>
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
	return bindActionCreators({ getAllAccounts }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(AccountList)
