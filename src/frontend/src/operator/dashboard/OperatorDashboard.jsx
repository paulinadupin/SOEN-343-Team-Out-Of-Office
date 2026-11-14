import { useEffect, useState } from "react";
import Map from "../../components/Map";
import {getOnDutyCouriers, getRestaurants } from '../../app/api';

function OperatorDashboard() {
    const [onDutyCouriers, setOnDutyCouriers] = useState([]);
    const [restaurants, setRestaurants] = useState([]);
    const [error, setError] = useState("");


    useEffect(() => {
    getOnDutyCouriers()
        .then((couriers) => {
            setOnDutyCouriers(couriers);
        })
        .catch(() => setError("Could not load on-duty couriers."));
    }, []);

    useEffect(() => {
        getRestaurants()
            .then(setRestaurants)
            .catch((requestError) => setError(requestError.message))
    }, []);

    return (
        <div className="dashboard-content">
            <h1>Some Operator Analytics</h1>
            <p>stocks are up yay!</p>
            <Map restaurants={restaurants} onDutyCouriers={onDutyCouriers}/>
        </div>
    )
}

export default OperatorDashboard