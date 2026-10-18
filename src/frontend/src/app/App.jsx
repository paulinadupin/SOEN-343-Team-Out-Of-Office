import './App.css'
import { BrowserRouter } from "react-router-dom";
import { AuthProvider } from "./AuthContext";
import AppRouter from "./Router";

function App() {
  return (
     <BrowserRouter>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </BrowserRouter>
  )
}

export default App
