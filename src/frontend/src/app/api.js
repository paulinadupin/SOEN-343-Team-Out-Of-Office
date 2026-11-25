import { addEntry, getConsoleUserId, setConsoleUser } from "./consoleLog";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";
const reverseGeocodeCache = new Map();

async function request(path, options = {}, shouldLog = true) {
  const method = options.method || "GET";
  const requestUserId = getConsoleUserId();
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
    const errorMessage = message || `Request failed with status ${response.status}`;
    if (shouldLog) addEntry(`${method} ${path} - ${errorMessage}`, "error", requestUserId);
    throw new Error(errorMessage);
  }

  if (shouldLog) addEntry(`${method} ${path}`, "success", requestUserId);
  return response.status === 204 ? null : response.json();
}

function mapAccount(account) {
  return {
    id: account.userId,
    username: account.username,
    accountType: account.accountType,
    location: account.location,
  };
}

export async function login(username, password) {
  const account = await request("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  }, false);
  const loggedInUser = mapAccount(account);
  setConsoleUser(loggedInUser.id);
  addEntry("POST /api/auth/login", "success", loggedInUser.id);
  return loggedInUser;
}

export async function register(account) {
  const response = await request("/api/auth/register", {
    method: "POST",
    body: JSON.stringify(account),
  });
  return mapAccount(response);
}

export async function getCurrentUser() {
  try {
    const account = await request("/api/auth/me", {}, false);
    const currentUser = mapAccount(account);
    setConsoleUser(currentUser.id);
    addEntry("GET /api/auth/me", "success", currentUser.id);
    return currentUser;
  } catch (error) {
    setConsoleUser(null);
    throw error;
  }
}

export async function getCurrentRestaurant() {
  return request("/api/auth/restaurant/me");
}

export async function getRestaurants() {
  return request("/api/auth/restaurants");
}

export async function updateRestaurantStatus(restaurantId, status) {
  return request(`/api/auth/restaurants/${encodeURIComponent(restaurantId)}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });
}

export async function getRestaurantMenu(restaurantId) {
  return request(`/api/restaurants/${encodeURIComponent(restaurantId)}/menu`);
}

export async function getRestaurantOrders() {
  return request("/api/orders/restaurant");
}

export async function getRestaurantMap() {
  return request("/api/map/restaurant");
}

export async function getCustomerMap() {
  return request("/api/map/customer");
}

export async function getCourierMap() {
  return request("/api/map/courier");
}

export async function getMapData() {
  return request("/api/auth/map");
}

export async function getActiveOrders() {
  return request("/api/orders/active");
}

export async function getRejectedOrders() {
  return request("/api/orders/rejected");
}

export async function getCancelledOrders() {
  return request("/api/orders/cancelled");
}

export async function getDeliveredOrders() {
  return request("/api/orders/delivered");
}

export async function placeOrder(restaurantId, lines, promoCode=null) {
  return request("/api/orders", {
    method: "POST",
    body: JSON.stringify({ restaurantId: Number(restaurantId), lines, promoCode }),
  });
}

export async function getOrderQuote(restaurantId, lines) {
  return request("/api/orders/quote", {
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
  return request("/api/auth/on-duty-couriers");
}

export async function resetSystem() {
  return request("/api/dash/reset", { method: "POST" });
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

export async function getStalledOrders() {
  return request("/api/courier/stalled");
}

export async function getIdleCouriers() {
  return request("/api/courier/idle");
}

export async function reassignOrder(orderId, courierId) {
  return request(`/api/courier/stalled/${encodeURIComponent(orderId)}/reassign`, {
    method: "POST",
    body: JSON.stringify({ courierId: Number(courierId) }),
  });
}

export async function reverseGeocode(lat, lng) {
  const requestUserId = getConsoleUserId();
  const cacheKey = `${Number(lat)},${Number(lng)}`;
  const cachedRequest = reverseGeocodeCache.get(cacheKey);
  if (cachedRequest) {
    addEntry("GET Nominatim /reverse (cached)", "info", requestUserId);
    return cachedRequest;
  }

  const query = new URLSearchParams({
    format: "jsonv2",
    lat: String(lat),
    lon: String(lng),
  });
  const lookup = (async () => {
    const response = await fetch(
      `https://nominatim.openstreetmap.org/reverse?${query.toString()}`
    );

    if (!response.ok) {
      throw new Error("Could not look up address.");
    }

    const data = await response.json();
    const { house_number, road } = data.address ?? {};

    if (!road) {
      throw new Error("No street found.");
    }

    return house_number ? `${house_number} ${road}` : road;
  })();
  reverseGeocodeCache.set(cacheKey, lookup);

  try {
    const address = await lookup;
    addEntry("GET Nominatim /reverse", "success", requestUserId);
    return address;
  } catch (error) {
    if (reverseGeocodeCache.get(cacheKey) === lookup) {
      reverseGeocodeCache.delete(cacheKey);
    }
    addEntry(`GET Nominatim /reverse - ${error.message}`, "error", requestUserId);
    throw error;
  }
}
export async function applyPromo(code, items) {
    return request("/api/orders/promo", {
        method: "POST",
        body: JSON.stringify({ code, items }),
    });
}
