import { createContext, useContext, useEffect, useState } from "react";
import * as authApi from "./api";
import { setConsoleUser } from "./consoleLog";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true); // true while checking existing session

  useEffect(() => {
    authApi.getCurrentUser()
      .then((currentUser) => {
        setUser(currentUser);
        setConsoleUser(currentUser.id);
      })
      .catch(() => {
        setUser(null);
        setConsoleUser(null);
      })
      .finally(() => setLoading(false));
  }, []);

  async function login(username, password) {
    const loggedInUser = await authApi.login(username, password);
    setUser(loggedInUser);
    setConsoleUser(loggedInUser.id);
    return loggedInUser;
  }

  async function logout() {
    await authApi.logout();
    setConsoleUser(null);
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);