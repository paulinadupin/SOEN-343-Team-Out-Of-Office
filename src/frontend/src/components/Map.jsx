import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import { Icon } from 'leaflet';

import "leaflet/dist/leaflet.css";
import './Map.css';

function Map({restaurants}) {
    const icon = new Icon({
        iconUrl:'./pin.png',
        iconSize:[30,30]}
    );
    return (
        <MapContainer
            center={[45.49694, -73.57806]}
            zoom={14}
            class="container"
        >
            <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                url='https://tile.openstreetmap.org/{z}/{x}/{y}.png'
            />
            {restaurants.map((restaurant) => (
                <Marker position={restaurant.position} icon={icon}>
                    <Popup>
                        {restaurant.name}
                    </Popup>
                </Marker>
            ))}
        </MapContainer>
    )
}

export default Map;