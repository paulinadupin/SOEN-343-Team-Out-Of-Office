import './Register.css'

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import * as authApi from '../app/api'
import { useAuth } from '../app/AuthContext'
import { HOME_BY_ACCOUNT_TYPE } from '../app/roles'

const accountFields = {
    CUSTOMER: [
        { name: 'name', label: 'Name', type: 'text', placeholder: 'Name' },
        { name: 'username', label: 'Username', type: 'text', placeholder: 'Username' },
        { name: 'password', label: 'Password', type: 'password', placeholder: 'Password' },
        {
            name: 'deliveryAddress',
            label: 'Delivery Address',
            type: 'text',
            placeholder: 'Latitude, Longitude',
        },
    ],
    RESTAURANT: [
        { name: 'name', label: 'Name', type: 'text', placeholder: 'Name' },
        { name: 'username', label: 'Username', type: 'text', placeholder: 'Username' },
        { name: 'password', label: 'Password', type: 'password', placeholder: 'Password' },
        {
            name: 'status',
            label: 'Status',
            type: 'select',
            options: [
                { value: 'OPEN', label: 'Open' },
                { value: 'PAUSED', label: 'Paused' },
                { value: 'CLOSED', label: 'Closed' },
            ],
        },
        { name: 'position', label: 'Position', type: 'text', placeholder: 'Latitude, Longitude' },
    ],
    COURIER: [
        { name: 'username', label: 'Username', type: 'text', placeholder: 'Username' },
        { name: 'password', label: 'Password', type: 'password', placeholder: 'Password' },
        {
            name: 'status',
            label: 'Status',
            type: 'select',
            options: ['OFFLINE', 'IDLE', 'ASSIGNED', 'CARRYING'],
        },
        { name: 'position', label: 'Position', type: 'text', placeholder: 'Latitude, Longitude' },
    ],
    OPERATOR: [
        { name: 'username', label: 'Username', type: 'text', placeholder: 'Username' },
        { name: 'password', label: 'Password', type: 'password', placeholder: 'Password' },
    ],
}

function Register() {
    const [accountType, setAccountType] = useState('')
    const [error, setError] = useState('')
    const [isSubmitting, setIsSubmitting] = useState(false)
    const navigate = useNavigate()
    const { login } = useAuth()

    async function handleSubmit(event) {
        event.preventDefault()
        setError('')
        setIsSubmitting(true)

        const formData = new FormData(event.currentTarget)
        const account = {
            accountType,
            username: formData.get('username'),
            password: formData.get('password'),
        }

        if (accountType === 'CUSTOMER') {
            account.name = formData.get('name')
            account.deliveryAddress = formData.get('deliveryAddress')
        }

        if (accountType === 'RESTAURANT') {
            account.name = formData.get('name')
            account.restaurantStatus = formData.get('status')
            account.position = formData.get('position')
        }

        if (accountType === 'COURIER') {
            account.courierStatus = formData.get('status')
            account.position = formData.get('position')
        }

        try {
            if (account.deliveryAddress) {
                account.deliveryAddress = parseLocation(formData.get('deliveryAddress'))
            }
            if (account.position) {
                account.position = parseLocation(formData.get('position'))
            }
            await authApi.register(account)
            const user = await login(account.username, account.password)
            navigate(HOME_BY_ACCOUNT_TYPE[user.accountType], { replace: true })
        } catch (registrationError) {
            setError(registrationError.message)
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <div className="register">
            <h1>Register</h1>
            <fieldset className="account-type">
                <h2>Account Type</h2>
                {Object.keys(accountFields).map((type) => (
                    <label className="account-type option" htmlFor={type.toLowerCase()} key={type}>
                        <input
                            type="radio"
                            id={type.toLowerCase()}
                            name="accountType"
                            value={type}
                            checked={accountType === type}
                            onChange={(event) => setAccountType(event.target.value)}
                        />
                        {type.charAt(0) + type.slice(1).toLowerCase()}
                    </label>
                ))}
            </fieldset>

            {accountType && (
                <form className="registration-fields" onSubmit={handleSubmit}>
                    {accountFields[accountType].map((field) => (
                        <label htmlFor={field.name} key={field.name}>
                            {field.label}
                            {field.type === 'select' ? (
                                <select id={field.name} name={field.name} defaultValue="" required>
                                    <option value="" disabled>Select {field.label}</option>
                                    {field.options.map((option) => (
                                        <option value={option.value} key={option.value}>{option.label}</option>
                                    ))}
                                </select>
                            ) : (
                                <input
                                    id={field.name}
                                    name={field.name}
                                    type={field.type}
                                    placeholder={field.placeholder}
                                    minLength={field.name === 'password' ? 8 : undefined}
                                    required
                                />
                            )}
                        </label>
                    ))}
                    {error && <p role="alert">{error}</p>}
                    <button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? 'Registering...' : 'Register'}
                    </button>
                </form>
            )}
            <a href="/login">Already have an account? Login</a>
        </div>
    )
}

function parseLocation(value) {
    const coordinates = value.split(',').map((coordinate) => Number(coordinate.trim()))

    if (coordinates.length !== 2 || coordinates.some((coordinate) => !Number.isFinite(coordinate))) {
        throw new Error('Enter a location as latitude, longitude.')
    }

    return { lat: coordinates[0], lng: coordinates[1] }
}

export default Register;