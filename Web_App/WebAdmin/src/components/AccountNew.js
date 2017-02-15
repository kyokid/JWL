import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { submitImg } from '../actions/AccountsAction'
import { loadUserInputImg } from '../utils/imgUtil'

class AccountNew extends Component {
	componentDidMount() {
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
			<div className="modal fade" id="accountNewModal" tabIndex="-1" role="dialog" aria-labelledby="accountNewModalLabel" aria-hidden="true">
				<div className="modal-dialog" role="document">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<h4 className="modal-title" id="accountNewModalLabel">New Account</h4>
						</div>
						<div className="modal-body">
							{this.renderForm()}
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">Close</button>
							<button type="button" className="btn btn-primary">Save changes</button>
						</div>
					</div>
				</div>
			</div>
		)
	}

	renderForm() {
		return (
			<form onSubmit={this.onFormSubmit.bind(this)}>
				<div className="img-input-container">
					<p>Your image:</p>
					<img id="uploadImg" src="#" alt="Your image" /><br/>
					<input id="inputImg" type="file" accept="image/*" />
					<input type="hidden" name="imgUrl" value="" />
				</div>
				<button type="submit" className="btn btn-primary">Submit</button>
			</form>

		)
	}

	onFormSubmit(event) {
		event.preventDefault()

		this.props.submitImg()
		// const imgUrl = submitImg()
		// console.log(imgUrl)
	}
}

function mapStateToProps({ img }) {
	return { img }
}

function mapDispatchToProps(dispatch) {
	return bindActionCreators({ submitImg }, dispatch)
}

export default connect(mapStateToProps, mapDispatchToProps)(AccountNew)
