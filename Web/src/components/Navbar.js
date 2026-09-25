import React from 'react';
import { Link } from 'react-router-dom';
import { Clock, LogOut, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { cliente, logout, isAuthenticated } = useAuth();

    return (
        <nav className="glass">
            <div className="container nav-container">
                <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 700, fontSize: '1.25rem', textDecoration: 'none', color: 'inherit' }}>
                    <Clock className="text-primary" size={24} style={{ color: '#6366f1' }} />
                    <span>Kompot Player Fichaje</span>
                </Link>
                <ul className="nav-links">
                    <li><a href="#about">Nosotros</a></li>
                    <li><a href="#products">Productos</a></li>
                    {isAuthenticated ? (
                        <li style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#6366f1', fontSize: '0.9rem', fontWeight: 600 }}>
                                <User size={16} />
                                {cliente.nombre_empresa}
                            </div>
                            <button
                                onClick={logout}
                                style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.25rem', fontSize: '0.85rem' }}
                            >
                                <LogOut size={14} /> Salir
                            </button>
                        </li>
                    ) : (
                        <li><Link to="/login">Login</Link></li>
                    )}
                </ul>
                {!isAuthenticated && (
                    <a href="#products" className="btn-primary" style={{ textDecoration: 'none' }}>Empezar</a>
                )}
            </div>
        </nav>
    );
};

export default Navbar;
