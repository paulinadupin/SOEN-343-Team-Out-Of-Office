import Order from "./Order";
import "./OrdersPage.css";
import { useCallback, useEffect, useState } from "react";
import { getRestaurantOrders, updateOrder } from "../../app/api";

function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);

    const loadOrders = useCallback(() => {
        setLoading(true);
        setError(null);
        return getRestaurantOrders()
            .then(setOrders)
            .catch((requestError) => setError(requestError.message))
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        const refreshOrders = () => getRestaurantOrders()
            .then(setOrders)
            .catch((requestError) => setError(requestError.message));

        refreshOrders().finally(() => setLoading(false));
        const interval = setInterval(refreshOrders, 5000);

        return () => clearInterval(interval);
    }, []);

    function handleAction(orderId, action, reason) {
        return updateOrder(orderId, action, reason)
            .then(loadOrders)
            .catch((requestError) => setError(requestError.message));
    }

    const incomingOrders = orders.filter((order) => order.status === "PLACED");
    const currentOrders = orders.filter(
        (order) => !["PLACED", "DELIVERED", "REJECTED", "CANCELLED", "REFUNDED"].includes(order.status)
    );

    return (
        <div className="orders-page">
            <h1>Orders</h1>
            {loading && <p>Loading orders...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && !error && (
                <div>
                    <div>
                        <h2>Incoming Orders</h2>
                        {incomingOrders.length === 0
                            ? <p>No incoming orders.</p>
                            : incomingOrders.map((order) => (
                                <Order key={order.id} order={order} onAction={handleAction} />
                            ))}
                    </div>
                    <div>
                        <h2>Current Orders</h2>
                        {currentOrders.length === 0
                            ? <p>No current orders.</p>
                            : currentOrders.map((order) => (
                                <Order key={order.id} order={order} onAction={handleAction} />
                            ))}
                    </div>
                    <div>
                        <h2>Past Orders</h2>
                        {orders.filter((order) => ["DELIVERED", "REJECTED", "CANCELLED", "REFUNDED"].includes(order.status)).length === 0
                            ? <p>No past orders.</p>
                            : orders.filter((order) => ["DELIVERED", "REJECTED", "CANCELLED", "REFUNDED"].includes(order.status)).map((order) => (
                                <Order key={order.id} order={order} onAction={handleAction} />
                            ))}
                    </div>
                    
                </div>
            )}
        </div>
    )
}

export default OrdersPage;