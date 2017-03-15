import { EMAIL_REGEX, PHONE_NO_REGEX } from '../constants/regex'

export const validate = (values) => {
	const errors = {}
	if (!values.profileImgUrl || typeof(values.profileImgUrl) != 'string' || values.profileImgUrl.trim() == '') {
		errors.profileImgUrl = 'User profile image is required.'
	}
	if (!values.userId) {
		errors.userId = 'User ID is required.'
	} else if (values.userId.length < 6 || values.userId.length > 15) {
		errors.userId = 'User ID’s length must be 6-15 characters.'
	}

	if (!values.profileFullname) {
		errors.profileFullname = 'Full name is required.'
	} else if (values.profileFullname.length > 100) {
		errors.profileFullname = 'Full name must not be longer than 100 characters.'
	}

	if (!values.password) {
		errors.password = 'Password is required.'
	} else if (values.password.length < 6 || values.password.length > 50) {
		errors.password = 'Password’s length must be 6-50 characters.'
	} else if (values.password != values.confirmPassword) {
		errors.confirmPassword = 'Confirm Password must match Password.'
	}

	if (!values.profileEmail) {
		errors.profileEmail = 'Email Address is required.'
	} else if (!EMAIL_REGEX.test(values.profileEmail)) {
		errors.profileEmail = 'Invalid Email Address format. Example of a valid one: test@mail.com.'
	}

	if (!values.profilePhoneNo) {
		errors.profilePhoneNo = 'Phone Number is required.'
	} else if (!PHONE_NO_REGEX.test(values.profilePhoneNo)) {
		errors.profilePhoneNo = 'Invalid Phone Number format. Example o a valid one: +841692536224 or 01692536224.'
	}

	if (!values.profileAddress) {
		errors.profileAddress = 'Address is required.'
	} else if (values.profileAddress.length > 250) {
		errors.profileAddress = 'Address must not be longer than 250 characters.'
	}

	if (!values.profilePlaceOfWork) {
		errors.profilePlaceOfWork = 'Place of Work is required.'
	} else if (values.profilePlaceOfWork.length > 100) {
		errors.profilePlaceOfWork = 'Place of Work must not be longer than 100 characters.'
	}

	if (!values.profileDateOfBirth) {
		errors.profileDateOfBirth = 'Date of Birth is required.'
	}

	if (!values.userRoleId) {
		errors.userRoleId = 'User Role is required.'
	}

	return errors
}
