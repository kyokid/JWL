import React from 'react'

export default function () {
	const userId = localStorage.userId
	return !(!userId || userId.trim() === "");
}