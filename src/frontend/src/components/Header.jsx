import './Header.css'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'
import UserMenu from './UserMenu'

function Header() {
    const { user } = useAuth();
    const { itemCount } = useCart();
    const navigate = useNavigate();

    return (
        <header>
            <img className="logo" src="./logo.png" alt="Logo" onClick={() => navigate('/')}></img>
            <h1>Enchantea</h1>
            {user && user.accountType === 'CUSTOMER' && (
                <div className="customer-header">
                    <p>
                        Deliver to{" "}
                        {user.location
                            ? `${user.location.lat}, ${user.location.lng}`
                            : "Location unavailable"}
                    </p>
                    <input className="search-bar" type="text" placeholder="Search shops"></input>
                    <button className="cart-button" onClick={()=> navigate('/cart')}>Cart · {itemCount}</button>
                </div>
            )}

            {user && <UserMenu/>}
        </header>
    )
}

export default Header