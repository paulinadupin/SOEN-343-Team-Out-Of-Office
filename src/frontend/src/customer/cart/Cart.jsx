import CartItem from "./CartItem";
import "./Cart.css";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../app/useCart";

function Cart() {
    const navigate = useNavigate();
    const { items, itemCount, clearCart } = useCart();
    const total = items.reduce(
        (sum, item) => sum + Number(item.price) * item.quantity,
        0
    );

    return (
        <div className="cart">
            <h1>Cart</h1>
            {items.length === 0 ? (
                <p>Your cart is empty.</p>
            ) : (
                <>
                    {items.map((item) => (
                        <CartItem key={item.name} item={item} />
                    ))}
                    <p>Items: {itemCount}</p>
                    <p>Total: ${total.toFixed(2)}</p>
                    <button onClick={() => {
                        clearCart();
                        alert("Order placed!");
                        navigate("/");
                    }}>Place Order</button>
                </>
            )}
        </div>
    )
}

export default Cart;