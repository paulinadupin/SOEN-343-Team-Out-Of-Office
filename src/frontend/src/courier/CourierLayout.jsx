import { Outlet } from 'react-router-dom'
import CourierSidebar from './CourierSidebar.jsx'
import CourierHeader from './CourierHeader.jsx'

function CourierLayout() {
    return (
        <div className="app">
            <CourierHeader />
            <div className="page-layout">
                <CourierSidebar />
                <main className="page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}

export default CourierLayout
