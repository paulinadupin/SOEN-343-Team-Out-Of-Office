import { useMemo, useState } from "react";
import { CartContext } from "./CartContextValue";

export function CartProvider({ children }) {
    const [items, setItems] = useState([]);

    function addItem(item) {
        setItems((currentItems) => {
            const existingItem = currentItems.find(
                (currentItem) => currentItem.name === item.name
            );

            if (existingItem) {
                return currentItems.map((currentItem) =>
                    currentItem.name === item.name
                        ? { ...currentItem, quantity: currentItem.quantity + 1 }
                        : currentItem
                );
            }

            return [...currentItems, { ...item, quantity: 1 }];
        });
    }

    function clearCart() {
        setItems([]);
    }

    const itemCount = useMemo(
        () => items.reduce((total, item) => total + item.quantity, 0),
        [items]
    );

    return (
        <CartContext.Provider value={{ items, itemCount, addItem, clearCart }}>
            {children}
        </CartContext.Provider>
    );
}
