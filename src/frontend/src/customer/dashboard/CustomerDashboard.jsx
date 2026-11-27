import './Dashboard.css'
import RestaurantProfile from '../../components/RestaurantProfile'
import Map from '../../components/Map'
import { useEffect, useState } from 'react'
import { useAuth } from '../../app/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom'
import { getCustomerMap, getRestaurants } from '../../app/api'

function CustomerDashboard() {
    const navigate = useNavigate();
    const location = useLocation();
    const [customerMap, setCustomerMap] = useState(null);
    const [loading, setLoading] = useState(true);
    const [restaurants, setRestaurants] = useState([]);
    const [error, setError] = useState(null);
    const [orderPlaced, setOrderPlaced] = useState(false);
    const {user} = useAuth();
    

    useEffect(() => {
        getRestaurants()
            .then(setRestaurants)
            .catch((requestError) => setError(requestError.message))
            .finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        getCustomerMap()
        .then(setCustomerMap)
        .catch((requestError) => setError(requestError.message))
    },[]);

    useEffect(() => {
        // If there is no order placed
        if(!location.state?.orderPlaced) return;
        
        setOrderPlaced(true);

        const timeout = setTimeout(() => {
            setOrderPlaced(false);
            //Prevent the popup from appearing again
            navigate("/", {replace: true, state: null});
        },1500);

        return () => clearTimeout(timeout);
    }, [location.state, navigate]);

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
            {customerMap && (
                <div>
                    <h1>Map</h1>
                    <Map
                        restaurants = {customerMap.restaurants}
                        onDutyCouriers = {customerMap.couriers}
                        customer = {customerMap.customer}
                        userLocation ={user?.location}
                    />
                </div>
            )}
            {orderPlaced && <span className="order-placed" role="status">Order Placed!</span>}
        </div>
    )
}

export default CustomerDashboard;