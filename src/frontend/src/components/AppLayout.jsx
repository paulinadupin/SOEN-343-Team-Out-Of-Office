import Header from "./Header";
import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";
import OutputConsole from "./OutputConsole";

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
            <OutputConsole />
        </div>
    );
}

export default AppLayout;