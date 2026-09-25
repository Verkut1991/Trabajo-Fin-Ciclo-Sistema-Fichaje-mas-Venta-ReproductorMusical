import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Hero from './components/Hero';
import AboutUs from './components/AboutUs';
import Pricing from './components/Pricing';
import Login from './components/Login';
import { AuthProvider } from './context/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app">
          <Navbar />
          <Routes>
            <Route path="/" element={
              <main>
                <Hero />
                <AboutUs />
                <Pricing />
              </main>
            } />
            <Route path="/login" element={<Login />} />
          </Routes>
          <footer className="container" style={{ textAlign: 'center', padding: '3rem 0', color: '#94a3b8', fontSize: '0.9rem', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
            &copy; {new Date().getFullYear()} FichaMaster. Todos los derechos reservados.
          </footer>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
