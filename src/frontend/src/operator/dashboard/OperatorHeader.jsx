import './Header.css'
import { Link } from 'react-router-dom'
import UserMenu from '../../components/UserMenu.jsx'

function OperatorHeader() {
    return (
        <header className="operator-header">
            <img className="logo" src="/logo.png" alt="Logo" />
            <h1>Enchantea Operator</h1>
            <nav>
                <Link to="/">Customer View</Link>
                <Link to="/courier">Courier View</Link>
            </nav>
            <UserMenu />
        </header>
    )
}

export default OperatorHeader