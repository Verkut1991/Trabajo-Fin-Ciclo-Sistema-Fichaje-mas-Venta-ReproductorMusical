const rateLimit = require('express-rate-limit');

// Limita intentos de login por PIN para reducir fuerza bruta
const pinLoginLimiter = rateLimit({
    windowMs: 15 * 60 * 1000,
    max: 30,
    standardHeaders: true,
    legacyHeaders: false,
    message: { error: 'Demasiados intentos. Espera unos minutos.' }
});

module.exports = pinLoginLimiter;
