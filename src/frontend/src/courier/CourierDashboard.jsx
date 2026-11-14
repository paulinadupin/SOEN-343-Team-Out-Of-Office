import { useState } from 'react'
import Map from "../components/Map";
import { useEffect} from "react";
import { getCourierMap } from "../app/api";

function CourierDashboard() {
    const [courierMap, setCourierMap] = useState();
    const [error, setError] = useState();

    useEffect(() => {
        getCourierMap()
        .then(setCourierMap)
        .catch((requestError) => setError(requestError.message));
    }, []);

    return (
        <div className="dashboard-content">
            <h1>Courier Overview</h1>
            <h2>Today</h2>
            <ul>
                <li>Deliveries completed: 0</li>
                <li>Active deliveries: 0</li>
                <li>Earned so far: $0.00</li>
            </ul>
            {courierMap && (
                <Map
                    restaurants={courierMap.restaurants}
                    onDutyCouriers={courierMap.couriers}
                    customer={courierMap.customer}
                />
            )}
        </div>
    )
}

export default CourierDashboard
