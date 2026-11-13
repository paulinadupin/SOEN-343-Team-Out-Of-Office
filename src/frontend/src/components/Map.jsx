import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';

import "leaflet/dist/leaflet.css";
import './Map.css';

// Reference: https://www.youtube.com/watch?v=jD6813wGdBA

//Map Canvas: canvas scaled to [minLon, maxLon] × [minLat, maxLat]

function Map({restaurants=[], onDutyCouriers=[], customer}) {
    const restaurantIcon = new Icon({
        iconUrl:'./pin.png',
        iconSize:[30,30]
    });

    const courierIcon = new Icon({
        iconUrl: './courier.png',
        iconSize: [30,30]
    });

    const customerIcon = new Icon({
        iconUrl: './customer-pin.png',
        iconSize: [30,30]
    });

    return (
        <MapContainer
            center={[45.49694, -73.57806]}
            zoom={14}
            maxZoom={18}
            minZoom={10}
            className="container"
        >
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                url='https://tile.openstreetmap.org/{z}/{x}/{y}.png'
            />
            {restaurants.map((restaurant) => (
                <Marker key={restaurant.userId} position={restaurant.position} icon={restaurantIcon}>
                    <Popup>
                        {restaurant.name}
                    </Popup>
                </Marker>
            ))}
            {onDutyCouriers.map((courier)=> (
                <Marker key={courier.userId} position={courier.position} icon={courierIcon}>
                    <Popup>
                        {courier.username}
                    </Popup>
                </Marker>
            ))}
            {customer?.deliveryAddress && (
                <Marker
                    key={customer.userId}
                    position={[
                        customer.deliveryAddress.lat,
                        customer.deliveryAddress.lng
                    ]}
                    icon={customerIcon}
                >
                    <Popup>{customer.name}</Popup>
                </Marker>
            )}
        </MapContainer>
    )
}

export default Map;