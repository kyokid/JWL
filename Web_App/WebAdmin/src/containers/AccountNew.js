import React, { Component } from 'react'
import { connect } from 'react-redux'
import {Field, Fields, reduxForm} from 'redux-form'

import { renderCommonField, renderUserRoleRadioGroup } from '../helpers/FieldRenderer'
import { validate } from  '../helpers/FieldValidator'
import ReactDatePicker from '../helpers/ReactDatePicker'
import ImgField from '../helpers/ImgFieldRenderer'

import { createAccount } from '../actions/AccountsAction'

class AccountNew extends Component {
	constructor(props) {
		super(props)

		this.onFormSubmit = this.onFormSubmit.bind(this)
	}

	render() {
		return (
			<div className="account-new">
				{this.renderButtonTrigger()}
				{this.renderModal()}
			</div>
		)
	}

	renderButtonTrigger() {
		return (
			<a className="account-new-btn" href="#">
				<span className="glyphicon glyphicon-plus"
							aria-hidden="true"
							data-toggle="modal"
							data-target="#accountNewModal" />
			</a>
		)
	}

	renderModal() {
		return (
			<div className="modal fade"
					 id="accountNewModal"
					 data-backdrop="static"
					 data-keyboard="false"
					 tabIndex="-1"
					 role="dialog"
					 aria-labelledby="accountNewModalLabel"
					 aria-hidden="true">
				<div className="modal-dialog" role="document">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button"
											className="close"
											data-dismiss="modal"
											aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 className="modal-title" id="accountNewModalLabel">New Account</h4>
						</div>
						<div className="modal-body">
							{this.renderForm()}
						</div>
					</div>
				</div>
			</div>
		)
	}

	renderForm() {
		const { handleSubmit, reset } = this.props

		return (
			<form onSubmit={handleSubmit(this.onFormSubmit)}>
				<Field id="inputImg"
							 name="profileImgUrl"
							 formName="accountNewForm"
							 label="User Image"
							 component={ImgField} />
				<Field id="userId"
							 name="userId"
							 label="User ID"
							 type="text"
							 component={renderCommonField} />
				<Field id="fullName"
							 name="profileFullname"
							 label="Full Name"
							 type="text"
							 component={renderCommonField} />
				<Field id="password"
							 name="password"
							 label="Password"
							 type="password"
							 component={renderCommonField} />
				<Field id="confirmPassword"
							 name="confirmPassword"
							 label="Confirm Password"
							 type="password"
							 component={renderCommonField} />
				<Field id="email"
							 name="profileEmail"
							 label="Email"
							 type="text"
							 component={renderCommonField} />
				<Field id="address"
							 name="profileAddress"
							 label="Address"
							 type="text"
							 component={renderCommonField} />
				<Field id="birthDate"
							 className="date"
							 name="profileDateOfBirth"
							 label="Date of Birth"
							 type="text"
							 component={ReactDatePicker} />
				<Field id="phoneNo"
							 name="profilePhoneNo"
							 label="Phone Number"
							 type="text"
							 component={renderCommonField} />
				<Field id="workPlace"
							 name="profilePlaceOfWork"
							 label="Place of Work"
							 type="text"
							 component={renderCommonField} />
				<Fields id="userRole"
							 	names={["userRoleId"]}
							 	labels={["Admin", "Librarian", "Borrower"]}
							 	values={["1", "2", "3"]}
							 	component={renderUserRoleRadioGroup} />

				<button type="reset" className="btn btn-default" data-dismiss="modal" onClick={reset}>Close</button>
				<button type="submit" className="btn btn-primary">Submit</button>
			</form>
		)
	}

	onFormSubmit(props) {
		console.log(props)
		this.props.createAccount(props)
	}
}

let accountNewForm = reduxForm({
	form: 'accountNewForm',
	destroyOnUnmount: true,
	validate
})(AccountNew)

export default connect(null, { createAccount })(accountNewForm)

