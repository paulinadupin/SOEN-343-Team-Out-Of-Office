import OrderItem from "./OrderItem";
import "./Order.css";

function displayStatus(status) {
    return status.replace(/_/g, " ").toLowerCase().replace(/^\w/, (letter) => letter.toUpperCase());
}

function Order({ order, onAction }) {
    function runAction(action, reason) {
        onAction(order.id, action, reason);
    }

    return (
        <article className="order">
            <div className="order-header">
                <div>
                    <p className="order-eyebrow">Order</p>
                    <h3>#{order.id.slice(0, 8)}</h3>
                </div>
                <span className={`order-status order-status--${order.status.toLowerCase()}`}>
                    {displayStatus(order.status)}
                </span>
            </div>

            <div className="order-meta">
                <p><span>Customer</span>{order.customerId}</p>
                <p><span>Placed</span>{new Date(order.placedAt).toLocaleString()}</p>
            </div>

            <div className="order-items">
                <div className="order-items-heading">
                    <span>Item</span>
                    <span>Qty</span>
                    <span>Price</span>
                </div>
                {order.lines.map((line, index) => (
                    <OrderItem key={`${line.itemId}-${index}`} item={line} />
                ))}
            </div>

            <div className="order-footer">
                <span>Total</span>
                <strong>${Number(order.total).toFixed(2)}</strong>
            </div>

            {order.status === "PLACED" && (
                <div className="order-actions">
                    <button className="order-button order-button--primary" onClick={() => runAction("accept")}>Accept order</button>
                    <button className="order-button order-button--secondary" onClick={() => runAction("reject", "Rejected by restaurant")}>Reject</button>
                </div>
            )}
            {order.status === "ACCEPTED" && (
                <div className="order-actions">
                    <button className="order-button order-button--primary" onClick={() => runAction("prepare")}>Start preparing</button>
                </div>
            )}
            {order.status === "PREPARING" && (
                <div className="order-actions">
                    <button className="order-button order-button--primary" onClick={() => runAction("ready")}>Mark ready</button>
                </div>
            )}
        </article>
    )
}

export default Order;