import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { getAllUsers } from '../actions';

class UserList extends Component {

	componentWillMount() {
		this.props.getAllUsers();
	}

	render() {
		return (
			<table className="table table-striped">
				<thead>
					<tr>
						<th>id</th>
						<th>name</th>
						<th>username</th>
						<th>email</th>
					</tr>
				</thead>
				<tbody>
					{this.props.users.map(this.renderUser)}
				</tbody>
			</table>
		);
	}

	renderUser(user) {
		const userId = user.userId;
		return (
			<tr key={userId}>
				{/*<th scope="row">{id}</th>*/}
				<td scope="row">{userId}</td>
				<td>{user.profileFullname}</td>
				<td>{user.profileEmail}</td>
			</tr>
		);
	}

}

function mapStateToProps({ users }) {
	return { users };
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getAllUsers }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(UserList);
