import { useEffect, useState } from 'react'
import { getRestaurants } from "../../app/api";
import { formatRestaurantStatus } from "../../app/formatRestaurantStatus";
import "./OperatorRestaurants.css";

const STATUS_ACTIONS = [
    { status: 'OPEN', label: 'Open' },
    { status: 'PAUSED', label: 'Pause' },
    { status: 'CLOSED', label: 'Close' },
]

function OperatorRestaurants() {
    const [restaurants, setRestaurants] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        getRestaurants()
            .then((data) => {
                setRestaurants(data)
                setError(null)
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    // DUMMY: only updates the status shown on this page. Nothing is sent to the backend,
    // so a reload brings back the real status.
    function changeStatus(index, status) {
        setRestaurants((current) =>
            current.map((restaurant, i) => (i === index ? { ...restaurant, status } : restaurant))
        )
    }

    return (
        <div className="dashboard-content operator-restaurants">
            <h1>Restaurants</h1>

            {loading && <p>Loading...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && restaurants.length === 0 && !error && <p>No restaurants registered.</p>}

            {restaurants.length > 0 && (
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Status</th>
                            <th>Change status</th>
                        </tr>
                    </thead>
                    <tbody>
                        {restaurants.map((restaurant, index) => {
                            const current = restaurant.status ?? restaurant.restaurantStatus
                            return (
                                <tr key={restaurant.id ?? restaurant.userId ?? index}>
                                    <td>{restaurant.id ?? restaurant.userId ?? '-'}</td>
                                    <td>{restaurant.name ?? '-'}</td>
                                    <td>{current ? formatRestaurantStatus(current) : '-'}</td>
                                    <td>
                                        {STATUS_ACTIONS.map(({ status, label }) => (
                                            <button
                                                key={status}
                                                disabled={current === status}
                                                onClick={() => changeStatus(index, status)}
                                            >
                                                {label}
                                            </button>
                                        ))}
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </table>
            )}
        </div>
    )
}

export default OperatorRestaurants