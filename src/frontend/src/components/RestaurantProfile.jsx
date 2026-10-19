import './RestaurantProfile.css';

function RestaurantProfile({ name, address, status }) {
    return (
        <div className="restaurant-profile">
            <h1>{name}</h1>
            <p>{address}</p>
            <p>Status: {status}</p>
        </div>
    );
}

export default RestaurantProfile;