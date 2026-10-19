import { Routes, Route, Navigate } from "react-router-dom";
import AppLayout  from "../components/AppLayout";
import { ProtectedRoute } from "./ProtectedRoute";
import { Login } from "../components/Login";
import { ROLES } from "./roles";
import RestaurantDashboard from "../restaurant/dashboard/RestaurantDashboard";
import CustomerDashboard from "../customer/dashboard/CustomerDashboard";
import Cart from "../customer/cart/Cart";
import OrdersPage from "../restaurant/orders/OrdersPage";
import Browse from "../customer/browse/Browse";


function RoleHome({ accountType }) {
  return <h1>{accountType} home</h1>;
}

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>

          <Route element={<ProtectedRoute role={ROLES.CUSTOMER} />}>
            <Route path="/" element={<CustomerDashboard />} />
            <Route path="/browse" element={<Browse />} />
            <Route path="/cart" element={<Cart />} />
          </Route>

          <Route element={<ProtectedRoute role={ROLES.RESTAURANT} />}>
            <Route path="/dashboard" element={<RestaurantDashboard />} />
            <Route path="/orders" element={<OrdersPage />} /> 
          </Route>

          <Route element={<ProtectedRoute role={ROLES.COURIER} />}>
            <Route path="/courier" element={<RoleHome accountType={ROLES.COURIER} />} />
          </Route>

          <Route element={<ProtectedRoute role={ROLES.OPERATOR} />}>
            <Route path="/operator" element={<RoleHome accountType={ROLES.OPERATOR} />} />
          </Route>

        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRouter;