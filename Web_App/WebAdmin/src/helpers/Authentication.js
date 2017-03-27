import React from 'react'
import { ROLE_LIBRARIAN, ROLE_ADMIN } from '../constants/common'

export default function () {
	const userId = localStorage.userId
	const userRole = localStorage.userRole
	const checkUserId = userId && userId.trim() !== ""
	const checkUserRole = userRole && userRole.trim() !== "" && userRole === ROLE_ADMIN || userRole === ROLE_LIBRARIAN

	return checkUserId && checkUserRole;
}