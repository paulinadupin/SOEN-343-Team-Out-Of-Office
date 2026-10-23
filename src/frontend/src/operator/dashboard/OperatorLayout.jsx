import { Outlet } from 'react-router-dom'
import OperatorSidebar from './OperatorSidebar.jsx'
import OperatorHeader from './OperatorHeader.jsx'

function OperatorLayout() {
    return (
        <div className="app">
            <OperatorHeader />
            <div className="page-layout">
                <OperatorSidebar />
                <main className="page-content">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}

export default OperatorLayout