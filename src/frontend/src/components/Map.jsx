import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import { Icon } from 'leaflet';
import {useEffect} from 'react';

import "leaflet/dist/leaflet.css";
import './Map.css';

function Map({restaurants=[], onDutyCouriers=[], customer, userLocation=null}) {
    const DEFAULT_CENTER = [45.49694, -73.57806];

    const centerMap = userLocation
        ? [userLocation.lat, userLocation.lng]
        : DEFAULT_CENTER;
    
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

   function CenterMap({ userLocation }) {
    const map = useMap();

    useEffect(() => {
        if (userLocation?.lat != null && userLocation?.lng != null) {
            map.setView([userLocation.lat, userLocation.lng], 14);
        }
    }, [userLocation, map]);

    return null;
}

    return (
        <MapContainer
            center= {centerMap}
            zoom={14}
            maxZoom={18}
            minZoom={10}
            className="container"
        >
            <CenterMap userLocation={userLocation} />
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