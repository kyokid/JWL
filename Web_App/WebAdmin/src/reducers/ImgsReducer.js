import React from 'react'
import { ACC_IMG_URL } from '../constants/action-type'

export default function (state = {}, action) {
	console.log("Action knocks on ImgsReducer!")
	switch (action.type) {
		case ACC_IMG_URL:
			console.log("ImgsReducer responses to action!")
			console.log(action.payload.data)
			return action.payload.data
		default:
			return state
	}
}
