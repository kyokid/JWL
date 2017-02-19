import React, { Component } from 'react'
import {Field, reduxForm} from 'redux-form'

import { submitImg } from '../actions/AccountsAction'
import { loadUserInputImg } from '../helpers/ImgPicker'
import { renderField } from '../helpers/FieldRenderer'
import { validate } from  '../helpers/FieldValidator'
import ReactDatePicker from '../helpers/ReactDatePicker'

class AccountNew extends Component {
	constructor(props) {
		super(props)

		this.state = {
			imgUrl: ''
		}
	}

	componentDidMount() {
		// Load JS.
		loadUserInputImg()
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
		const {handleSubmit} = this.props

		return (
			<form onSubmit={handleSubmit(this.onFormSubmit.bind(this))}>
				<div className="img-input-container form-group">
					<p>Your image:</p>
					<div className="img-placeholder">
						<img id="uploadImg" src="#" alt="Your image" />
						<input id="inputImg" type="file" name="file" accept="image/*" />
						<label htmlFor="inputImg">Upload your profile image.</label>
						<input type="hidden" name="imgUrl" value={this.props.imgUrl} />
					</div>
				</div>

				<Field id="userId"
							 name="userId"
							 label="User ID"
							 type="text"
							 component={renderField}/>
				<Field id="fullName"
							 name="fullName"
							 label="Full Name"
							 type="text"
							 component={renderField}/>
				<Field id="password"
							 name="password"
							 label="Password"
							 type="password"
							 component={renderField}/>
				<Field id="email"
							 name="email"
							 label="Email"
							 type="text"
							 component={renderField}/>
				<Field id="address"
							 name="address"
							 label="Address"
							 type="text"
							 component={renderField}/>
				<Field id="birthDate"
							 className="date"
							 name="birthDate"
							 label="Date of Birth"
							 type="text"
							 component={ReactDatePicker}/>
				<Field id="phoneNo"
							 name="phoneNo"
							 label="Phone Number"
							 type="text"
							 component={renderField}/>
				<Field id="workPlace"
							 name="workPlace"
							 label="Place of Work"
							 type="text"
							 component={renderField}/>
				<Field id="userRole"
							 name="userRole"
							 label="User Role"
							 type="text"
							 component={renderField}/>

				<button type="reset" className="btn btn-default" data-dismiss="modal">Close</button>
				<button type="submit" className="btn btn-primary">Submit</button>
			</form>
		)
	}

	onFormSubmit(props) {
		console.log(props)

	}
}

export default reduxForm({
	form: 'accountNewForm',
	destroyOnUnmount: false,
	validate
}, null, { submitImg })(AccountNew)
