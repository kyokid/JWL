import React from 'react'
import SearchBar from '../containers/SearchBar'
import AccountList from '../containers/AccountList'
import AccountNew from '../containers/AccountNew'

export default function () {
	return (
		<div className="container table-user-container">
			<SearchBar />
			<AccountList />
		</div>
	)
}
