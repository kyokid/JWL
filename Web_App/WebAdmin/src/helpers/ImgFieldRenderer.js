import React, { Component } from 'react'
import { connect } from 'react-redux'
import { submitImg } from '../actions/AccountsAction'
import { change } from 'redux-form'
import store from '../store'

class ImgFieldRenderer extends Component {
	constructor(props) {
		super(props)

		this.state = {
			imgSrc: '#',
			uploading: false
		}
		this.onUserInputImg = this.onUserInputImg.bind(this)
	}

	render() {
		const { input, id, label, formName, meta: { touched, error } } = this.props

		return (
			<div className={`img-input-container form-group ${touched && error ? 'has-error' : ''}`}>
				<label htmlFor={id}>{label}: </label>
				<div className="img-placeholder">
					<img id="uploadImg"
							 src={this.state.imgSrc}
							 alt="Your profile image."
							 style={{ display: this.state.imgSrc != '#' ? "block" : "none" }}	/>
					<input id={id}
								 type="file"
								 accept="image/*"
								 onChange={(e) => input.onChange(this.onUserInputImg(e, formName, input.name))} />
					<label htmlFor={id}>
						{this.state.uploading ? "" : "Upload your profile image."}
					</label>
					<div className="downloading"
							 style={{ display: this.state.uploading ? "block" : "none" }}>
						Uploading...
					</div>
					<input {...input} type="hidden" />
				</div>
				{touched && error && <span className="help-block">{error}</span>}
			</div>
		)
	}

	onUserInputImg(e, formName, inputName) {
		if (e.target.files && e.target.files[0]) {
			let self = this
			const imgFile = e.target.files[0]
			const reader = new FileReader()
			reader.readAsDataURL(imgFile)
			reader.onload = (e) => (this.setState({
				imgSrc: e.target.result,
				uploading: true
			}))

			self.props.submitImg(imgFile).then((data) => {
				// change the value of Field named {inputName} of the form named {formName}
				store.dispatch(change(formName, inputName, data.payload.data.data.img_url))

				self.setState({ uploading: false })
			})
		}
	}
}

export default connect(null, { submitImg })(ImgFieldRenderer)
