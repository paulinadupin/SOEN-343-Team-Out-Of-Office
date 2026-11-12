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
    const body = await response.json().catch(() => null);
    const message = body?.detail || body?.message || body?.error;
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  return response.status === 204 ? null : response.json();
}

function mapAccount(account) {
  return {
    id: account.userId,
    username: account.username,
    accountType: account.accountType,
    deliveryAddress: account.deliveryAddress,
  };
}

export async function login(username, password) {
  const account = await request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
  return mapAccount(account);
}

export async function register(account) {
  const response = await request("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(account),
  });
  return mapAccount(response);
}

export async function getCurrentUser() {
  const account = await request("/api/auth/me");
  return mapAccount(account);
}

export async function getCurrentRestaurant() {
  return request("/api/auth/restaurant/me");
}

export async function getRestaurants() {
  return request("/api/auth/restaurants");
}

export async function getRestaurantMenu(restaurantId) {
  return request(`/api/restaurants/${encodeURIComponent(restaurantId)}/menu`);
}

export async function getRestaurantOrders() {
  return request("/api/orders/restaurant");
}

export async function placeOrder(restaurantId, lines) {
  return request("/api/orders", {
    method: "POST",
    body: JSON.stringify({ restaurantId: Number(restaurantId), lines }),
  });
}

export async function getPreperationQueueSize() {
  return request('/api/orders/restaurant/queue/size');
}

export async function updateOrder(orderId, action, reason) {
  return request(`/api/orders/${encodeURIComponent(orderId)}/${action}`, {
    method: "POST",
    body: JSON.stringify({ reason }),
  });
}

export async function getOnDutyCouriers() {
  return request("/api/on-duty-couriers");
}

export async function logout() {
  await request("/api/auth/logout", { method: "POST" });
}

export async function getCourierState(){
  return request("/api/courier/assignment");
}

export async function acceptOffer(){
  return request("/api/courier/offer/accept",{method:"POST"});
}

export async function declineOffer(){
  return request("/api/courier/offer/decline",{method:"POST"});
}

export async function goOnline(){
  return request("/api/courier/online",{method:"POST"});
}

export async function goOffline(){
  return request("/api/courier/offline",{method:"POST"});
}

export async function confirmPickup(){
  return request("/api/courier/pickup",{method:"POST"});
}

export async function confirmDelivery(){
  return request("/api/courier/delivery",{method:"POST"});
}
