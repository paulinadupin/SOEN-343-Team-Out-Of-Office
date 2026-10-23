function CourierDeliveries() {
    // Placeholder data - replace with real orders later
    const deliveries = [
        { id: 1, shop: "Mimi's Tea", dropoff: '45 Oak Ave', status: 'Ready for pickup' },
        { id: 2, shop: "Mimi's Tea", dropoff: '88 Pine St', status: 'On the way' },
    ]

    return (
        <div className="dashboard-content">
            <h1>Deliveries</h1>
            <p>Your current and available deliveries.</p>
            <ul>
                {deliveries.map((d) => (
                    <li key={d.id}>
                        <strong>{d.shop}</strong> to {d.dropoff} ({d.status})
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default CourierDeliveries
