import React from 'react'
import { FETCH_ACCOUNTS } from '../constants/action-type'

export default function (state = [], action) {
	switch (action.type) {
		case FETCH_ACCOUNTS:
			return action.payload.data
	}
	return state
}
