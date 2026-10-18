//Test for now
const USERS_KEY = "mock_users";
const SESSION_KEY = "mock_session";

// Fake "database" to test for now
function loadUsers() {
  const stored = localStorage.getItem(USERS_KEY);
  if (stored) return JSON.parse(stored);

  const seed = [
    { id: 1, email: "customer@test.com", password: "pass", role: "customer", name: "Sam" },
    { id: 2, email: "pizza@test.com", password: "pass", role: "restaurant", name: "Pizza Place" },
  ];
  localStorage.setItem(USERS_KEY, JSON.stringify(seed));
  return seed;
}

const delay = (ms = 300) => new Promise((r) => setTimeout(r, ms));
const stripPassword = ({ password, ...user }) => user;

export async function login(email, password) {
  await delay();
  const user = loadUsers().find((u) => u.email === email && u.password === password);
  if (!user) throw new Error("Invalid credentials");

  const safeUser = stripPassword(user);
  localStorage.setItem(SESSION_KEY, JSON.stringify(safeUser));
  return safeUser;
}

export async function signup({ email, password, role, name }) {
  await delay();
  const users = loadUsers();
  if (users.some((u) => u.email === email)) throw new Error("Email already in use");

  const newUser = { id: Date.now(), email, password, role, name };
  localStorage.setItem(USERS_KEY, JSON.stringify([...users, newUser]));

  const safeUser = stripPassword(newUser);
  localStorage.setItem(SESSION_KEY, JSON.stringify(safeUser));
  return safeUser;
}

export async function getCurrentUser() {
  await delay(100);
  const stored = localStorage.getItem(SESSION_KEY);
  if (!stored) throw new Error("Not logged in");
  return JSON.parse(stored);
}

export async function logout() {
  await delay(100);
  localStorage.removeItem(SESSION_KEY);
}