import './Header.css'
import { Link } from 'react-router-dom'
import UserMenu from './UserMenu.jsx'

function CourierHeader() {
    return (
        <header>
            <img className="logo" src="/logo.png" alt="Logo" />
            <h1>Enchantea Courier</h1>
            <UserMenu />
        </header>
    )
}

export default CourierHeader