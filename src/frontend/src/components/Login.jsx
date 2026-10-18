import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../app/AuthContext";
import { HOME_BY_ROLE } from "../app/roles";

export function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const user = await login(username, password);
      // Send them back to the page they tried to visit, else their role's home
      const from = location.state?.from?.pathname;
      navigate(from || HOME_BY_ROLE[user.role], { replace: true });
    } catch {
      setError("Invalid username or password");
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
        required
      />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      {error && <p>{error}</p>}
      <button type="submit">Log in</button>
    </form>
  );
}