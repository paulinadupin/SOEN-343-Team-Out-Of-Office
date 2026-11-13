import { useEffect, useState } from 'react'
import {
    getActiveOrders,
    getRejectedOrders,
    getCancelledOrders,
    getDeliveredOrders,
    getStalledOrders,
    getIdleCouriers,
    updateOrder,
    reassignOrder,
} from "../../app/api";

const POLL_INTERVAL_MS = 15000

// Instants normally arrive as ISO strings; handle epoch seconds too, just in case
function formatTime(value) {
    if (value == null) return '-'
    const date = typeof value === 'number' ? new Date(value * 1000) : new Date(value)
    return Number.isNaN(date.getTime()) ? '-' : date.toLocaleString()
}

function OrdersTable({ title, orders, emptyText, columns }) {
    return (
        <section>
            <h2>{title}</h2>
            {orders.length === 0 ? (
                <p>{emptyText}</p>
            ) : (
                <table>
                    <thead>
                        <tr>
                            {columns.map((column) => (
                                <th key={column.header}>{column.header}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                {columns.map((column) => (
                                    <td key={column.header}>{column.render(order)}</td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </section>
    )
}

function StalledOrdersTable({ orders, idleCouriers, selectedCourier, onSelectCourier, onReassign, busyId }) {
    return (
        <section>
            <h2>Stalled orders</h2>
            {orders.length === 0 ? (
                <p>No stalled orders.</p>
            ) : (
                <table>
                    <thead>
                        <tr>
                            <th>Order</th>
                            <th>Stuck with</th>
                            <th>Reassign to</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr key={order.orderId}>
                                <td title={order.orderId}>{order.orderId.slice(0, 8)}</td>
                                <td>{order.courierUsername}</td>
                                <td>
                                    <select
                                        value={selectedCourier[order.orderId] ?? ''}
                                        onChange={(e) => onSelectCourier(order.orderId, e.target.value)}
                                    >
                                        <option value="" disabled>Select an idle courier</option>
                                        {idleCouriers.map((courier) => (
                                            <option key={courier.courierId} value={courier.courierId}>
                                                {courier.username}
                                            </option>
                                        ))}
                                    </select>
                                </td>
                                <td>
                                    <button
                                        disabled={busyId === order.orderId || !selectedCourier[order.orderId]}
                                        onClick={() => onReassign(order.orderId)}
                                    >
                                        Reassign
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </section>
    )
}

function OperatorOrders() {
    const [active, setActive] = useState([])
    const [rejected, setRejected] = useState([])
    const [cancelled, setCancelled] = useState([])
    const [delivered, setDelivered] = useState([])
    const [stalled, setStalled] = useState([])
    const [idleCouriers, setIdleCouriers] = useState([])
    const [selectedCourier, setSelectedCourier] = useState({})
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [busyId, setBusyId] = useState(null)

    // loads all six lists; one failing list does not blank the others
    async function load() {
        const [activeResult, rejectedResult, cancelledResult, deliveredResult, stalledResult, idleResult] =
            await Promise.allSettled([
                getActiveOrders(),
                getRejectedOrders(),
                getCancelledOrders(),
                getDeliveredOrders(),
                getStalledOrders(),
                getIdleCouriers(),
            ])

        if (activeResult.status === 'fulfilled') setActive(activeResult.value)
        if (rejectedResult.status === 'fulfilled') setRejected(rejectedResult.value)
        if (cancelledResult.status === 'fulfilled') setCancelled(cancelledResult.value)
        if (deliveredResult.status === 'fulfilled') setDelivered(deliveredResult.value)
        if (stalledResult.status === 'fulfilled') setStalled(stalledResult.value)
        if (idleResult.status === 'fulfilled') setIdleCouriers(idleResult.value)

        const failed = [activeResult, rejectedResult, cancelledResult, deliveredResult, stalledResult, idleResult]
            .find((r) => r.status === 'rejected')
        setError(failed ? failed.reason.message : null)
        setLoading(false)
    }

    useEffect(() => {
        load()
        const timer = setInterval(load, POLL_INTERVAL_MS)
        return () => clearInterval(timer)
    }, [])

    async function act(orderId, action) {
        setBusyId(orderId)
        try {
            await updateOrder(orderId, action)
        } catch (err) {
            setError(err.message)
        }
        await load()
        setBusyId(null)
    }

    function selectCourier(orderId, courierId) {
        setSelectedCourier((current) => ({ ...current, [orderId]: courierId }))
    }

    async function reassign(orderId) {
        const courierId = selectedCourier[orderId]
        if (!courierId) return
        setBusyId(orderId)
        try {
            await reassignOrder(orderId, courierId)
        } catch (err) {
            setError(err.message)
        }
        await load()
        setBusyId(null)
    }

    const baseColumns = [
        { header: 'Order', render: (order) => <span title={order.id}>{order.id.slice(0, 8)}</span> },
        { header: 'Restaurant', render: (order) => order.restaurantId },
        { header: 'Customer', render: (order) => order.customerId },
        { header: 'Placed', render: (order) => formatTime(order.placedAt) },
    ]

    const activeColumns = [
        ...baseColumns,
        { header: 'Status', render: (order) => order.status },
        {
            header: 'Actions',
            render: (order) =>
        order.status === 'PLACED' ? null : (
            <button disabled={busyId === order.id} onClick={() => act(order.id, 'cancel/operator')}>
                Cancel
            </button>
            ),
        },
    ]

    const rejectedColumns = [
        ...baseColumns,
        { header: 'Rejected', render: (order) => formatTime(order.closedAt) },
        { header: 'Reason', render: (order) => order.rejectionReason ?? '-' },
    ]

    const cancelledColumns = [
        ...baseColumns,
        { header: 'Cancelled', render: (order) => formatTime(order.closedAt) },
    ]

    const deliveredColumns = [
        ...baseColumns,
        { header: 'Delivered', render: (order) => formatTime(order.closedAt) },
        {
            header: 'Actions',
            render: (order) => (
                <button disabled={busyId === order.id} onClick={() => act(order.id, 'refund')}>
                    Refund
                </button>
            ),
        },
    ]

    return (
        <div className="dashboard-content">
            <h1>Orders</h1>

            {loading && <p>Loading...</p>}
            {error && <p role="alert">{error}</p>}

            {!loading && (
                <>
                    <StalledOrdersTable
                        orders={stalled}
                        idleCouriers={idleCouriers}
                        selectedCourier={selectedCourier}
                        onSelectCourier={selectCourier}
                        onReassign={reassign}
                        busyId={busyId}
                    />
                    <OrdersTable
                        title="Active orders"
                        orders={active}
                        emptyText="No active orders."
                        columns={activeColumns}
                    />
                    <OrdersTable
                        title="Rejected orders"
                        orders={rejected}
                        emptyText="No rejected orders."
                        columns={rejectedColumns}
                    />
                    <OrdersTable
                        title="Cancelled orders"
                        orders={cancelled}
                        emptyText="No cancelled orders."
                        columns={cancelledColumns}
                    />
                    <OrdersTable
                        title="Delivered orders"
                        orders={delivered}
                        emptyText="No delivered orders."
                        columns={deliveredColumns}
                    />
                </>
            )}
        </div>
    )
}

export default OperatorOrders