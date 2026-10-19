function OrderItem({item}) {
    return (
        <div className="order-item">
            <p>{item.name}</p>
            <p>{item.quantity}</p>
        </div>
    )
}

export default OrderItem;