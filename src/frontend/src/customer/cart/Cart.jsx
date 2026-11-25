import CartItem from "./CartItem";
import "./Cart.css";
import { useNavigate } from "react-router-dom";
import { useCart } from "../../app/useCart";
import { placeOrder, getOrderQuote, getRestaurants, getRestaurantMenu, applyPromo } from "../../app/api";
import { useEffect, useState } from "react";

function Cart() {
    const navigate = useNavigate();
    const { items, itemCount, clearCart } = useCart();
    const [error, setError] = useState(null);
    const [placingOrder, setPlacingOrder] = useState(false);
    const [restaurantStatus, setRestaurantStatus] = useState(null);
    const [menu, setMenu] = useState([]);
    const [quoteResult, setQuoteResult] = useState(null);
    const [promoCode, setPromoCode] = useState("");
    const [promoMessage, setPromoMessage] = useState(null);
    const [appliedCode, setAppliedCode] = useState(null);
    //TODO: Review if discount is needed or will be handled in the backend
    const [discount, setDiscount] = useState(0);

    const cartRestaurantId = items[0]?.restaurantId;
    const hasValidQuoteRequest = items.length > 0
        && new Set(items.map((item) => item.restaurantId)).size === 1
        && items.every((item) => item.itemId);
    const currentQuoteResult = quoteResult?.items === items ? quoteResult : null;
    const quote = currentQuoteResult?.quote ?? null;
    const quoteError = items.length > 0 && !hasValidQuoteRequest
        ? "Your cart contains invalid restaurant or menu information."
        : currentQuoteResult?.error ?? null;
    const quoteLoading = items.length > 0 && hasValidQuoteRequest && !currentQuoteResult;

    useEffect(() => {
        let current = true;

        if (!hasValidQuoteRequest) return;

        getOrderQuote(
            cartRestaurantId,
            items.map((item) => ({ itemId: item.itemId, quantity: item.quantity }))
        )
            .then((result) => {
                if (current) setQuoteResult({ items, quote: result, error: null });
            })
            .catch((requestError) => {
                if (current) setQuoteResult({ items, quote: null, error: requestError.message });
            });

        return () => {
            current = false;
        };
    }, [items, cartRestaurantId, hasValidQuoteRequest]);

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

    useEffect(() => {
        setDiscount(0);
        setAppliedCode(null);
        setPromoMessage(null);
    }, [items]);

    const allItemsSoldOut = items.length > 0 && items.every((item) => {
        const menuItem = menu.find((candidate) => candidate.id === item.itemId);
        return menuItem?.status === "SOLD_OUT";
    });

    const placeOrderDisabled =
        placingOrder || quoteLoading || !quote
        || (restaurantStatus != null && restaurantStatus !== "OPEN") || allItemsSoldOut;

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
                    quantity: item.quantity
                })),
                appliedCode
            );
            clearCart();
            navigate("/", {state: {orderPlaced: true}});
        } catch (requestError) {
            setError(requestError.message);
        } finally {
            setPlacingOrder(false);
        }
    }

    async function handleApplyPromo() {
        setPromoMessage(null);
        const code = promoCode.trim();
        try {
            const result = await applyPromo(
                code,
                items.map((item) => ({ itemId: item.itemId, quantity: item.quantity }))
            );
            setDiscount(result.discount);
            setAppliedCode(code);
            setPromoMessage("Promo applied!");
        } catch (requestError) {
            setDiscount(0);
            setAppliedCode(null);
            setPromoMessage(requestError.message);
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
                    {quoteLoading && <p role="status">Calculating total...</p>}
                    {quoteError && <p role="alert">Unable to calculate total: {quoteError}</p>}
                    {quote && (
                        <>
                            <p>Subtotal: ${quote.subtotal.toFixed(2)}</p>
                            <p>Delivery fee: ${quote.deliveryFee.toFixed(2)}</p>
                            <p>Service fee: ${quote.serviceFee.toFixed(2)}</p>
                            {quote.smallOrderSurcharge > 0 && (
                                <p>Small-order surcharge: ${quote.smallOrderSurcharge.toFixed(2)}</p>
                            )}
                            <p>Total: ${quote.total.toFixed(2)}</p>
                        </>
                    )}
                    {error && <p role="alert">{error}</p>}
                    {restaurantStatus != null && restaurantStatus !== "OPEN" && (
                        <p role="alert">This restaurant is currently {restaurantStatus.toLowerCase()}.</p>
                    )}
                    {allItemsSoldOut && <p role="alert">All selected items are sold out.</p>}
                    <button onClick={handlePlaceOrder} disabled={placeOrderDisabled}>
                        {placingOrder ? "Placing order..." : "Place Order"}
                    </button>
                    <div className="cart promo">
                <label htmlFor="promo-code">Promo code</label>
                <input
                    id="promo-code"
                    type="text"
                    value={promoCode}
                    onChange={(e) => setPromoCode(e.target.value)}
                />
                <button type="button" onClick={handleApplyPromo}>Apply</button>
            </div>
            {promoMessage && <p role="status">{promoMessage}</p>}

            {error && <p role="alert">{error}</p>}
                </div>
            )}
        </div>
    )
}

export default Cart;