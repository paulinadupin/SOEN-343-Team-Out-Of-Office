import './App.css'
import { Routes, Route} from 'react-router-dom'
import Header from './Header.jsx'
import OperatorSidebar from './OperatorSidebar.jsx'
import MenuItem from './MenuItem.jsx'
import RestaurantHeader from './RestaurantHeader.jsx'
import OperatorLayout from './OperatorLayout.jsx'
import OperatorDashboard from './OperatorDashboard.jsx'
import OperatorOrders from './OperatorOrders.jsx'

function App() {
  return (
    <div>
      <Header />
      <Routes>
        <Route path="/op-dash" element={<OperatorLayout />}>
          <Route index element={<OperatorDashboard />} />
  <Route path="orders" element={<OperatorOrders />} />
        </Route>
        </Routes>
      </div>
  )
}

export default App