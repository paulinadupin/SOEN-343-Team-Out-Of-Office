let entries = [];
const subscribers = new Set();

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
