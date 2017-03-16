import React, { Component } from 'react'
import DatePicker from 'react-datepicker'
import moment from 'moment'

export default class ReactDatePicker extends Component {
	constructor(props) {
		super(props)

		this.state = {
			startDate: moment("1990-01-01")
		}
	}

	handleChange(date) {
		this.setState({
			startDate: date
		})
	}

	render() {
		const { input, id, className, label, helpBlock, meta: { touched, error } } = this.props

		return (
			<div className={`form-group ${className ? className : ''} ${touched && error ? 'has-error' : ''}`}>
				<label htmlFor={id}>{label}</label>
				<br />
				<DatePicker {...input}
										className="form-control"
										dateFormat="YYYY-MM-DD"
										placeholderText="YYYY-MM-DD"
										showMonthDropdown
										showYearDropdown
										dropdownMode="select"
										selected={this.state.startDate}
										onChange={this.handleChange.bind(this)}
										readOnly />
				{touched && error ? <span className="help-block">{error}</span> : <p className="help-block">{helpBlock}</p>}
			</div>
		)
	}
}
