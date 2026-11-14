let entries = [];
const subscribers = new Set();

let isOpen = false;
const visibilitySubscribers = new Set();

export function addEntry(message, type = "info") {
  entries = [...entries, { message, type, time: new Date() }];
  subscribers.forEach((callback) => callback(entries));
}

export function getEntries() {
  return entries;
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
