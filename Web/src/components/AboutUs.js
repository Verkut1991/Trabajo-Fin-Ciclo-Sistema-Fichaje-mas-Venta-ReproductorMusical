import React from 'react';

const AboutUs = () => {
    return (
        <section id="about" className="container">
            <div className="glass" style={{ padding: '4rem', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '3rem', alignItems: 'center' }}>
                <div>
                    <h2 style={{ fontSize: '2.5rem', marginBottom: '1.5rem' }}>Sobre Nosotros</h2>
                    <p style={{ color: '#94a3b8', marginBottom: '1.5rem' }}>
                        Nacimos con una misión clara: simplificar la gestión del tiempo para que las empresas puedan centrarse en lo que realmente importa: su talento.
                    </p>
                    <p style={{ color: '#94a3b8' }}>
                        Nuestro sistema de fichaje no es solo una herramienta de control, es una plataforma diseñada para fomentar la transparencia y la eficiencia en el entorno laboral.
                    </p>
                </div>
                <div style={{ background: 'linear-gradient(45deg, rgba(99, 102, 241, 0.2), rgba(192, 132, 252, 0.2))', height: '300px', borderRadius: '1rem', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <div style={{ textAlign: 'center' }}>
                        <div style={{ fontSize: '3rem', fontWeight: 700, color: '#6366f1' }}>+500</div>
                        <div style={{ color: '#94a3b8' }}>Empresas Confían</div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default AboutUs;
