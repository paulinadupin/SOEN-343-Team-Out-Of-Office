import './MenuItem.css'
import { useEffect, useState } from 'react'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'

function MenuItem({ itemId, restaurantId, name, price, description, status, image}) {
    const {user} = useAuth();
    const { addItem } = useCart();
    const [added, setAdded] = useState(false);
    const itemStatus = status || description;

    useEffect(() => {
        if (!added) {
            return undefined;
        }

        const timeout = setTimeout(() => setAdded(false), 1500);
        return () => clearTimeout(timeout);
    }, [added]);

    function handleAddToCart() {
        addItem({ itemId, restaurantId: Number(restaurantId), name, price, description: itemStatus, image });
        setAdded(true);
    }

    return (
        <div className="menu-item">
            <img src={image} alt={name} className="img"></img>
            <div className="content">
                <h2 className="title">{name}</h2>
                <p className="description">{itemStatus}</p>
                <p className="price">${price}</p>
            </div>
            {user && user.accountType === 'CUSTOMER' && (
               <button
                   className="add-cart"
                   onClick={handleAddToCart}
                   aria-label={`Add ${name} to cart`}
                   type="button"
               >
                   +
               </button>
            )}
            {added && <span className="cart-feedback" role="status">Added to cart</span>}

        </div>
    )
}

export default MenuItem
