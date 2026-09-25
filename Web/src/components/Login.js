import React, { useState } from 'react';
import { LogIn } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { ENDPOINTS } from '../config/api';
import { apiPost } from '../services/apiClient';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const { response, data } = await apiPost(ENDPOINTS.clientes.login, {
                email_admin: email,
                password,
            });

            if (response.ok && data.success) {
                login(data.cliente);
                navigate('/');
            } else {
                setError(data.error || 'Credenciales incorrectas');
            }
        } catch (err) {
            console.error('Error en login:', err);
            setError(err.message || 'Error de conexión con el servidor');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
            <form className="login-form glass" onSubmit={handleSubmit}>
                <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
                    <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '1rem', borderRadius: '1rem', width: 'fit-content', margin: '0 auto 1rem', color: '#6366f1' }}>
                        <LogIn size={32} />
                    </div>
                    <h2 style={{ fontSize: '2rem' }}>Panel de Control</h2>
                    <p style={{ color: '#94a3b8' }}>Accede a la gestión de tu empresa</p>
                </div>

                {error && (
                    <div style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444', padding: '0.75rem', borderRadius: '0.5rem', marginBottom: '1.5rem', fontSize: '0.9rem', textAlign: 'center', border: '1px solid rgba(239, 68, 68, 0.2)' }}>
                        {error}
                    </div>
                )}

                <div className="input-group">
                    <label>Email Administrador</label>
                    <input
                        type="email"
                        placeholder="admin@empresa.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        disabled={loading}
                    />
                </div>

                <div className="input-group">
                    <label>Contraseña</label>
                    <input
                        type="password"
                        placeholder="••••••••"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        disabled={loading}
                    />
                </div>

                <button
                    type="submit"
                    className="btn-primary"
                    style={{ width: '100%', marginTop: '1rem', opacity: loading ? 0.7 : 1 }}
                    disabled={loading}
                >
                    {loading ? 'Conectando...' : 'Iniciar Sesión'}
                </button>

                <p style={{ color: '#94a3b8', fontSize: '0.85rem', textAlign: 'center', marginTop: '1.5rem' }}>
                    ¿Tu empresa no tiene cuenta? <a href="#products" style={{ color: '#6366f1', textDecoration: 'none', fontWeight: 600 }}>Crea una hoy</a>
                </p>
            </form>
        </div>
    );
};

export default Login;
