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
import "./CourierDeliveries.css";

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

function formatCourierStatus(status) {
    return status
        ? status.toLowerCase().replace(/_/g, " ").replace(/^\w/, (letter) => letter.toUpperCase())
        : "Unknown";
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

    if (loading) {
        return (
            <div className="dashboard-content courier-deliveries">
                <h1>Deliveries</h1>
                <p className="deliveries-loading">Loading delivery status...</p>
            </div>
        );
    }

    return (
        <div className="dashboard-content courier-deliveries">
            <div className="deliveries-heading">
                <div>
                    <p className="deliveries-eyebrow">Courier workspace</p>
                    <h1>Deliveries</h1>
                </div>
                <span className={`courier-status ${courierStatus?.toLowerCase()}`}>
                    {formatCourierStatus(courierStatus)}
                </span>
            </div>
            {error && <p className="deliveries-error" role="alert">{error}</p>}

            {courierStatus === "OFFLINE" && (
                <button className="delivery-button primary" onClick={() => respond(goOnline)}>
                    Go online
                </button>
            )}

            {courierStatus === "IDLE" && (
                <>
                    {offer ? (
                        <section className="delivery-card offer-card">
                            <div className="delivery-card-heading">
                                <div>
                                    <p className="deliveries-eyebrow">New request</p>
                                    <h2>Delivery offer</h2>
                                </div>
                                <span className="offer-countdown">{secondsLeft}s</span>
                            </div>
                            <div className="delivery-route">
                                <strong>{offer.shop}</strong>
                                <span>to</span>
                                <strong>{offer.dropoff}</strong>
                            </div>
                            <div className="delivery-actions">
                                <button className="delivery-button primary" onClick={() => respond(acceptOffer)}>
                                    Accept offer
                                </button>
                                <button className="delivery-button secondary" onClick={() => respond(declineOffer)}>
                                    Decline
                                </button>
                            </div>
                        </section>
                    ) : (
                        <div className="delivery-empty-state">
                            <h2>Waiting for offers</h2>
                            <p>New delivery requests will appear here while you are online.</p>
                        </div>
                    )}
                    <button className="delivery-button outline" onClick={() => respond(goOffline)}>
                        Go offline
                    </button>
                </>
            )}

            {(courierStatus === "ASSIGNED" || courierStatus === "CARRYING") && (
                <section className="delivery-card active-delivery">
                    <div className="delivery-card-heading">
                        <div>
                            <p className="deliveries-eyebrow">In progress</p>
                            <h2>Current delivery</h2>
                        </div>
                    </div>
                    {active && (
                        <div className="delivery-route">
                            <strong>{active.shop}</strong>
                            <span>to</span>
                            <strong>{active.dropoff}</strong>
                        </div>
                    )}
                    {courierStatus === "ASSIGNED" && (
                        <button className="delivery-button primary" onClick={() => respond(confirmPickup)}>
                            Confirm pickup
                        </button>
                    )}
                    {courierStatus === "CARRYING" && (
                        <button className="delivery-button primary" onClick={() => respond(confirmDelivery)}>
                            Confirm delivery
                        </button>
                    )}
                </section>
            )}
        </div>
    );
}

export default CourierDeliveries;