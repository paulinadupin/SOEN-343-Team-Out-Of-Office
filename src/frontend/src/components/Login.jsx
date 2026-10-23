import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../app/AuthContext";
import { HOME_BY_ACCOUNT_TYPE } from "../app/roles";
import "./Login.css";

export function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setIsSubmitting(true);
    try {
      const user = await login(username, password);
      // Send them back to the page they tried to visit, else their role's home
      const from = location.state?.from?.pathname;
      navigate(from || HOME_BY_ACCOUNT_TYPE[user.accountType], { replace: true });
    } catch {
      setError("Invalid username or password");
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="login">
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <label>Username</label>
      <input
        type="text"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        placeholder="Username"
        required
      />
      <label>Password</label>
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Password"
        required
      />
      {error && <p role="alert">{error}</p>}
      <button className="login button" type="submit" disabled={isSubmitting}>
        {isSubmitting ? "Logging in..." : "Log in"}
      </button>
    </form>
    <a href="/register">Don't have an account? Register</a>
    </div>
  );
}