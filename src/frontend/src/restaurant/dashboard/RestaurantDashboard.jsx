import { useEffect, useState } from "react";
import { getCurrentRestaurant, getRestaurantMenu } from "../../app/api";
import RestaurantHeader from "../../components/RestaurantHeader";
import MenuItem from "../../components/MenuItem";
import './RestaurantDashboard.css';

function RestaurantDashboard() {
    const [restaurant, setRestaurant] = useState(null);
    const [menu, setMenu] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        getCurrentRestaurant()
            .then((currentRestaurant) => {
                setRestaurant(currentRestaurant);
                return getRestaurantMenu(currentRestaurant.userId);
            })
            .then(setMenu)
            .catch(() => setError("Could not load restaurant information."));
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
        </div>
    )
}

export default RestaurantDashboard;