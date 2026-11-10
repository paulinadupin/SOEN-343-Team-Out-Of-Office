import RestaurantHeader from "../../components/RestaurantHeader";
import MenuItem from "../../components/MenuItem";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getRestaurantMenu, getRestaurants } from "../../app/api";
import { formatRestaurantStatus } from "../../app/formatRestaurantStatus";
import './Browse.css';

function displayMenuStatus(status) {
    return status
        ? status.replace(/_/g, ' ').toLowerCase().replace(/^\w/, (letter) => letter.toUpperCase())
        : 'Unknown';
}

function Browse() {
    const { restaurantId } = useParams();
    const [restaurant, setRestaurant] = useState(null);
    const [menu, setMenu] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        Promise.all([getRestaurants(), getRestaurantMenu(restaurantId)])
            .then(([restaurants, restaurantMenu]) => {
                const selectedRestaurant = restaurants.find(
                    (candidate) => candidate.userId === Number(restaurantId)
                );

                if (!selectedRestaurant) {
                    throw new Error('Restaurant not found');
                }

                setRestaurant(selectedRestaurant);
                setMenu(restaurantMenu);
            })
            .catch((requestError) => setError(requestError.message))
            .finally(() => setLoading(false));
    }, [restaurantId]);

    if (loading) {
        return <div className="browse"><p>Loading restaurant...</p></div>;
    }

    if (error) {
        return <div className="browse"><p role="alert">{error}</p></div>;
    }

    const address = restaurant.position
        ? `${restaurant.position.lat}, ${restaurant.position.lng}`
        : 'Location unavailable';

    return (
        <div className="browse">
            <RestaurantHeader
                name={restaurant.name}
                address={address}
                status={restaurant.status}
            />
            <h1>Menu</h1>
            <div className="menu-items">
                {menu.length === 0 && <p>No menu items available.</p>}
                {menu.map((menuItem) => (
                    <MenuItem
                        key={menuItem.id}
                        itemId={menuItem.id}
                        restaurantId={restaurantId}
                        name={menuItem.name}
                        price={menuItem.price.toFixed(2)}
                        status={displayMenuStatus(menuItem.status)}
                        image="/bubble-tea-test.jpg"
                    />
                ))}
            </div>
        </div>
    )
}

export default Browse;
