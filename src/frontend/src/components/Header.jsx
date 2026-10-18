import './Header.css'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'

function Header() {
    const [cartItems] = useState(0);
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [menuOpen, setMenuOpen] = useState(false)

    async function handleLogout() {
        await logout();
        setMenuOpen(false);
        navigate('/login');
    }

    return (
        <header>
            <img className="logo" src="./logo.png" alt="Logo"></img>
            <h1>Enchantea</h1>
            <input className="search-bar" type="text" placeholder="Search shops"></input>
            <button className="cart-button">Cart {cartItems}</button>
            {user ? (
                <div className="user-menu">
                    <img
                        className="user-icon"
                        src="./user.png"
                        alt="User"
                        onClick={() => setMenuOpen(!menuOpen)}
                    />
                    {menuOpen && (
                        <div className="user-dropdown">
                            <p>{user.name}</p>
                            <button onClick={handleLogout}>Log out</button>
                        </div>
                    )}
                </div>
            ) : (
                <Link to="/login" className="login-button">Log in</Link>
            )}
        </header>
    )
}

export default Header