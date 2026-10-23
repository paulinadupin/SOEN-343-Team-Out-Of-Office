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
import Register from "../components/Register";
import OperatorDashboard from "../operator/dashboard/OperatorDashboard";
import OperatorOrders from "../operator/dashboard/OperatorOrders";
import CourierDashboard from "../courier/CourierDashboard";
import CourierDeliveries from "../courier/CourierDeliveries";
import CourierHistory from "../courier/CourierHistory";
import CourierEarnings from "../courier/CourierEarnings";

export function AppRouter() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>

          <Route element={<ProtectedRoute role={ROLES.CUSTOMER} />}>
            <Route path="/" element={<CustomerDashboard />} />
            <Route path="/restaurants/:restaurantId" element={<Browse />} />
            <Route path="/cart" element={<Cart />} />
          </Route>

          <Route element={<ProtectedRoute role={ROLES.RESTAURANT} />}>
            <Route path="/dashboard" element={<RestaurantDashboard />} />
            <Route path="/orders" element={<OrdersPage />} /> 
          </Route>

          <Route element={<ProtectedRoute role={ROLES.COURIER} />}>
            <Route path="/courier" element={<CourierDashboard/>} />
            <Route path="/deliveries" element={<CourierDeliveries />} />
            <Route path="/history" element={<CourierHistory />} />
            <Route path="/earnings" element={<CourierEarnings />} />
          </Route>

          <Route element={<ProtectedRoute role={ROLES.OPERATOR} />}>
            <Route path="/operator" element={ <OperatorDashboard/>} />
            <Route path="/operator/orders" element={<OperatorOrders />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRouter;