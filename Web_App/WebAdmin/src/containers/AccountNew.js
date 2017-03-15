import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Field, Fields, reduxForm} from 'redux-form'
import { Link, browserHistory } from 'react-router'

import { renderCommonField, renderUserRoleRadioGroup } from '../helpers/FieldRenderer'
import { validate } from  '../helpers/FieldValidator'
import ReactDatePicker from '../helpers/ReactDatePicker'
import ImgField from '../helpers/ImgFieldRenderer'

import { createAccount } from '../actions/AccountsAction'

class AccountNew extends Component {
	constructor(props) {
		super(props)

		this.state = { dirty: false }

		this.onFormSubmit = this.onFormSubmit.bind(this)
		this.beforeUnload = this.beforeUnload.bind(this)
	}

	componentWillMount() {
		window.addEventListener("beforeunload", this.beforeUnload)

		this.props.router.setRouteLeaveHook(this.props.route, () => {
			if (this.state.dirty) {
				return 'You have unsaved information, are you sure you want to leave this page?'
			}
		})
	}

	componentWillReceiveProps(nextProps) {
		if (nextProps.dirty) {
			this.setState({ dirty: true })
		}
	}

	render() {
		return (
			<div className="account-new">
				<div className="panel panel-primary" style={{ width: "100%" }}>
					<div className="panel-heading">
						<h3 className="panel-title">Registration Form</h3>
					</div>
					<div className="panel-body">
						{this.renderForm()}
					</div>
				</div>
			</div>
		)
	}

	beforeUnload(event) {
		if (this.state.dirty) {
			event.returnValue = "before unload"
		}
	}

	renderForm() {
		const { handleSubmit } = this.props
		return (
			<form onSubmit={handleSubmit(this.onFormSubmit)}>
				<div className="col-md-6 col-sm-6">
					<Field id="userId"
								 className="col-md-12 col-sm-12"
								 name="userId"
								 label="User ID"
								 type="text"
								 helpBlock="Enter a unique User ID."
								 component={renderCommonField} />
					<Field id="password"
								 className="col-md-12 col-sm-12"
								 name="password"
								 label="Password"
								 type="password"
								 helpBlock="Enter a secure Password."
								 component={renderCommonField} />
					<Field id="confirmPassword"
								 className="col-md-12 col-sm-12"
								 name="confirmPassword"
								 label="Confirm Password"
								 type="password"
								 helpBlock="Confirm Password must match Password."
								 component={renderCommonField} />
				</div>
				<div className="col-md-1 col-sm-1"></div>
				<Field id="inputImg"
							 className="col-md-5 col-sm-5"
							 name="profileImgUrl"
							 formName="accountNewForm"
							 label="User Image"
							 component={ImgField} />
				<div className="col-md-12 col-sm-12">
					<Field id="fullName"
								 className="col-md-5 col-sm-5"
								 name="profileFullname"
								 label="Full Name"
								 type="text"
								 helpBlock="Enter the user's Full name."
								 component={renderCommonField} />
					<div className="col-md-1 col-sm-1"></div>
					<Field id="email"
								 className="col-md-5 col-sm-5"
								 name="profileEmail"
								 label="Email"
								 type="text"
								 helpBlock="Example Email address: new.user@mail.com."
								 component={renderCommonField} />
				</div>
				<div className="col-md-12 col-sm-12">
					<Field id="phoneNo"
								 className="col-md-5 col-sm-5"
								 name="profilePhoneNo"
								 label="Phone Number"
								 type="text"
								 helpBlock="Phone number can be like +841692536224 or 01692536224."
								 component={renderCommonField} />
					<div className="col-md-1 col-sm-1"></div>
					<Field id="address"
								 className="col-md-5 col-sm-5"
								 name="profileAddress"
								 label="Address"
								 type="text"
								 helpBlock="Enter the new user's Address."
								 component={renderCommonField} />
				</div>
				<div className="col-md-12 col-sm-12">
					<Field id="workPlace"
								 className="col-md-5 col-sm-5"
								 name="profilePlaceOfWork"
								 label="Place of Work"
								 type="text"
								 helpBlock="Enter a company or school name."
								 component={renderCommonField} />
					<div className="col-md-1 col-sm-1"></div>
					<Field id="birthDate"
								 className="col-md-5 col-sm-5"
								 name="profileDateOfBirth"
								 label="Date of Birth"
								 type="text"
								 helpBlock="Choose a Date of Birth."
								 component={ReactDatePicker} />
				</div>
				<div className="col-md-12 col-sm-12">
					<Fields id="userRole"
									className="col-md-6 col-sm-6"
									names={["userRoleId"]}
									labels={["Admin", "Librarian", "Borrower"]}
									values={["1", "2", "3"]}
									helpBlock="Choose the new user's Role in the library."
									component={renderUserRoleRadioGroup} />
				</div>

				<div className="col-md-12 col-sm-12">
					<div className="col-md-3 col-sm-3 pull-right submit-btn" >
						<input type="submit" className="btn btn-primary" value="Submit" />
					</div>
					<div className="col-md-3 col-sm-3 pull-right cancel-btn" >
						<Link className="btn btn-default" to="/">Cancel</Link>
					</div>
				</div>
			</form>
		)
	}

	onFormSubmit(props) {
		this.props.createAccount(props)
		window.removeEventListener("beforeunload", this.beforeUnload)
		this.props.router.setRouteLeaveHook(this.props.route, () => {
			return true
		})

		browserHistory.push("/")
	}
}

let accountNewForm = reduxForm({
	form: 'accountNewForm',
	destroyOnUnmount: true,
	validate
})(AccountNew)

export default connect(null, { createAccount })(accountNewForm)

