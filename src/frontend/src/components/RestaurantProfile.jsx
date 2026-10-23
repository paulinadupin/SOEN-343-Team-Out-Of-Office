import './RestaurantProfile.css';
import { formatRestaurantStatus } from '../app/formatRestaurantStatus';

function RestaurantProfile({ name, address, status, onClick }) {
    return (
        <div
            className="restaurant-profile"
            onClick={onClick}
            onKeyDown={(event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    onClick();
                }
            }}
            role="button"
            tabIndex="0"
        >
            <h1>{name}</h1>
            <p>{address}</p>
            <p>Status: {formatRestaurantStatus(status)}</p>
        </div>
    );
}

export default RestaurantProfile;