import './Header.css'
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
        <header className="site-header">
            <img className="logo" src="./logo.png" alt="Logo" onClick={() => navigate('/')}></img>
            <h1 className="brand">Enchantea</h1>
            {user && (
                <div className="header-actions">
                    {user.accountType === 'CUSTOMER' && (
                        <>
                            <div className="customer-header">
                                <p>
                                    Deliver to{" "}
                                    {user.location
                                        ? `${user.location.lat}, ${user.location.lng}`
                                        : "Location unavailable"}
                                </p>
                            </div>
                            <button className="cart-button" onClick={() => navigate('/cart')}>
                                Cart · {itemCount}
                            </button>
                        </>
                    )}

                    {user.accountType === 'OPERATOR' && (
                        <button className="reset-button" onClick={handleReset}>Reset</button>
                    )}

                    <UserMenu />
                </div>
            )}
        </header>
    )
}

export default Header