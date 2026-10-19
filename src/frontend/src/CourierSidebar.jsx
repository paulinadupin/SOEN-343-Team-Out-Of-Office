import './Sidebar.css'
import { NavLink } from 'react-router-dom'

function CourierSidebar() {
    return (
        <aside className="sidebar">
            <h2>Courier</h2>
            <nav>
                <NavLink to="/courier" end>Overview</NavLink>
                <NavLink to="/courier/deliveries">Deliveries</NavLink>
                <NavLink to="/courier/history">History</NavLink>
                <NavLink to="/courier/earnings">Earnings</NavLink>
            </nav>
        </aside>
    )
}

export default CourierSidebar
