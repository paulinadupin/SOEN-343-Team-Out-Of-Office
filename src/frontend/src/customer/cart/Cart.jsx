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
            items.map((item) => ({ itemId: item.itemId, quantity: item.quantity })),
            appliedCode
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
    }, [items, cartRestaurantId, hasValidQuoteRequest, appliedCode]);

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
            // validate the code before committing it to the quote - a rejected
            // code shouldn't break the base subtotal/fees breakdown below.
            await applyPromo(
                code,
                cartRestaurantId,
                items.map((item) => ({ itemId: item.itemId, quantity: item.quantity }))
            );
            setAppliedCode(code);
            setPromoMessage("Promo applied!");
        } catch (requestError) {
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
                    <p className="receipt-note">Items: {itemCount}</p>

                    <div className="receipt-divider" />

                    {quoteLoading && <p role="status">Calculating total...</p>}
                    {quoteError && <p role="alert">Unable to calculate total: {quoteError}</p>}
                    {quote && (
                        <>
                            <div className="receipt-line">
                                <span>Subtotal</span>
                                <span>${quote.subtotal.toFixed(2)}</span>
                            </div>
                            <div className="receipt-line">
                                <span>Delivery fee</span>
                                <span>${quote.deliveryFee.toFixed(2)}</span>
                            </div>
                            <div className="receipt-line">
                                <span>Service fee</span>
                                <span>${quote.serviceFee.toFixed(2)}</span>
                            </div>
                            {quote.smallOrderSurcharge > 0 && (
                                <div className="receipt-line">
                                    <span>Small-order surcharge</span>
                                    <span>${quote.smallOrderSurcharge.toFixed(2)}</span>
                                </div>
                            )}
                            {quote.discount > 0 && (
                                <div className="receipt-line">
                                    <span>Discount</span>
                                    <span>-${quote.discount.toFixed(2)}</span>
                                </div>
                            )}
                            <div className="receipt-line total">
                                <span>Total</span>
                                <span>${quote.total.toFixed(2)}</span>
                            </div>
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

                    <div className="receipt-divider" />

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
                </div>
            )}
        </div>
    )
}

export default Cart;