const { verifyAccessToken } = require('../utiles/jwt');

// Exige un access token JWT de tenant valido en la cabecera Authorization
function requireTenantAuth(req, res, next) {
    const header = req.headers.authorization;
    if (!header || !header.startsWith('Bearer ')) {
        return res.status(401).json({ error: 'Token requerido' });
    }
    const token = header.slice(7);
    const payload = verifyAccessToken(token);
    if (!payload) {
        return res.status(401).json({ error: 'Token invalido o expirado' });
    }
    req.tenant = { clienteId: payload.clienteId, email: payload.email };
    next();
}

module.exports = { requireTenantAuth };
