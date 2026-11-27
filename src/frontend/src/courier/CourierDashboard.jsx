import { useState } from 'react'
import Map from "../components/Map";
import { useEffect } from "react";
import { useAuth } from '../app/AuthContext';
import { getCourierMap } from "../app/api";
import "./CourierDashboard.css";

function CourierDashboard() {
    const [courierMap, setCourierMap] = useState();
    const [error, setError] = useState();
    const {user} = useAuth();

    useEffect(() => {
        getCourierMap()
        .then(setCourierMap)
        .catch((requestError) => setError(requestError.message));
    }, []);

    return (
        <div className="dashboard-content courier-dashboard">
            <h1>Courier Overview</h1>
            <h2 className="courier-section-title">Today</h2>
            {error && <p className="courier-error" role="alert">{error}</p>}
            <ul>
                <li>
                    <span>Deliveries completed</span>
                    <strong>0</strong>
                </li>
                <li>
                    <span>Active deliveries</span>
                    <strong>0</strong>
                </li>
                <li>
                    <span>Earned so far</span>
                    <strong>$0.00</strong>
                </li>
            </ul>
            {courierMap && (
                <Map
                    restaurants={courierMap.restaurants}
                    onDutyCouriers={courierMap.couriers}
                    customer={courierMap.customer}
                    userLocation={user?.location}
                />
            )}
        </div>
    )
}

export default CourierDashboard
