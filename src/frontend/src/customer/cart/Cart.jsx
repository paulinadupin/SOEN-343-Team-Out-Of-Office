import CartItem from "./CartItem";
import "./Cart.css";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../app/useCart";
import { placeOrder, getRestaurants, getRestaurantMenu } from "../../app/api";
import { useEffect, useState } from "react";

function Cart() {
    const navigate = useNavigate();
    const { items, itemCount, clearCart } = useCart();
    const [error, setError] = useState(null);
    const [placingOrder, setPlacingOrder] = useState(false);
    const [restaurantStatus, setRestaurantStatus] = useState(null);
    const [menu, setMenu] = useState([]);
    const total = items.reduce(
        (sum, item) => sum + Number(item.price) * item.quantity,
        0
    );

    const cartRestaurantId = items[0]?.restaurantId;

    // R-DASH-04: Place Order must disable when the restaurant is paused/closed,
    // or when every selected item is sold out, so this needs live data, not the
    // cart's own snapshot from whenever each item was added.
    useEffect(() => {
        if (!cartRestaurantId) return;

        getRestaurants()
            .then((restaurants) => {
                const restaurant = restaurants.find((r) => r.userId === cartRestaurantId);
                setRestaurantStatus(restaurant?.status ?? null);
            })
            .catch(() => setRestaurantStatus(null));

        getRestaurantMenu(cartRestaurantId)
            .then(setMenu)
            .catch(() => setMenu([]));
    }, [cartRestaurantId]);

    const allItemsSoldOut = items.length > 0 && items.every((item) => {
        const menuItem = menu.find((candidate) => candidate.id === item.itemId);
        return menuItem?.status === "SOLD_OUT";
    });

    const placeOrderDisabled =
        placingOrder || (restaurantStatus != null && restaurantStatus !== "OPEN") || allItemsSoldOut;

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
                    {restaurantStatus != null && restaurantStatus !== "OPEN" && (
                        <p role="alert">This restaurant is currently {restaurantStatus.toLowerCase()}.</p>
                    )}
                    {allItemsSoldOut && <p role="alert">All selected items are sold out.</p>}
                    <button onClick={handlePlaceOrder} disabled={placeOrderDisabled}>
                        {placingOrder ? "Placing order..." : "Place Order"}
                    </button>
                </div>
            )}
        </div>
    )
}

export default Cart;