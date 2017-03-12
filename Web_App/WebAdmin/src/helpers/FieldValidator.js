import { EMAIL_REGEX, PHONE_NO_REGEX } from '../constants/regex'

export const validate = (values) => {
	const errors = {}
	if (!values.imgUrl || values.imgUrl.trim() == '') {
		errors.imgUrl = 'User profile image is required.'
	}
	if (!values.userId) {
		errors.userId = 'User ID is required.'
	} else if (values.userId.length < 6 || values.userId.length > 15) {
		errors.userId = 'User ID’s length must be 6-15 characters.'
	}

	if (!values.fullName) {
		errors.fullName = 'Full name is required.'
	} else if (values.fullName.length > 100) {
		errors.fullName = 'Full name must not be longer than 100 characters.'
	}

	if (!values.password) {
		errors.password = 'Password is required.'
	} else if (values.password.length < 6 || values.password.length > 50) {
		errors.password = 'Password’s length must be 6-50 characters.'
	} else if (values.password != values.confirmPassword) {
		errors.confirmPassword = 'Confirm Password must match Password.'
	}

	if (!values.email) {
		errors.email = 'Email Address is required.'
	} else if (!EMAIL_REGEX.test(values.email)) {
		errors.email = 'Invalid Email Address format. Example of a valid one: test@mail.com.'
	}

	if (!values.phoneNo) {
		errors.phoneNo = 'Phone Number is required.'
	} else if (!PHONE_NO_REGEX.test(values.phoneNo)) {
		errors.phoneNo = 'Invalid Phone Number format. Example o a valid one: +841692536224 or 01692536224.'
	}

	if (!values.address) {
		errors.address = 'Address is required.'
	} else if (values.address.length > 250) {
		errors.address = 'Address must not be longer than 250 characters.'
	}

	if (!values.workPlace) {
		errors.workPlace = 'Place of Work is required.'
	} else if (values.workPlace.length > 100) {
		errors.workPlace = 'Place of Work must not be longer than 100 characters.'
	}

	if (!values.birthDate) {
		errors.birthDate = 'Date of Birth is required.'
	}

	if (!values.userRole) {
		errors.userRole = 'User Role is required.'
	}

	return errors
}
