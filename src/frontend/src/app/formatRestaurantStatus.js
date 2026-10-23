const RESTAURANT_STATUS_LABELS = {
    OPEN: "Open",
    CLOSED: "Closed",
    PAUSED: "Paused",
};

export function formatRestaurantStatus(status) {
    const normalizedStatus = typeof status === "string" ? status.toUpperCase() : "";
    return RESTAURANT_STATUS_LABELS[normalizedStatus] || "Unknown";
}
