import './Header.css'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'
import { resetSystem } from '../app/api'
import UserMenu from './UserMenu'

function Header() {
    const { user } = useAuth();
    const { itemCount } = useCart();
    const navigate = useNavigate();

    async function handleReset() {
        if (!window.confirm("Reset the entire system to its initial state? This cannot be undone.")) return;
        await resetSystem();
        window.location.reload();
    }

    return (
        <header>
            <img className="logo" src="./logo.png" alt="Logo" onClick={() => navigate('/')}></img>
            <h1>Enchantea</h1>
            {user && user.accountType === 'CUSTOMER' && (
                <div className="customer-header">
                    <p>
                        Deliver to{" "}
                        {user.deliveryAddress
                            ? `${user.deliveryAddress.lat}, ${user.deliveryAddress.lng}`
                            : "Location unavailable"}
                    </p>
                    <input className="search-bar" type="text" placeholder="Search shops"></input>
                    <button className="cart-button" onClick={()=> navigate('/cart')}>Cart · {itemCount}</button>
                </div>
            )}

            {user && user.accountType === 'OPERATOR' && (
                <button className="reset-button" onClick={handleReset}>Reset</button>
            )}

            {user && <UserMenu/>}
        </header>
    )
}

export default Header