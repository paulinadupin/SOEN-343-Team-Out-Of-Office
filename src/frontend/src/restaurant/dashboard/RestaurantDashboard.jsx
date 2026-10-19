import { useEffect, useState } from "react";
import { getCurrentRestaurant } from "../../app/api";
import RestaurantHeader from "../../components/RestaurantHeader";
import MenuItem from "../../components/MenuItem";

function RestaurantDashboard() {
    const [restaurant, setRestaurant] = useState(null);
    const [error, setError] = useState("");

    useEffect(() => {
        getCurrentRestaurant()
            .then(setRestaurant)
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
                    <MenuItem name="Milk Tea" price={12.99} description="Delicious Milk Tea" image="/bubble-tea-test.jpg" />
                </div>
            ) : !error ? (
                <p>Loading restaurant information...</p>
            ) : null}
        </div>
    )
}

export default RestaurantDashboard;