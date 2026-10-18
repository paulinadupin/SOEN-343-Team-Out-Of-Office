const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`Request failed with status ${response.status}`);
  }

  return response.status === 204 ? null : response.json();
}

function mapAccount(account) {
  return {
    id: account.userId,
    username: account.username,
    role: account.accountType.toLowerCase(),
  };
}

export async function login(username, password) {
  const account = await request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
  return mapAccount(account);
}

export async function getCurrentUser() {
  const account = await request("/api/auth/me");
  return mapAccount(account);
}

export async function logout() {
  await request("/api/auth/logout", { method: "POST" });
}
