import React from 'react'
import { ACC_IMG_URL } from '../constants/action-type'

export default function (state = '', action) {
	switch (action.type) {
		case ACC_IMG_URL:
			return action.payload.data.data
		default:
			return state
	}
}
