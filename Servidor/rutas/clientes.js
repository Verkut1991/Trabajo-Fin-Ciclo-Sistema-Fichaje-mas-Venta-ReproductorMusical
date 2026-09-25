const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const { query } = require('../utiles/db');
const {
    signAccessToken,
    signRefreshToken,
    verifyRefreshToken,
    getAccessExpiresInSeconds
} = require('../utiles/jwt');

// REGISTRO DE EMPRESA (CLIENTE)
router.post('/registro', async (req, res) => {
    const { nombre_empresa, email_admin, password } = req.body;
    try {
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        const sql = `INSERT INTO TFG_clientes (nombre_empresa, email_admin, password_hash) VALUES (?, ?, ?)`;
        const result = await query(sql, [nombre_empresa, email_admin, hashedPassword]);

        res.status(201).json({
            success: true,
            message: "Empresa registrada con éxito",
            cliente_id: result.insertId
        });
    } catch (error) {
        console.error('Error en registro cliente:', error);
        res.status(500).json({ error: error.message });
    }
});

// LOGIN DE EMPRESA (CLIENTE) - legacy sin JWT
router.post('/login', async (req, res) => {
    const { email_admin, password } = req.body;
    try {
        const rows = await query('SELECT * FROM TFG_clientes WHERE email_admin = ?', [email_admin]);
        if (rows.length === 0) return res.status(401).json({ error: "Credenciales inválidas" });

        const cliente = rows[0];
        const match = await bcrypt.compare(password, cliente.password_hash);
        if (!match) return res.status(401).json({ error: "Credenciales inválidas" });

        res.json({
            success: true,
            cliente: {
                id: cliente.id,
                nombre_empresa: cliente.nombre_empresa,
                email_admin: cliente.email_admin,
                plan_contratado: cliente.plan_contratado
            }
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// LOGIN JWT PARA KIOSKO (tenant)
router.post('/login-jwt', async (req, res) => {
    const { email_admin, password } = req.body;
    if (!email_admin || !password) {
        return res.status(400).json({ error: 'Email y contraseña requeridos' });
    }
    try {
        const rows = await query('SELECT * FROM TFG_clientes WHERE email_admin = ?', [email_admin]);
        if (rows.length === 0) {
            return res.status(401).json({ error: 'Credenciales inválidas' });
        }
        const cliente = rows[0];
        const match = await bcrypt.compare(password, cliente.password_hash);
        if (!match) {
            return res.status(401).json({ error: 'Credenciales inválidas' });
        }

        const accessToken = signAccessToken(cliente);
        const refreshToken = signRefreshToken(cliente);

        res.json({
            success: true,
            accessToken,
            refreshToken,
            expiresIn: getAccessExpiresInSeconds(),
            cliente: {
                id: cliente.id,
                nombre_empresa: cliente.nombre_empresa,
                email_admin: cliente.email_admin,
                plan_contratado: cliente.plan_contratado
            }
        });
    } catch (error) {
        console.error('Error en login-jwt:', error);
        res.status(500).json({ error: error.message });
    }
});

// RENOVAR ACCESS TOKEN DEL KIOSKO
router.post('/refresh-token', async (req, res) => {
    const { refreshToken } = req.body;
    if (!refreshToken) {
        return res.status(400).json({ error: 'Refresh token requerido' });
    }
    try {
        const payload = verifyRefreshToken(refreshToken);
        if (!payload) {
            return res.status(401).json({ error: 'Refresh token inválido o expirado' });
        }

        const rows = await query('SELECT * FROM TFG_clientes WHERE id = ?', [payload.clienteId]);
        if (rows.length === 0) {
            return res.status(401).json({ error: 'Cliente no encontrado' });
        }

        const cliente = rows[0];
        const accessToken = signAccessToken(cliente);

        res.json({
            success: true,
            accessToken,
            expiresIn: getAccessExpiresInSeconds(),
            cliente: {
                id: cliente.id,
                nombre_empresa: cliente.nombre_empresa,
                email_admin: cliente.email_admin,
                plan_contratado: cliente.plan_contratado
            }
        });
    } catch (error) {
        console.error('Error en refresh-token:', error);
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
