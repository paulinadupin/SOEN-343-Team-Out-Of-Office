import { Outlet } from 'react-router-dom'
import OperatorSidebar from './OperatorSidebar.jsx'

function OperatorLayout() {
    return (
        <div className="page-layout">
            <OperatorSidebar />
            <main className="page-content">
                <Outlet />
            </main>
        </div>
    )
}

export default OperatorLayout