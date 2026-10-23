function CourierHistory() {
    // Placeholder data - replace with real orders later
    const past = [
        { id: 101, shop: "Mimi's Tea", date: '2026-10-17', pay: '8.50' },
        { id: 102, shop: "Mimi's Tea", date: '2026-10-16', pay: '6.25' },
    ]

    return (
        <div className="dashboard-content">
            <h1>Delivery History</h1>
            <p>Your completed deliveries.</p>
            <ul>
                {past.map((d) => (
                    <li key={d.id}>
                        {d.date}: {d.shop} (${d.pay})
                    </li>
                ))}
            </ul>
        </div>
    )
}

export default CourierHistory
