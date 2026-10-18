import './Header.css'
import { useState } from 'react'
import { Link } from 'react-router-dom'



function Header() {
    const cartItems = useState(0);

    return (
        <header>
            <img className="logo" src="./logo.png" alt="Logo"></img>
            <h1>Enchantea</h1>
            <input className="search-bar" type="text" placeholder="Search shops"></input>
            <button className="cart-button">Cart {cartItems}</button>
            <img className="user-icon" src="./user.png" alt="User"></img>
        
        </header>
    )
}

export default Header