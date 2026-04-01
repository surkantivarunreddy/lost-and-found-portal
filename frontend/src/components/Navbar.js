import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

/* ── Brand logo SVG ────────────────────────────────────── */
const LostFoundLogo = () => (
  <svg width="34" height="34" viewBox="0 0 34 34" fill="none"
    xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
    <circle cx="17" cy="17" r="17" fill="url(#logoGrad)" />
    <circle cx="15" cy="15" r="6" stroke="white" strokeWidth="2.2" fill="none" />
    <line x1="19.8" y1="19.8" x2="24.5" y2="24.5"
      stroke="white" strokeWidth="2.4" strokeLinecap="round" />
    {/* tiny heart inside lens — reunited symbol */}
    <path
      d="M13 14.5 C13 13.5 14.5 13 15 14 C15.5 13 17 13.5 17 14.5 C17 15.5 15 17 15 17 C15 17 13 15.5 13 14.5Z"
      fill="white" opacity="0.9" />
    <defs>
      <linearGradient id="logoGrad" x1="0" y1="0" x2="34" y2="34" gradientUnits="userSpaceOnUse">
        <stop stopColor="#1d4ed8" />
        <stop offset="1" stopColor="#7c3aed" />
      </linearGradient>
    </defs>
  </svg>
);

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setMenuOpen(false);
  };

  const close = () => setMenuOpen(false);

  return (
    <nav className="navbar">
      <div className="nav-container">

        {/* Brand */}
        <Link to="/" className="nav-brand" onClick={close}>
          <LostFoundLogo />
          <span className="brand-text">
            <span className="brand-lost">Lost</span>
            <span className="brand-amp"> &amp; </span>
            <span className="brand-found">Found</span>
          </span>
        </Link>

        {/* Hamburger */}
        <button
          className={`nav-hamburger${menuOpen ? ' open' : ''}`}
          onClick={() => setMenuOpen((o) => !o)}
          aria-label="Toggle menu"
        >
          <span /><span /><span />
        </button>

        {/* Links */}
        <div className={`nav-links${menuOpen ? ' nav-links-open' : ''}`}>
          <Link to="/search" onClick={close}>Search Items</Link>

          {user ? (
            <>
              <Link to="/report-lost" className="btn btn-outline"
                style={{ padding: '0.4rem 1rem' }} onClick={close}>
                Report Lost
              </Link>
              <Link to="/report-found" className="btn btn-primary"
                style={{ padding: '0.4rem 1rem' }} onClick={close}>
                Report Found
              </Link>
              <Link to="/dashboard" onClick={close}>Dashboard</Link>
              {user.role === 'ADMIN' && (
                <Link to="/admin" className="nav-admin-link" onClick={close}>🛡️ Admin</Link>
              )}
              <button className="nav-logout" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login" onClick={close}>Login</Link>
              <Link to="/register" className="btn btn-primary"
                style={{ padding: '0.4rem 1rem' }} onClick={close}>
                Sign Up
              </Link>
            </>
          )}
        </div>

      </div>
    </nav>
  );
};

export default Navbar;
