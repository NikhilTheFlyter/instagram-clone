import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Home, Search, Compass, PlusSquare, LogOut, User } from 'lucide-react';
import useAuthStore from '../../store/useAuthStore';

export default function Navbar() {
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 h-[54px] bg-white border-b border-gray-300 flex items-center justify-between px-4">
      <div className="flex items-center justify-between w-full max-w-[935px] mx-auto">
        {/* Left: Logo */}
        <Link to="/" data-cy="navbar-logo" className="text-xl font-semibold italic">
          Instagram
        </Link>

        {/* Center: Search */}
        <form onSubmit={handleSearch} className="hidden sm:block">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              data-cy="navbar-search-input"
              type="text"
              placeholder="Search"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-gray-100 rounded-lg pl-10 pr-4 py-1.5 text-sm w-[268px] border border-transparent focus:border-gray-300 focus:outline-none"
            />
          </div>
        </form>

        {/* Right: Icons */}
        <div className="flex items-center gap-4">
          <Link to="/" data-cy="navbar-home-btn" className="text-gray-800 hover:text-gray-600">
            <Home className="w-6 h-6" />
          </Link>
          <Link to="/explore" data-cy="navbar-explore-btn" className="text-gray-800 hover:text-gray-600">
            <Compass className="w-6 h-6" />
          </Link>
          <Link to="/post/create" data-cy="navbar-create-btn" className="text-gray-800 hover:text-gray-600">
            <PlusSquare className="w-6 h-6" />
          </Link>
          <Link
            to={`/profile/${user?.id || ''}`}
            data-cy="navbar-profile-btn"
            className="flex items-center"
          >
            {user?.profilePicture ? (
              <img
                src={user.profilePicture}
                alt={user.username}
                className="w-6 h-6 rounded-full object-cover border border-gray-300"
              />
            ) : (
              <User className="w-6 h-6 text-gray-800" />
            )}
          </Link>
          <button
            data-cy="navbar-logout-btn"
            onClick={handleLogout}
            className="text-gray-800 hover:text-gray-600"
          >
            <LogOut className="w-6 h-6" />
          </button>
        </div>
      </div>
    </nav>
  );
}
