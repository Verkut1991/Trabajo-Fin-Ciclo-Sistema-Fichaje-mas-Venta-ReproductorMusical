import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [cliente, setCliente] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const storedCliente = localStorage.getItem('fichamaster_cliente');
        if (storedCliente) {
            setCliente(JSON.parse(storedCliente));
        }
        setLoading(false);
    }, []);

    const login = (clienteData) => {
        setCliente(clienteData);
        localStorage.setItem('fichamaster_cliente', JSON.stringify(clienteData));
    };

    const logout = () => {
        setCliente(null);
        localStorage.removeItem('fichamaster_cliente');
        window.location.href = '/';
    };

    return (
        <AuthContext.Provider value={{ cliente, login, logout, isAuthenticated: !!cliente, loading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
