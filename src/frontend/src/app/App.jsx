import './App.css'
import { AuthProvider } from "./AuthContext";
import { CartProvider } from "./CartContext";
import AppRouter from "./Router";

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <AppRouter />
      </CartProvider>
    </AuthProvider>
  )
}

export default App
