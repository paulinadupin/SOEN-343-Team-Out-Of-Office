import './Header.css'
import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'

function Header() {
    const { user, logout } = useAuth();
    const { itemCount } = useCart();
    const navigate = useNavigate();
    const [menuOpen, setMenuOpen] = useState(false)

    async function handleLogout() {
        await logout();
        setMenuOpen(false);
        navigate('/login');
    }

    return (
        <header>
            <img className="logo" src="./logo.png" alt="Logo" onClick={() => navigate('/')}></img>
            <h1>Enchantea</h1>
            {user && user.accountType === 'CUSTOMER' && (
                <div className="customer-header">
                    <p>Deliver to {user.address}</p>
                    <input className="search-bar" type="text" placeholder="Search shops"></input>
                    <button className="cart-button" onClick={()=> navigate('/cart')}>Cart · {itemCount}</button>
                </div>
            )}

            {user && user.accountType === 'RESTAURANT' && (
                <button onClick={() => navigate('/orders')}>View Orders</button>
            )}

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
                            <p>{user.username}</p>
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