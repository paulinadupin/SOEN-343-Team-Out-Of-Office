import { useEffect, useState } from "react";
import { useAuth } from '../../app/AuthContext';
import { getCurrentRestaurant, getRestaurantMenu, getRestaurantMap} from "../../app/api";
import RestaurantHeader from "../../components/RestaurantHeader";
import MenuItem from "../../components/MenuItem";
import './RestaurantDashboard.css';
import Map from "../../components/Map";

function RestaurantDashboard() {
    const [restaurant, setRestaurant] = useState(null);
    const [menu, setMenu] = useState([]);
    const [error, setError] = useState("");
    const [restaurantMap, setRestaurantMap] = useState();
    const {user} = useAuth();
    

    useEffect(() => {
        getCurrentRestaurant()
            .then((currentRestaurant) => {
                setRestaurant(currentRestaurant);
                return getRestaurantMenu(currentRestaurant.userId);
            })
            .then(setMenu)
            .catch(() => setError("Could not load restaurant information."));
    }, []);

    useEffect(() => {
        getRestaurantMap()
        .then(setRestaurantMap)
        .catch((requestError) => setError(requestError.message));
    }, []);

    return (
        <div>
            {error && <p>{error}</p>}
            {restaurant ? (
                <div>
                    <RestaurantHeader
                        name={restaurant.name}
                        address={`${restaurant.position.lat}, ${restaurant.position.lng}`}
                        status={restaurant.status}
                    />
                    <h1>Restaurant dashboard</h1>
                    <h2>Menu</h2>
                    <div className="menu-items">
                        {menu.length === 0
                        ? <p>No menu items available.</p>
                        : menu.map((item) => (
                            <MenuItem
                                key={item.id}
                                itemId={item.id}
                                restaurantId={restaurant.userId}
                                name={item.name}
                                price={item.price.toFixed(2)}
                                status={item.status}
                                image="/bubble-tea-test.jpg"
                            />
                        ))}
                    </div>
                </div>
            ) : !error ? (
                <p>Loading restaurant information...</p>
            ) : null}
            {restaurantMap && (
                <Map restaurants={restaurantMap.restaurants} onDutyCouriers={restaurantMap.couriers} userLocation={user?.location}/>
            )}
        </div>
    )
}

export default RestaurantDashboard;