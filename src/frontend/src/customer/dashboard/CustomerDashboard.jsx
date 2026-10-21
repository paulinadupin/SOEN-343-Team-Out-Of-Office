import './Dashboard.css'
import RestaurantProfile from '../../components/RestaurantProfile'
import {useNavigate} from 'react-router-dom'

function CustomerDashboard() {
    const navigate = useNavigate();

    return (
        <div className="dashboard">
            <h1>Customer Dashboard</h1>
            <h2>Restaurants Available</h2>
            <div className="restaurant-list">
                {/* TODO: Get the list of restaurants from the backend */}
                <RestaurantProfile name="Bubble Tea House" address="123 Main St, City, Country" status="Open" />
            </div>
        </div>
    )
}

export default CustomerDashboard;