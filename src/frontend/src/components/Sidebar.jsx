import { NavLink } from "react-router-dom";
import { useAuth } from "../app/AuthContext";
import { ROLES } from "../app/roles";
import "../Sidebar.css";

const linksByRole = {
    [ROLES.CUSTOMER]: [
        { to: "/", label: "Dashboard", end: true },
        { to: "/cart", label: "Cart" },
    ],
    [ROLES.RESTAURANT]: [
        { to: "/dashboard", label: "Dashboard", end: true },
        { to: "/orders", label: "Orders" },
    ],
    [ROLES.COURIER]: [
        //to: "/courier", label: "Overview", end: true },
        { to: "/deliveries", label: "Deliveries" },
       // to: "/history", label: "History" },
       // to: "/earnings", label: "Earnings" },
    ],
    [ROLES.OPERATOR]: [
       // to: "/operator", label: "Dashboard", end: true },
        { to: "/operator/orders", label: "Orders" },
        { to: "/operator/restaurants", label: "Restaurants"},
    ],
};

function Sidebar() {
    const { user } = useAuth();
    const links = user ? linksByRole[user.accountType] ?? [] : [];

    if (!user || links.length === 0) {
        return null;
    }

    return (
        <aside className="sidebar">
            <h2>{user.accountType}</h2>
            <nav>
                {links.map(({ to, label, end }) => (
                    <NavLink key={to} to={to} end={end}>
                        {label}
                    </NavLink>
                ))}
            </nav>
        </aside>
    );
}

export default Sidebar;
