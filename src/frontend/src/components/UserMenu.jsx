import './UserMenu.css'
import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../app/AuthContext'

function UserMenu() {
    const [open, setOpen] = useState(false)
    const { user, logout } = useAuth();
    const menuRef = useRef(null)
    const navigate = useNavigate()

    // close when clicking outside the menu, or pressing Escape
    useEffect(() => {
        function handleClick(e) {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setOpen(false)
            }
        }
        function handleKey(e) {
            if (e.key === 'Escape') setOpen(false)
        }
        document.addEventListener('mousedown', handleClick)
        document.addEventListener('keydown', handleKey)
        return () => {
            document.removeEventListener('mousedown', handleClick)
            document.removeEventListener('keydown', handleKey)
        }
    }, [])

    async function handleLogout() {
        await logout();
        setOpen(false);
        navigate('/login');
    }

    return (
        <div className="user-menu" ref={menuRef}>
            <button
                className="user-menu-button"
                onClick={() => setOpen(!open)}
                aria-haspopup="menu"
                aria-expanded={open}
            >
                <img className="user-icon" src="/user.png" alt="User menu" />
            </button>

            {open && (
                <div className="user-menu-dropdown" role="menu">
                    <p>{user?.username}</p>
                    {/* <Link to="/profile" onClick={() => setOpen(false)}>Profile</Link>
                    <Link to="/settings" onClick={() => setOpen(false)}>Settings</Link>
                    <hr />
                    <Link to="/" onClick={() => setOpen(false)}>Customer View</Link>
                    <Link to="/op-dash" onClick={() => setOpen(false)}>Operator View</Link>
                    <Link to="/courier" onClick={() => setOpen(false)}>Courier View</Link> */}
                    <hr />
                    <button onClick={() => handleLogout()}>Log out</button>
                </div>
            )}
        </div>
    )
}

export default UserMenu