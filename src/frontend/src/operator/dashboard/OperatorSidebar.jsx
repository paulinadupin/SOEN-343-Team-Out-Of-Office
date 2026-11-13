import './Sidebar.css'
import { NavLink } from 'react-router-dom'

function OperatorSidebar() {
    return (
        <aside className="sidebar">
            <nav>
                <NavLink to="/op-dash" end>Dashboard</NavLink>
                <NavLink to="/op-dash/orders">Orders</NavLink>
                <NavLink to="/op-dash/restaurants">Restaurants</NavLink>
            </nav>
        </aside>
    )
}

export default OperatorSidebar