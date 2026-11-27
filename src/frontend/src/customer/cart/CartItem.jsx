import './CartItem.css'

function CartItem({ item }) {
    const lineTotal = Number(item.price) * item.quantity;

    return (
        <div className="cart-item">
            <p className="cart-item-name">{item.name}</p>
            <p className="cart-item-quantity">{item.quantity}</p>
            <p className="price">${lineTotal.toFixed(2)}</p>
        </div>
    )
}

export default CartItem;