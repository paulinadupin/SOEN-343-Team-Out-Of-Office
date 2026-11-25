let entries = [];
const subscribers = new Set();
let activeUserId = null;

let isOpen = false;
const visibilitySubscribers = new Set();

function notifySubscribers() {
  const userEntries = activeUserId == null
    ? []
    : entries.filter((entry) => entry.userId === activeUserId);
  subscribers.forEach((callback) => callback(userEntries));
}

export function addEntry(message, type = "info", userId = activeUserId) {
  entries = [...entries, { message, type, time: new Date(), userId }];
  notifySubscribers();
}

export function getConsoleUserId() {
  return activeUserId;
}

export function getEntries() {
  if (activeUserId == null) return [];
  return entries.filter((entry) => entry.userId === activeUserId);
}

export function setConsoleUser(userId) {
  activeUserId = userId;
  notifySubscribers();
}

export function subscribe(callback) {
  subscribers.add(callback);
  return () => subscribers.delete(callback);
}

export function isConsoleOpen() {
  return isOpen;
}

export function toggleConsole() {
  isOpen = !isOpen;
  visibilitySubscribers.forEach((callback) => callback(isOpen));
}

export function subscribeVisibility(callback) {
  visibilitySubscribers.add(callback);
  return () => visibilitySubscribers.delete(callback);
}
