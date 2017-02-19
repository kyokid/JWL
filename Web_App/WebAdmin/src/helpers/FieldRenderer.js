import React from 'react'

export function renderField({input, id, label, type, meta: {touched, error}}) {
	return (
		<div className={`form-group ${touched && error ? 'has-error' : ''}`}>
			{ label && <label htmlFor={id}>{label}</label> }
			<input {...input}
						 id={id}
						 className="form-control"
						 placeholder={label}
						 type={type} />
			{touched && error && <span className="help-block">{error}</span>}
		</div>
	)
}
