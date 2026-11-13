import './Dashboard.css'
import RestaurantProfile from '../../components/RestaurantProfile'
import Map from '../../components/Map'
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getCustomerMap, getRestaurants } from '../../app/api'

function CustomerDashboard() {
    const navigate = useNavigate();
    const [customerMap, setCustomerMap] = useState(null);
    const [loading, setLoading] = useState(true);
    const [restaurants, setRestaurants] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        getRestaurants()
            .then(setRestaurants)
            .catch((requestError) => setError(requestError.message))
            .finally(() => setLoading(false));
    });

    useEffect(() => {
        getCustomerMap()
        .then(setCustomerMap)
        .catch((requestError) => setError(requestError.message))
    },[]);

    return (
        <div className="dashboard">
            <h1>Customer Dashboard</h1>
            <h2>Browse Restaurants Available</h2>
            <div className="restaurant-list">
                {loading && <p>Loading restaurants...</p>}
                {error && <p role="alert">{error}</p>}
                {!loading && !error && restaurants.map((restaurant) => (
                    <RestaurantProfile
                        key={restaurant.userId}
                        name={restaurant.name}
                        address={restaurant.position
                            ? `${restaurant.position.lat}, ${restaurant.position.lng}`
                            : 'Location unavailable'}
                        status={restaurant.status}
                        onClick={() => navigate(`/restaurants/${restaurant.userId}`)}
                    />
                ))}
            </div>
            {customerMap?.restaurants?.length > 0 && (
                <Map
                    restaurants = {customerMap.restaurants}
                    onDutyCouriers = {customerMap.onDutyCouriers}
                    customer = {customerMap.customer}
                />
            )}
        </div>
    )
}

export default CustomerDashboard;