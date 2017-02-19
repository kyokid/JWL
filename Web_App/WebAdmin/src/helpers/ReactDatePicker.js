import React, { Component } from 'react'
import DatePicker from 'react-datepicker'
import moment from 'moment'

export default class ReactDatePicker extends Component {
	constructor(props) {
		super(props)

		this.state = {
			startDate: moment()
		}
	}

	handleChange(date) {
		this.setState({
			startDate: date
		})
	}

	render() {
		const {input} = this.props

		return (
			<div className="form-group">
				<DatePicker {...input}
										className="form-control"
										dateFormat="DD/MM/YYYY"
										selected={this.state.startDate}
										onChange={this.handleChange.bind(this)} readOnly />
				<div className="input-group-addon">
					<span className="glyphicon glyphicon-th" />
				</div>
			</div>
		)
	}
}
