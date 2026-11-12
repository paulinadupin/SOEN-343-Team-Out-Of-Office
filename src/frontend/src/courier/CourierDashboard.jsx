import { useState } from 'react'

function CourierDashboard() {

    return (
        <div className="dashboard-content">
            <h1>Courier Overview</h1>
            <h2>Today</h2>
            <ul>
                <li>Deliveries completed: 0</li>
                <li>Active deliveries: 0</li>
                <li>Earned so far: $0.00</li>
            </ul>
        </div>
    )
}

export default CourierDashboard
