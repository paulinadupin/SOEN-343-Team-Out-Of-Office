import { useEffect, useState } from 'react'

const POLL_INTERVAL_MS = 15000
// Calls the Spring backend directly, so no Vite proxy is needed.
// If your other pages use a shared API base URL, import it here instead.
const API_BASE_URL = 'http://localhost:8080'

// Instants normally arrive as ISO strings; handle epoch seconds too, just in case
function formatTime(value) {
    if (value == null) return '-'
    const date = typeof value === 'number' ? new Date(value * 1000) : new Date(value)
    return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString()
}

function OperatorOrders() {
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        let cancelled = false

        async function loadRejectedOrders() {
            try {
                const response = await fetch(`${API_BASE_URL}/api/orders/rejected`, { credentials: 'include' })
                if (response.status === 401) throw new Error('Please log in again.')
                if (response.status === 403) throw new Error('An operator account is required to view this page.')
                if (!response.ok) throw new Error(`Could not load orders (${response.status}).`)
                const data = await response.json()
                if (!cancelled) {
                    setOrders(data)
                    setError(null)
                }
            } catch (err) {
                if (!cancelled) setError(err.message)
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        loadRejectedOrders()
        const timer = setInterval(loadRejectedOrders, POLL_INTERVAL_MS)
        return () => {
            cancelled = true
            clearInterval(timer)
        }
    }, [])

    return (
        <div className="dashboard-content">
            <h1>Orders</h1>
            <h2>Rejected orders</h2>

            {loading && <p>Loading...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && !error && orders.length === 0 && <p>No rejected orders.</p>}

            {orders.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>Order</th>
                            <th>Restaurant</th>
                            <th>Customer</th>
                            <th>Placed</th>
                            <th>Rejected</th>
                            <th>Reason</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td title={order.id}>{order.id.slice(0, 8)}</td>
                                <td>{order.restaurantId}</td>
                                <td>{order.customerId}</td>
                                <td>{formatTime(order.placedAt)}</td>
                                <td>{formatTime(order.closedAt)}</td>
                                <td>{order.rejectionReason ?? '-'}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    )
}

export default OperatorOrders