import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import { Icon, divIcon } from 'leaflet';
import { useEffect, useState } from 'react';
import { updateOrder } from '../app/api';

import "leaflet/dist/leaflet.css";
import './Map.css';

function Map({
    restaurants = [],
    onDutyCouriers = [],
    customer,
    userLocation = null,
    queueOrders = [],
    canManageQueue = false,
    onOrderAction
}) {
    const [selectedRestaurant, setSelectedRestaurant] = useState(null);
    const [actionError, setActionError] = useState('');
    const DEFAULT_CENTER = [45.49694, -73.57806];

    const centerMap = userLocation
        ? [userLocation.lat, userLocation.lng]
        : DEFAULT_CENTER;

    const courierIcon = new Icon({
        iconUrl: './courier.png',
        iconSize: [40,40]
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

function getQueueStyle(queueSize) {
    if(queueSize > 5 ) {
        return 'red'
    }
    else if (queueSize > 2) {
        return 'orange'
    }
    else {
        return 'green'
    }
}

function createRestaurantIcon(restaurant) {
    const queueSize = restaurant.queueSize ?? 0;
    const queueColor = getQueueStyle(queueSize);

    return divIcon({
        className: 'restaurant-marker-wrapper',
        html: `
            <div class="restaurant-marker ${queueColor}">
                <span>${queueSize}</span>
            </div>
            <div class="restaurant-label">
                ${restaurant.name} - ${queueSize} queued
            </div>
        `,
        iconSize: [150, 50],
        iconAnchor: [20, 20]
    });
}

    async function handleOrderAction(orderId, action) {
        setActionError('');

        try {
            const updatedOrder = await updateOrder(orderId, action);
            onOrderAction?.(updatedOrder);
        } catch (error) {
            setActionError(error.message);
        }
    }

    return (
        <div className="map-layout">
            <MapContainer
                center={centerMap}
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
                <Marker
                    key={restaurant.userId}
                    position={[restaurant.position.lat, restaurant.position.lng]}
                    icon={createRestaurantIcon(restaurant)}
                    eventHandlers={{
                        click: () => setSelectedRestaurant(restaurant)
                    }}
                >
                </Marker>
            ))}
            {onDutyCouriers
                .filter((courier) =>
                    courier.position?.lat != null &&
                    courier.position?.lng != null
                )
                .map((courier) => (
                    <Marker
                        key={courier.userId}
                        position={[
                            courier.position.lat,
                            courier.position.lng
                        ]}
                        icon={courierIcon}
                    >
                        <Popup>{courier.username}</Popup>
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

            {selectedRestaurant && (
                <aside className="restaurant-details">
                    <button
                        type="button"
                        onClick={() => setSelectedRestaurant(null)}
                    >
                        Close
                    </button>
                    <h2>{selectedRestaurant.name}</h2>
                    <p>{selectedRestaurant.queueSize ?? 0} queued</p>

                    {canManageQueue ? (
                        <>
                            {actionError && <p role="alert">{actionError}</p>}
                            {queueOrders.length === 0 ? (
                                <p>No orders in the preparation queue.</p>
                            ) : (
                                queueOrders.map((order) => (
                                    <div className="queue-order" key={order.id}>
                                        <strong>Order {order.id}</strong>
                                        <span>{order.status}</span>
                                        {order.status === 'PLACED' && (
                                            <button
                                                type="button"
                                                onClick={() => handleOrderAction(order.id, 'accept')}
                                            >
                                                Accept
                                            </button>
                                        )}
                                        {order.status === 'ACCEPTED' && (
                                            <button
                                                type="button"
                                                onClick={() => handleOrderAction(order.id, 'prepare')}
                                            >
                                                Start preparing
                                            </button>
                                        )}
                                        {order.status === 'PREPARING' && (
                                            <button
                                                type="button"
                                                onClick={() => handleOrderAction(order.id, 'ready')}
                                            >
                                                Mark ready
                                            </button>
                                        )}
                                    </div>
                                ))
                            )}
                        </>
                    ) : (
                        <p>Preparation queue: {selectedRestaurant.queueSize ?? 0}</p>
                    )}
                </aside>
            )}
        </div>
    )
}

export default Map;