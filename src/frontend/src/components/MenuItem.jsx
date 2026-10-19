import './MenuItem.css'
import { useAuth } from '../app/AuthContext'
import { useCart } from '../app/useCart'

function MenuItem({ name, price, description, image}) {
    const {user} = useAuth();
    const { addItem } = useCart();
    return (
        <div className="menu-item">
            <img src={image} alt={name} className="img"></img>
            <div className="content">
                <h2 className="title">{name}</h2>
                <p className="description">{description}</p>
                <p className="price">${price}</p>
            </div>
            {user && user.accountType === 'RESTAURANT' && (
               <button>
                <img src="/edit.png" alt="Edit" className="edit-icon"></img>
               </button>
            )}
            {user && user.accountType === 'CUSTOMER' && (
               <button
                   className="add-cart"
                   onClick={() => addItem({ name, price, description, image })}
                   aria-label={`Add ${name} to cart`}
               >
                   +
               </button>
            )}

        </div>
    )
}

export default MenuItem