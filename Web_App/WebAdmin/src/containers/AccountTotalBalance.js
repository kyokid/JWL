import React, { Component } from 'react'
import { connect } from 'react-redux'
import formatMoney from '../helpers/CurrencyFormatter'
import { updateTotalBalance } from '../actions/AccountsAction'

class AccountTotalBalance extends Component {
	constructor(props) {
		super(props)

		this.state = {
			isEditingTotalBalance: false,
			newTotalBalance: this.props.totalBalance
		}

		this.onInputChange = this.onInputChange.bind(this)
		this.onStartEditing = this.onStartEditing.bind(this)
		this.onCancel = this.onCancel.bind(this)
		this.onFormSubmit = this.onFormSubmit.bind(this)
	}

	render() {
		let { isEditingTotalBalance, newTotalBalance } = this.state
		const { totalBalance } = this.props

		if (isEditingTotalBalance) {
			return (
				<form className="form-inline balance">
					<div className="form-group">
						<label htmlFor="totalBalanceInput">Total Balance:&nbsp;</label>
						<input type="text"
									 className="form-control"
									 id="totalBalanceInput"
									 placeholder="Enter total balance..."
									 value={newTotalBalance}
									 onChange={this.onInputChange} />
					</div>
					<div className="form-group">
						<a onClick={() => this.onCancel(totalBalance)}
							 className={this.props.isAddingBook ? 'disable' : ''}>
							<span className="glyphicon glyphicon-remove edit-balance-icon" aria-hidden="true" />
						</a>
						<a onClick={this.onFormSubmit}
							 className={this.props.isAddingBook ? 'disable' : ''}>
							<span className="glyphicon glyphicon-ok edit-balance-icon" aria-hidden="true" />
						</a>
					</div>
				</form>
			)
		}

		const formattedTotalBalance = formatMoney(totalBalance)
		return (
			<p className="balance">
				Total Balance: {formattedTotalBalance}&nbsp;
				<a onClick={() => this.onStartEditing(totalBalance)}
					 className={this.props.isAddingBook ? 'disable' : ''}>
					<span className="glyphicon glyphicon-pencil edit-balance-icon" aria-hidden="true" />
				</a>
			</p>
		)
	}

	onInputChange(event) {
		this.setState({ newTotalBalance: event.target.value })
	}

	onStartEditing(totalBalance) {
		this.setState({
			isEditingTotalBalance: true,
			newTotalBalance: totalBalance
		})
	}

	onCancel(totalBalance) {
		this.setState({
			isEditingTotalBalance: false,
			newTotalBalance: totalBalance
		})
	}

	onFormSubmit() {
		this.props.updateTotalBalance(this.props.userId, this.state.newTotalBalance).then((data) => {
			this.setState({ isEditingTotalBalance: false })
		})
	}
}

export default connect(null, { updateTotalBalance })(AccountTotalBalance)
