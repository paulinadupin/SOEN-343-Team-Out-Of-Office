import './Sidebar.css'
import { NavLink } from 'react-router-dom'

function OperatorSidebar() {
    return (
        <aside className="sidebar">
            <nav>
                <NavLink to="/op-dash" end>Overview</NavLink>
<NavLink to="/op-dash/orders">Orders</NavLink>
            </nav>
        </aside>
    )
}

export default OperatorSidebar