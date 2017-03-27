import React from 'react'
import { browserHistory } from 'react-router'
import { ROLE_LIBRARIAN } from '../constants/common'

export function checkLibrarian() {
	if (localStorage.userRole !== ROLE_LIBRARIAN) {
		browserHistory.push("/")
		return false
	}
	return true
}