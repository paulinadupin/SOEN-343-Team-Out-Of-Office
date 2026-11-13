import { useEffect, useState } from 'react'

// Same base URL as OperatorOrders; swap for your shared API helper/constant if you have one
const API_BASE_URL = 'http://localhost:8080'

function OperatorRestaurants() {
    const [restaurants, setRestaurants] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        let cancelled = false

        async function loadRestaurants() {
            try {
                const response = await fetch(`${API_BASE_URL}/api/auth/restaurants`, { credentials: 'include' })
                if (!response.ok) throw new Error(`Could not load restaurants (${response.status}).`)
                const data = await response.json()
                if (!cancelled) setRestaurants(data)
            } catch (err) {
                if (!cancelled) setError(err.message)
            } finally {
                if (!cancelled) setLoading(false)
            }
        }

        loadRestaurants()
        return () => {
            cancelled = true
        }
    }, [])

    return (
        <div className="dashboard-content">
            <h1>Restaurants</h1>

            {loading && <p>Loading...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && !error && restaurants.length === 0 && <p>No restaurants registered.</p>}

            {restaurants.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {restaurants.map((restaurant, index) => (
                            <tr key={restaurant.id ?? restaurant.userId ?? index}>
                                <td>{restaurant.id ?? restaurant.userId ?? '-'}</td>
                                <td>{restaurant.name ?? '-'}</td>
                                <td>{restaurant.status ?? restaurant.restaurantStatus ?? '-'}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    )
}

export default OperatorRestaurants