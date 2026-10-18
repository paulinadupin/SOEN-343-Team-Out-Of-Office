import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "./AuthContext";
import { HOME_BY_ROLE } from "./roles";

export function ProtectedRoute({ role }) {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <p>Loading...</p>;

  // Not logged in -> login page, remembering where they were going
  if (!user) return <Navigate to="/login" state={{ from: location }} replace />;

  // Logged in but wrong role -> bounce to their own home
  if (role && user.role !== role) {
    return <Navigate to={HOME_BY_ROLE[user.role]} replace />;
  }

  return <Outlet />;
}