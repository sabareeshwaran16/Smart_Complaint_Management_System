import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LogOut, Bell } from 'lucide-react'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="bg-indigo-700 text-white px-6 py-3 flex items-center justify-between shadow-md">
      <Link to="/" className="text-xl font-bold tracking-wide flex items-center gap-2">
        <Bell size={22} />
        HostelCare
      </Link>
      {user && (
        <div className="flex items-center gap-4">
          <span className="text-sm opacity-80">
            {user.name} &middot; <span className="font-semibold">{user.role}</span>
          </span>
          <button
            onClick={handleLogout}
            className="flex items-center gap-1 bg-indigo-900 hover:bg-indigo-800 px-3 py-1.5 rounded text-sm"
          >
            <LogOut size={15} /> Logout
          </button>
        </div>
      )}
    </nav>
  )
}
