import React, { Component } from 'react'
import DatePicker from 'react-datepicker'
import moment from 'moment'

export default class ReactDatePicker extends Component {
	constructor(props) {
		super(props)

		this.state = {
			startDate: ''
		}
	}

	handleChange(date) {
		this.setState({
			startDate: date
		})
	}

	render() {
		const { input, id, label, meta: { touched, error } } = this.props

		return (
			<div className={`form-group ${touched && error ? 'has-error' : ''}`}>
				<label htmlFor={id}>{label}</label>
				<br />
				<DatePicker {...input}
										className="form-control"
										dateFormat="DD/MM/YYYY"
										placeholderText="DD/MM/YYYY"
										selected={this.state.startDate}
										onChange={this.handleChange.bind(this)} readOnly />
				{touched && error && <span className="help-block">{error}</span>}
			</div>
		)
	}
}
