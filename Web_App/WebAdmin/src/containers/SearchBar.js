import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { getUserByUsername, getAllAccounts } from '../actions/AccountsAction'

class SearchBar extends Component {
	constructor(props) {
		super(props)

		this.state = { term: '' }
		this.onFormSubmit = this.onFormSubmit.bind(this)
		this.onInputChange = this.onInputChange.bind(this)
	}

	render() {
		return (
			<form className="input-group" onSubmit={this.onFormSubmit}>
				<input
					placeholder="put userID here..."
					className="form-control"
					value={this.state.term}
					onChange={this.onInputChange} />
				<span className="input-group-btn">
					<button className="btn secondary-btn" type="submit">Search</button>
				</span>
			</form>
		)
	}

	onFormSubmit(event) {
		event.preventDefault()

		let searchTerm = this.state.term.trim()

		if (searchTerm == "") {
			this.setState({term: ''})
			this.props.getAllAccounts()
			return
		}

		if (searchTerm.length > 15) {
			searchTerm = searchTerm.slice(0, 15)
			searchTerm += "..."
		}

		this.props.getUserByUsername(searchTerm)

		this.setState({term: ''})
	}

	onInputChange(event) {
		this.setState({ term: event.target.value })
	}
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getUserByUsername, getAllAccounts }, dispatch)
}

export default connect(null, mapDispatchToProps)(SearchBar)
