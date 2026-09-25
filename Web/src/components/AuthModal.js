import React, { useState } from 'react';
import { X, LogIn, UserPlus } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { ENDPOINTS } from '../config/api';
import { apiPost } from '../services/apiClient';

const AuthModal = ({ isOpen, onClose }) => {
    const [isLogin, setIsLogin] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [nombreEmpresa, setNombreEmpresa] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();

    if (!isOpen) return null;

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        const endpoint = isLogin ? ENDPOINTS.clientes.login : ENDPOINTS.clientes.registro;
        const body = isLogin
            ? { email_admin: email, password }
            : { nombre_empresa: nombreEmpresa, email_admin: email, password };

        try {
            const { response, data } = await apiPost(endpoint, body);

            if (response.ok && data.success) {
                if (isLogin) {
                    login(data.cliente);
                } else {
                    const { data: loginData } = await apiPost(ENDPOINTS.clientes.login, {
                        email_admin: email,
                        password,
                    });
                    login(loginData.cliente);
                }
                onClose();
            } else {
                setError(data.error || 'Error al procesar la solicitud');
            }
        } catch (err) {
            setError(err.message || 'Error de conexión con el servidor');
        }
    };

    return (
        <div className="modal-overlay">
            <div className="auth-modal glass">
                <button className="close-btn" onClick={onClose}><X size={20} /></button>

                <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
                    <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '0.75rem', borderRadius: '0.75rem', width: 'fit-content', margin: '0 auto 1rem', color: '#6366f1' }}>
                        {isLogin ? <LogIn size={28} /> : <UserPlus size={28} />}
                    </div>
                    <h2>{isLogin ? 'Acceso Empresa' : 'Registro Empresa'}</h2>
                    <p style={{ color: '#94a3b8', fontSize: '0.9rem' }}>
                        {isLogin ? 'Gestiona tu suscripción y personalización' : 'Crea una cuenta para tu negocio'}
                    </p>
                </div>

                <form onSubmit={handleSubmit}>
                    {!isLogin && (
                        <div className="input-group">
                            <label>Nombre de la Empresa</label>
                            <input
                                type="text"
                                value={nombreEmpresa}
                                onChange={(e) => setNombreEmpresa(e.target.value)}
                                placeholder="e.g. Acme Demo S.L."
                                required
                            />
                        </div>
                    )}
                    <div className="input-group">
                        <label>Email Administrador</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="email@empresa.com"
                            required
                        />
                    </div>
                    <div className="input-group">
                        <label>Contraseña</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="••••••••"
                            required
                        />
                    </div>

                    {error && <p style={{ color: '#ef4444', fontSize: '0.85rem', marginBottom: '1rem', textAlign: 'center' }}>{error}</p>}

                    <button type="submit" className="btn-primary" style={{ width: '100%', padding: '0.875rem' }}>
                        {isLogin ? 'Iniciar Sesión' : 'Crear Cuenta'}
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.85rem', color: '#94a3b8' }}>
                    {isLogin ? '¿Tu empresa no tiene cuenta?' : '¿Ya gestionas una empresa?'}
                    <button
                        onClick={() => setIsLogin(!isLogin)}
                        style={{ background: 'none', border: 'none', color: '#6366f1', cursor: 'pointer', paddingLeft: '5px', fontWeight: 600 }}
                    >
                        {isLogin ? 'Regístrate aquí' : 'Inicia sesión'}
                    </button>
                </p>
            </div>
        </div>
    );
};

export default AuthModal;
