import "./OrderItem.css";

function OrderItem({item}) {
    return (
        <div className="order-item">
            <p className="order-item-name">{item.itemId}</p>
            <p className="order-item-quantity">{item.quantity}</p>
            <p className="order-item-price">${Number(item.unitPrice).toFixed(2)}</p>
        </div>
    )
}

export default OrderItem;