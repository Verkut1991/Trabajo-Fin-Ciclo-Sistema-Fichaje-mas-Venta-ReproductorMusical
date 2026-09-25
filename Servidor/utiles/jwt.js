const jwt = require('jsonwebtoken');

const ACCESS_SECRET = process.env.JWT_ACCESS_SECRET || 'change-me-access';
const REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'change-me-refresh';
const ACCESS_EXPIRY = process.env.JWT_ACCESS_EXPIRY || '1h';
const REFRESH_EXPIRY = process.env.JWT_REFRESH_EXPIRY || '30d';

// Genera el access token JWT del tenant del kiosko
function signAccessToken(cliente) {
    return jwt.sign(
        { clienteId: cliente.id, email: cliente.email_admin, type: 'tenant_access' },
        ACCESS_SECRET,
        { expiresIn: ACCESS_EXPIRY }
    );
}

// Genera el refresh token JWT del tenant del kiosko
function signRefreshToken(cliente) {
    return jwt.sign(
        { clienteId: cliente.id, type: 'tenant_refresh' },
        REFRESH_SECRET,
        { expiresIn: REFRESH_EXPIRY }
    );
}

// Verifica un access token y devuelve el payload o null
function verifyAccessToken(token) {
    try {
        const payload = jwt.verify(token, ACCESS_SECRET);
        if (payload.type !== 'tenant_access') return null;
        return payload;
    } catch {
        return null;
    }
}

// Verifica un refresh token y devuelve el payload o null
function verifyRefreshToken(token) {
    try {
        const payload = jwt.verify(token, REFRESH_SECRET);
        if (payload.type !== 'tenant_refresh') return null;
        return payload;
    } catch {
        return null;
    }
}

// Devuelve los segundos de validez del access token para el cliente
function getAccessExpiresInSeconds() {
    if (ACCESS_EXPIRY.endsWith('h')) {
        return parseInt(ACCESS_EXPIRY, 10) * 3600;
    }
    if (ACCESS_EXPIRY.endsWith('m')) {
        return parseInt(ACCESS_EXPIRY, 10) * 60;
    }
    if (ACCESS_EXPIRY.endsWith('d')) {
        return parseInt(ACCESS_EXPIRY, 10) * 86400;
    }
    return 3600;
}

module.exports = {
    signAccessToken,
    signRefreshToken,
    verifyAccessToken,
    verifyRefreshToken,
    getAccessExpiresInSeconds
};
