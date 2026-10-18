import { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../app/AuthContext";
import { HOME_BY_ROLE } from "../app/roles";

export function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    try {
      const user = await login(email, password);
      // Send them back to the page they tried to visit, else their role's home
      const from = location.state?.from?.pathname;
      navigate(from || HOME_BY_ROLE[user.role], { replace: true });
    } catch (err) {
      setError("Invalid email or password");
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
      <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
      {error && <p>{error}</p>}
      <button type="submit">Log in</button>
    </form>
  );
}