import Header from "./Header";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

function AppLayout() {
    return (
        <div className="app">
            <Header />
            <div className="page-layout">
                <Sidebar />
                <main className="page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default AppLayout;