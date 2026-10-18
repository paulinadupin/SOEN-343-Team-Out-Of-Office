import { Routes, Route, Navigate } from "react-router-dom";
import AppLayout  from "../components/AppLayout";
import { ProtectedRoute } from "./ProtectedRoute";
import { Login } from "../components/Login";
import { ROLES } from "./roles";

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>

          <Route element={<ProtectedRoute role={ROLES.CUSTOMER} />}>
            <Route path="/" element={< AppLayout />} />
            <Route path="/browse" element={< AppLayout/>} />
            <Route path="/checkout" element={< AppLayout/>} />
          </Route>

          {/* <Route element={<ProtectedRoute role={ROLES.RESTAURANT} />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/menu" element={<MenuManagement />} />
            <Route path="/incoming-orders" element={<IncomingOrders />} />
          </Route> */}

        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRouter;