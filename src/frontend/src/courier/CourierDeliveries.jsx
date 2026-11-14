import { useEffect, useState } from "react";
import {
    getCourierState,
    acceptOffer,
    declineOffer,
    goOnline,
    goOffline,
    confirmPickup,
    confirmDelivery,
} from "../app/api";

function useSecondsLeft(expiresAt) {
    const [left, setLeft] = useState(0);

    useEffect(() => {
        if (!expiresAt) {
            setLeft(0);
            return;
        }
        const tick = () =>
            setLeft(Math.max(0, Math.ceil((new Date(expiresAt) - Date.now()) / 1000)));
        tick();
        const id = setInterval(tick, 1000);
        return () => clearInterval(id);
    }, [expiresAt]);

    return left;
}

function CourierDeliveries() {
    const [state, setState] = useState({ courierStatus: null, active: null, offer: null });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    async function load() {
        try {
            setState(await getCourierState());
            setError(null);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        load();
        const id = setInterval(load, 5000);
        return () => clearInterval(id);
    }, []);

    const { courierStatus, active, offer } = state;
    const secondsLeft = useSecondsLeft(offer?.expiresAt);

    useEffect(() => {
        if (offer && secondsLeft === 0) load();
    }, [secondsLeft, offer]);

    async function respond(fn) {
        try {
            await fn();
            setError(null);
        } catch (e) {
            setError(e.message);
        }
        await load();
    }

    if (loading) return <p>Loading...</p>;

    return (
        <div className="dashboard-content">
            <h1>Deliveries</h1>
            <p>Status: {courierStatus}</p>
            {error && <p role="alert">{error}</p>}

            {courierStatus === "OFFLINE" && (
                <button onClick={() => respond(goOnline)}>Go online</button>
            )}

            {courierStatus === "IDLE" && (
                <>
                    {offer ? (
                        <section>
                            <h2>New offer ({secondsLeft}s)</h2>
                            <p>
                                {/* FIX: was `delivery.dropoff`, but `delivery` is not defined here */}
                                <strong>{offer.shop}</strong> to {offer.dropoff}
                            </p>
                            <button onClick={() => respond(acceptOffer)}>Accept</button>
                            <button onClick={() => respond(declineOffer)}>Decline</button>
                        </section>
                    ) : (
                        <p>Waiting for offers...</p>
                    )}
                    <button onClick={() => respond(goOffline)}>Go offline</button>
                </>
            )}

            {(courierStatus === "ASSIGNED" || courierStatus === "CARRYING") && (
                <section>
                    <h2>Current delivery</h2>
                    {active && (
                        <p>
                            <strong>{active.shop}</strong> to {active.dropoff}
                        </p>
                    )}
                    {courierStatus === "ASSIGNED" && (
                        <button onClick={() => respond(confirmPickup)}>Confirm pickup</button>
                    )}
                    {courierStatus === "CARRYING" && (
                        <button onClick={() => respond(confirmDelivery)}>Confirm delivery</button>
                    )}
                </section>
            )}
        </div>
    );
}

export default CourierDeliveries;