import React, { useState } from 'react';
import { Check, Shield, Zap, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import AuthModal from './AuthModal';
import { ENDPOINTS } from '../config/api';
import { apiPost } from '../services/apiClient';

const Pricing = () => {
    const { cliente, isAuthenticated } = useAuth();
    const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);

    const plans = [
        {
            id: 'Basic',
            name: 'Básico',
            price: '1',
            description: 'Perfecto para comenzar.',
            features: ['Fichaje digital básico', 'Reportes estándar', 'Soporte email'],
            icon: <Zap size={24} className="text-primary" />
        },
        {
            id: 'Pro',
            name: 'Profesional',
            price: '3',
            description: 'Gestión avanzada para equipos.',
            features: ['Controles de RRHH', 'Gestión de vacaciones', 'App móvil completa'],
            icon: <Shield size={24} className="text-primary" />
        },
        {
            id: 'Enterprise',
            name: 'Enterprise',
            price: '10',
            description: 'Personalización total y marca blanca.',
            features: ['Personalización Java Desktop', 'Integridad SHA-256 Multi-empresa', 'Soporte 24/7', 'Branding ilimitado'],
            icon: <Sparkles size={24} className="text-primary" />
        }
    ];

    const handlePurchase = async (plan) => {
        if (!isAuthenticated) {
            setIsAuthModalOpen(true);
            return;
        }

        try {
            const { data } = await apiPost(ENDPOINTS.ventas.comprar, {
                cliente_id: cliente.id,
                planId: plan.id,
            });
            if (data.success) {
                alert(`¡Gracias por tu compra! Has adquirido el plan ${plan.name}.`);
                // En un caso real, aquí recargaríamos el plan del contexto
                window.location.reload();
            }
        } catch (error) {
            console.error('Error en la compra:', error);
            alert('Hubo un error al procesar la compra.');
        }
    };

    return (
        <>
            <section id="products" className="container">
                <div style={{ textAlign: 'center', marginBottom: '4rem' }}>
                    <h2 style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>Suscripciones</h2>
                    <p style={{ color: '#94a3b8' }}>Mejora tu experiencia con nuestros planes</p>
                </div>
                <div className="pricing-grid">
                    {plans.map((plan) => (
                        <div key={plan.id} className="pricing-card glass">
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
                                <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '0.5rem', borderRadius: '0.5rem', color: '#6366f1' }}>
                                    {plan.icon}
                                </div>
                                <h3 style={{ fontSize: '1.5rem' }}>{plan.name}</h3>
                            </div>
                            <p style={{ color: '#94a3b8', fontSize: '0.9rem' }}>{plan.description}</p>
                            <div className="price">
                                {plan.price}€ <span>/ mes por trabajador</span>
                            </div>
                            <ul className="features-list">
                                {plan.features.map((feature, idx) => (
                                    <li key={idx}>
                                        <Check size={16} style={{ color: '#10b981' }} />
                                        <span>{feature}</span>
                                    </li>
                                ))}
                            </ul>
                            <button className="btn-primary" onClick={() => handlePurchase(plan)}>
                                {isAuthenticated ? 'Actualizar Plan' : 'Suscribirse'}
                            </button>
                        </div>
                    ))}
                </div>
            </section>

            <AuthModal
                isOpen={isAuthModalOpen}
                onClose={() => setIsAuthModalOpen(false)}
            />
        </>
    );
};

export default Pricing;
