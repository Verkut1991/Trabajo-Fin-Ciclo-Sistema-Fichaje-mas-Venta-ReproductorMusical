import React from 'react';

const Hero = () => {
    return (
        <section className="hero">
            <div className="container" style={{ maxWidth: '800px' }}>
                <h1 style={{ fontSize: '4rem', marginBottom: '1.5rem' }}>
                    El Sistema de <span className="gradient-text">Fichaje</span> que tu Empresa Merece
                </h1>
                <p style={{ fontSize: '1.25rem', color: '#94a3b8', marginBottom: '2.5rem' }}>
                    Gestiona el tiempo de tu equipo con precisión, elegancia y total cumplimiento legal.
                    La solución definitiva para empresas modernas.
                </p>
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
                    <a href="#products" className="btn-primary" style={{ padding: '1rem 2rem', textDecoration: 'none' }}>Ver Planes</a>
                    <button className="btn-secondary" style={{
                        background: 'transparent',
                        color: 'white',
                        padding: '1rem 2rem',
                        border: '1px solid rgba(255,255,255,0.1)',
                        borderRadius: '0.5rem',
                        cursor: 'pointer'
                    }}>Saber Más</button>
                </div>
            </div>
        </section>
    );
};

export default Hero;
