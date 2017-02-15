import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { getAllAccounts } from '../actions/AccountsAction'

class AccountList extends Component {
	componentWillMount() {
		this.props.getAllAccounts()
	}

	render() {
		return (
			<table className="table table-striped">
				<thead>
					<tr>
						<th>No.</th>
						<th>name</th>
						<th>username</th>
						<th>email</th>
						<th>Tools</th>
					</tr>
				</thead>
				<tbody>
					{this.props.accounts.map((account, index) => this.renderAccount(account, index))}
				</tbody>
			</table>
		)
	}

	renderAccount(account, index) {
		const userId = account.userId
		return (
			<tr key={userId}>
				<td>{index}</td>
				<td>{userId}</td>
				<td>{account.profileFullname}</td>
				<td>{account.profileEmail}</td>
				<td>
					<a href="#"><span className="glyphicon glyphicon-pencil" aria-hidden="true" /></a>
					<a href="#"><span className="glyphicon glyphicon-remove" aria-hidden="true" /></a>
				</td>
			</tr>
		)
	}
}

function mapStateToProps({ accounts }) {
	return { accounts }
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getAllAccounts }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(AccountList)
