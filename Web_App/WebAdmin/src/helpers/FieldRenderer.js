import React from 'react'

export function renderCommonField({ input, id, className, label, type, helpBlock, meta: { touched, error } }) {
	return (
		<div className={`form-group ${className ? className : ''} ${touched && error ? 'has-error' : ''}`}>
			{ label && <label htmlFor={id}>{label}</label> }
			<input {...input}
						 id={id}
						 className="form-control"
						 type={type} />
			{touched && error ? <span className="help-block">{error}</span> : <p className="help-block">{helpBlock}</p>}
		</div>
	)
}

export function renderUserRoleRadioGroup(fields) {
	const { id, className, labels, values, helpBlock, userRoleId: { input, meta: { touched, error } } } = fields
	return (
		<div className={`form-group radio-group ${className ? className : ''} ${touched && error ? 'has-error' : ''}`} id={id}>
			<label className="role-group" htmlFor={id}>User Role:</label>
			{values.map((value, index) => {
				return (
						<label className="role-radio" key={index}>
							{labels[index]}
							<input {...input}
										 type="radio"
										 value={value} />
						</label>
					)
			})}
			{touched && error ? <span className="help-block">{error}</span> : <p className="help-block">{helpBlock}</p>}
		</div>
	)
}
