import OrderItem from "./OrderItem";

function Order({orderType}) {
    return (
        <div>
            <h2>Order #</h2>
            <p>Customer: John Doe</p>
            <p>Items:</p>
            <OrderItem item={{ name: "Milk Tea", quantity: 2 }} />
            {orderType === "INCOMING" && 
            <div>
                <button> Accept</button>
                <button> Reject</button>
            </div>
            }
            {orderType === "CURRENT" &&
            <button> Mark as Complete</button>
            }
        </div>
    )
}

export default Order;