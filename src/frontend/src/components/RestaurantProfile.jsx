import './RestaurantProfile.css';
import { formatRestaurantStatus } from '../app/formatRestaurantStatus';

function RestaurantProfile({ name, address, status, onClick }) {
    const statusLabel = formatRestaurantStatus(status);

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
            <h2>{name}</h2>
            <p className="restaurant-profile-address">{address}</p>
            <span className={`restaurant-profile-status ${statusLabel}`}>
                {statusLabel}
            </span>
        </div>
    );
}

export default RestaurantProfile;