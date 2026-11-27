import { useEffect, useState } from 'react'
import { getRestaurants, updateRestaurantStatus } from "../../app/api";
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
    const [updatingStatus, setUpdatingStatus] = useState(false)

    useEffect(() => {
        getRestaurants()
            .then((data) => {
                setRestaurants(data)
                setError(null)
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [])

    async function changeStatus(restaurantId, status) {
        setUpdatingStatus(true)
        setError(null)
        try {
            const updatedRestaurant = await updateRestaurantStatus(restaurantId, status)
            setRestaurants((current) =>
                current.map((restaurant) =>
                    (restaurant.userId ?? restaurant.id) === restaurantId
                        ? { ...restaurant, ...updatedRestaurant }
                        : restaurant
                )
            )
        } catch (requestError) {
            setError(requestError.message)
        } finally {
            setUpdatingStatus(false)
        }
    }

    return (
        <div className="dashboard-content operator-restaurants">
            <h1>Restaurants</h1>

            {loading && <p>Loading...</p>}
            {error && <p role="alert">{error}</p>}
            {!loading && restaurants.length === 0 && !error && <p>No restaurants registered.</p>}

            {restaurants.length > 0 && (
                <div className="table-scroll">
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
                        {restaurants.map((restaurant) => {
                            const current = restaurant.status ?? restaurant.restaurantStatus
                            const restaurantId = restaurant.userId ?? restaurant.id
                            return (
                                <tr key={restaurantId}>
                                    <td>{restaurantId ?? '-'}</td>
                                    <td>{restaurant.name ?? '-'}</td>
                                    <td>{current ? formatRestaurantStatus(current) : '-'}</td>
                                    <td>
                                        {STATUS_ACTIONS.map(({ status, label }) => (
                                            <button
                                                key={status}
                                                disabled={updatingStatus || current === status}
                                                onClick={() => changeStatus(restaurantId, status)}
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
                </div>
            )}
        </div>
    )
}

export default OperatorRestaurants