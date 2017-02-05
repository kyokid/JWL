import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { getUserByUsername, getAllUsers } from '../actions';

class SearchBar extends Component {

	constructor(props) {
		super(props);

		this.state = { term: '' };
		this.onFormSubmit = this.onFormSubmit.bind(this);
		this.onInputChange = this.onInputChange.bind(this);
	}

	render() {
		return (
			<form className="input-group" onSubmit={this.onFormSubmit}>
				<input
					placeholder="put username here..."
					className="form-control"
					value={this.state.term}
					onChange={this.onInputChange}
				/>
				<span className="input-group-btn">
					<button className="btn secondary-btn" type="submit">Search</button>
				</span>
			</form>
		);
	}

	onFormSubmit(event) {
		event.preventDefault();

		this.setState({term: ''});

		const searchTerm = this.state.term;
		if (searchTerm.trim() == "") {
			this.props.getAllUsers();
			return;
		}
		this.props.getUserByUsername(searchTerm);
	}

	onInputChange(event) {
		this.setState({ term: event.target.value })
	}

}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ getUserByUsername, getAllUsers }, dispatch);
}

export default connect(null, mapDispatchToProps)(SearchBar);
