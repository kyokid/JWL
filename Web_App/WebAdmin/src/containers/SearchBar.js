import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { getUserByUsername, getAllAccounts, getBorrowerByUsername, getAllBorrowers } from '../actions/AccountsAction'
import { ROLE_ADMIN } from '../constants/common'

class SearchBar extends Component {
	constructor(props) {
		super(props)

		this.state = { term: '' }
		this.onFormSubmit = this.onFormSubmit.bind(this)
		this.onInputChange = this.onInputChange.bind(this)
	}

	render() {
		return (
			<form className="form-inline search-box" onSubmit={this.onFormSubmit}>
				<div className="form-group pull-right" style={{ textAlign: "left" }}>
					<input
						className="form-control"
						value={this.state.term}
						onChange={this.onInputChange} />

					<button className="btn secondary-btn" type="submit">Search</button>

					<p className="help-block" style={{ marginLeft: "10px" }}>Enter user ID.</p>
				</div>

			</form>
		)
	}

	onFormSubmit(event) {
		event.preventDefault()

		let searchTerm = this.state.term.trim()
		let userRole = localStorage.userRole

		if (searchTerm === "") {
			this.setState({term: ''})
			if (userRole === ROLE_ADMIN) {
				this.props.getAllAccounts()
			} else {
				this.props.getAllBorrowers()
			}
			return
		}

		if (searchTerm.length > 15) {
			searchTerm = searchTerm.slice(0, 15)
			searchTerm += "..."
		}

		if (userRole === ROLE_ADMIN) {
			this.props.getUserByUsername(searchTerm)
		} else {
			this.props.getBorrowerByUsername(searchTerm)
		}

		this.setState({term: ''})
	}

	onInputChange(event) {
		this.setState({ term: event.target.value })
	}
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({
		getUserByUsername,
		getAllAccounts,
		getBorrowerByUsername,
		getAllBorrowers
	}, dispatch)
}

export default connect(null, mapDispatchToProps)(SearchBar)
