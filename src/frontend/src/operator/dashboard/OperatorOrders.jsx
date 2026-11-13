import { useEffect, useState } from 'react'
import { getActiveOrders, getRejectedOrders, getCancelledOrders, updateOrder } from "../../app/api";

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

function OperatorOrders() {
    const [active, setActive] = useState([])
    const [rejected, setRejected] = useState([])
    const [cancelled, setCancelled] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [busyId, setBusyId] = useState(null)

    // loads all three lists; one failing list does not blank the others
    async function load() {
        const [activeResult, rejectedResult, cancelledResult] = await Promise.allSettled([
            getActiveOrders(),
            getRejectedOrders(),
            getCancelledOrders(),
        ])

        if (activeResult.status === 'fulfilled') setActive(activeResult.value)
        if (rejectedResult.status === 'fulfilled') setRejected(rejectedResult.value)
        if (cancelledResult.status === 'fulfilled') setCancelled(cancelledResult.value)

        const failed = [activeResult, rejectedResult, cancelledResult].find((r) => r.status === 'rejected')
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
            render: (order) => (
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
        {
            header: 'Actions',
            render: (order) => (
                <button disabled={busyId === order.id} onClick={() => act(order.id, 'restore')}>
                    Make active again
                </button>
            ),
        },
    ]

    const cancelledColumns = [
        ...baseColumns,
        { header: 'Cancelled', render: (order) => formatTime(order.closedAt) },
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
                </>
            )}
        </div>
    )
}

export default OperatorOrders