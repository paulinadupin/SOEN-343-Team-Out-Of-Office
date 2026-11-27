import './Header.css'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'
import { resetSystem, reverseGeocode } from '../app/api'
import UserMenu from './UserMenu'

function Header() {
    const { user } = useAuth();
    const { itemCount } = useCart();
    const navigate = useNavigate();
    const [geocodedAddress, setGeocodedAddress] = useState(null);
    const location = user?.location;
    const locationKey = location
        ? `${user.id}:${location.lat}:${location.lng}`
        : null;
    const address = geocodedAddress?.key === locationKey
        ? geocodedAddress.value
        : null;

    async function handleReset() {
        if (!window.confirm("Reset the entire system to its initial state? This cannot be undone.")) return;
        await resetSystem();
        window.location.reload();
    }

    useEffect(() => {
        let current = true;
        if (!location) {
            return () => {
                current = false;
            };
        }

        const requestKey = `${user.id}:${location.lat}:${location.lng}`;
        reverseGeocode(location.lat, location.lng)
            .then((value) => {
                if (current) setGeocodedAddress({ key: requestKey, value });
            })
            .catch(() => {
                if (current) setGeocodedAddress({ key: requestKey, value: null });
            });

        return () => {
            current = false;
        };
    }, [user?.id, location]);

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
                                        ? address ?? `${user.location.lat}, ${user.location.lng}`
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