import { useState } from 'react'

function CourierDashboard() {
    const [online, setOnline] = useState(false)

    return (
        <div className="dashboard-content">
            <h1>Courier Overview</h1>
            <p>Status: {online ? 'Online - accepting deliveries' : 'Offline'}</p>
            <button onClick={() => setOnline(!online)}>
                {online ? 'Go Offline' : 'Go Online'}
            </button>

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
