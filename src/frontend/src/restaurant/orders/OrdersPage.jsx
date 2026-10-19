import Order from "./Order";
import "./OrdersPage.css";

function OrdersPage() {
    return (
        <div className="orders-page">
            <div>
                <h1>Incoming Orders</h1>
                <Order orderType="INCOMING" />
            </div>
            <div>
                <h1>Current Orders</h1>
                <Order orderType="CURRENT" />
            </div>
        </div>
    )
}

export default OrdersPage;