import CartItem from "./CartItem";
import "./Cart.css";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../app/useCart";
import { placeOrder } from "../../app/api";
import { useState } from "react";

function Cart() {
    const navigate = useNavigate();
    const { items, itemCount, clearCart } = useCart();
    const [error, setError] = useState(null);
    const [placingOrder, setPlacingOrder] = useState(false);
    const total = items.reduce(
        (sum, item) => sum + Number(item.price) * item.quantity,
        0
    );

    async function handlePlaceOrder() {
        setError(null);
        const restaurantIds = new Set(items.map((item) => item.restaurantId));

        if (restaurantIds.size !== 1 || items.some((item) => !item.itemId)) {
            setError("Your cart contains invalid restaurant or menu information.");
            return;
        }

        setPlacingOrder(true);
        try {
            const [restaurantId] = restaurantIds;
            await placeOrder(
                restaurantId,
                items.map((item) => ({
                    itemId: item.itemId,
                    quantity: item.quantity,
                }))
            );
            clearCart();
            navigate("/", {state: {orderPlaced: true}});
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setPlacingOrder(false);
        }
    }

    return (
        <div className="cart">
            <h1>Cart</h1>
            {items.length === 0 ? (
                <p>Your cart is empty.</p>
            ) : (
                <div className="cart items">
                    <div className="cart header">
                        <p>Item</p>
                        <p>Quantity</p>
                        <p>Price</p>
                    </div>
                    {items.map((item) => (
                        <CartItem key={item.name} item={item} />
                    ))}
                    <p>Items: {itemCount}</p>
                    <p>Total: ${total.toFixed(2)}</p>
                    {error && <p role="alert">{error}</p>}
                    <button onClick={handlePlaceOrder} disabled={placingOrder}>
                        {placingOrder ? "Placing order..." : "Place Order"}
                    </button>
                </div>
            )}
        </div>
    )
}

export default Cart;