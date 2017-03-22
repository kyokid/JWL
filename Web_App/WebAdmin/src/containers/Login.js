import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Field, reduxForm } from 'redux-form'
import { browserHistory } from 'react-router'

import isLoggedIn from '../helpers/Authentication'
import { renderCommonField } from '../helpers/FieldRenderer'

import login from '../actions/LoginAction'

class Login extends Component {
	constructor(props) {
		super(props)

		this.onFormSubmit = this.onFormSubmit.bind(this)

		this.state = {
			code: "200",
			message: ""
		}
	}

	componentWillMount() {
		if (isLoggedIn()) {
			browserHistory.push("/")
		}
	}

	render() {
		const { handleSubmit } = this.props
		const { code, message } = this.state

		return (
			<div>
				<div className="panel panel-default" style={{ width: "100%" }}>
					<div className="panel-heading">
						<h3 className="panel-title">Login</h3>
					</div>
					<div className="panel-body">
						<form className="form-horizontal"
									onSubmit={handleSubmit(this.onFormSubmit)}>
							<Field id="userId"
										 className="col-md-12 col-sm-12"
										 name="userId"
										 label="User ID"
										 type="text"
										 helpBlock="Enter your User ID."
										 component={renderCommonField} />
							<Field id="password"
										 className="col-md-12 col-sm-12"
										 name="password"
										 label="Password"
										 type="password"
										 helpBlock="Enter your Password."
										 component={renderCommonField} />
							<input type="submit" className="btn btn-primary" value="Login" />
							{code !== "200" && <span className="help-block" style={{ color: "red", marginTop: "20px" }}>{message}</span>}
						</form>

					</div>
				</div>
			</div>
		)
	}

	onFormSubmit(props) {
		const self = this
		this.props.login(props).then((actionObj) => {
			const data = actionObj.payload.data
			if (data.code !== "200") {
				self.setState({
					code: data.code,
					message: data.textMessage
				})
			} else {
				self.setState({
					code: "",
					message: ""
				})
				localStorage.userId = data.data.userId
				localStorage.userRole = data.data.userRoleRole
				browserHistory.push("/")
			}
		})
	}
}

let loginForm = reduxForm({
	form: 'loginForm',
	destroyOnUnmount: true
})(Login)

export default connect(null, { login })(loginForm)
