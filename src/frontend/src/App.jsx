import './App.css'
import { Routes, Route} from 'react-router-dom'
import CustomerHeader from './CustomerHeader.jsx'
import OperatorSidebar from './OperatorSidebar.jsx'
import MenuItem from './MenuItem.jsx'
import RestaurantHeader from './RestaurantHeader.jsx'
import OperatorLayout from './OperatorLayout.jsx'
import OperatorDashboard from './OperatorDashboard.jsx'
import OperatorOrders from './OperatorOrders.jsx'
import OperatorHeader from './OperatorHeader.jsx'
import CourierLayout from './CourierLayout.jsx'
import CourierDashboard from './CourierDashboard.jsx'
import CourierDeliveries from './CourierDeliveries.jsx'
import CourierHistory from './CourierHistory.jsx'
import CourierEarnings from './CourierEarnings.jsx'

function App() {
  return (
      <Routes>
        <Route path="/op-dash" element={<OperatorLayout />}>
          <Route index element={<OperatorDashboard />} />
  <Route path="orders" element={<OperatorOrders />} />
        </Route>
        <Route path="/courier" element={<CourierLayout />}>
  <Route index element={<CourierDashboard />} />
  <Route path="deliveries" element={<CourierDeliveries />} />
  <Route path="history" element={<CourierHistory />} />
  <Route path="earnings" element={<CourierEarnings />} />
</Route>
        </Routes>
  )
}

export default App