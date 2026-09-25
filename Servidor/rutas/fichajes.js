const express = require('express');
const router = express.Router();
const { generateIntegrityHash, decryptNIF } = require('../utiles/cifrado');
const { query } = require('../utiles/db');
const { Audit } = require('../utiles/mongo_esquema');

// Normaliza el tipo de fichaje para evitar variaciones de formato.
function normalizeTipo(tipo) {
    if (typeof tipo !== 'string') return '';
    return tipo.trim().toUpperCase();
}

// Formatea la fecha en hora local compatible con DATETIME de MySQL.
function formatMySqlLocalDateTime(date = new Date()) {
    const pad = (value) => String(value).padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

// Obtiene el ultimo fichaje registrado del empleado para validar secuencia.
async function getLastFichajeType(empleadoId) {
    const sql = `
        SELECT tipo
        FROM TFG_fichajes
        WHERE empleado_id = ?
        ORDER BY timestamp DESC
        LIMIT 1
    `;
    const rows = await query(sql, [empleadoId]);
    if (!rows.length) return null;
    return normalizeTipo(rows[0].tipo);
}

// AUDITORIA PARA DESKTOP (GET) - Filtrada por cliente_id
router.get('/auditoria/:cliente_id', async (req, res) => {
    try {
        const sql = `
            SELECT f.*, e.nombre as nombre_empleado, e.nif, e.cliente_id
            FROM TFG_fichajes f
            JOIN TFG_empleados e ON f.empleado_id = e.id
            WHERE e.cliente_id = ?
            ORDER BY f.timestamp DESC
        `;
        const rows = await query(sql, [req.params.cliente_id]);
        const records = rows.map(row => ({
            ...row,
            dni: decryptNIF(row.nif)
        }));
        res.json(records);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// HISTORIAL INDIVIDUAL PARA MÓVIL (GET)
router.get('/historial/:empleado_id', async (req, res) => {
    try {
        const sql = `
            SELECT tipo, timestamp, ubicacion 
            FROM TFG_fichajes 
            WHERE empleado_id = ? 
            ORDER BY timestamp DESC 
            LIMIT 10
        `;
        const rows = await query(sql, [req.params.empleado_id]);
        res.json(rows);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// TIEMPO TRABAJADO HOY (GET)
router.get('/tiempo-hoy/:empleado_id', async (req, res) => {
    try {
        const sql = `
            SELECT tipo, timestamp 
            FROM TFG_fichajes 
            WHERE empleado_id = ? AND DATE(timestamp) = CURDATE() 
            ORDER BY timestamp ASC
        `;
        const rows = await query(sql, [req.params.empleado_id]);

        let totalMs = 0;
        let lastIn = null;

        // Recorre la jornada en orden para sumar cada tramo valido de entrada a salida.
        for (const row of rows) {
            const time = new Date(row.timestamp);
            const tipoNormalizado = normalizeTipo(row.tipo);
            if (tipoNormalizado === 'ENTRADA') {
                if (!lastIn) lastIn = time;
            } else if (tipoNormalizado === 'SALIDA' && lastIn) {
                totalMs += (time - lastIn);
                lastIn = null;
            }
        }

        // Si sigue dentro actualmente (fichó entrada pero no salida todavía)
        if (lastIn) {
            totalMs += (new Date() - lastIn);
        }

        const totalMinutes = Math.floor(totalMs / 1000 / 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;

        const lastTipo = await getLastFichajeType(req.params.empleado_id);
        let estado = 'PENDIENTE';
        if (lastTipo === 'ENTRADA') estado = 'TRABAJANDO';
        else if (lastTipo === 'SALIDA') estado = 'DESCANSANDO';

        res.json({
            totalMinutes,
            hours,
            minutes,
            formatted: `${hours}h ${minutes}m`,
            estado
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// FICHAR (POST)
router.post('/fichar', async (req, res) => {
    const { empleado_id, cliente_id, tipo, ubicacion } = req.body;
    if (!empleado_id || !cliente_id || !tipo || !ubicacion) {
        return res.status(400).json({ error: "Faltan campos obligatorios para registrar el fichaje" });
    }

    const tipoNormalizado = normalizeTipo(tipo);
    if (!['ENTRADA', 'SALIDA'].includes(tipoNormalizado)) {
        return res.status(400).json({ error: "El tipo debe ser ENTRADA o SALIDA" });
    }

    const timestamp = formatMySqlLocalDateTime();

    try {
        // Verifica que el empleado pertenece al cliente para evitar fichajes cruzados.
        const empleadoRows = await query(
            'SELECT id FROM TFG_empleados WHERE id = ? AND cliente_id = ? LIMIT 1',
            [empleado_id, cliente_id]
        );
        if (!empleadoRows.length) {
            return res.status(403).json({ error: "Empleado no valido para el cliente indicado" });
        }

        // Fuerza la alternancia ENTRADA SALIDA para mantener coherencia de jornada.
        const lastTipo = await getLastFichajeType(empleado_id);
        if (lastTipo === tipoNormalizado) {
            return res.status(409).json({
                error: `No se puede registrar ${tipoNormalizado} dos veces seguidas`
            });
        }
        if (!lastTipo && tipoNormalizado === 'SALIDA') {
            return res.status(409).json({
                error: "No se puede registrar SALIDA sin una ENTRADA previa"
            });
        }

        // Generar Hash con cliente_id incluido
        const hash = generateIntegrityHash({ cliente_id, empleado_id, timestamp, tipo: tipoNormalizado });

        await query(
            'INSERT INTO TFG_fichajes (empleado_id, tipo, timestamp, ubicacion, hash_integridad) VALUES (?, ?, ?, ?, ?)',
            [empleado_id, tipoNormalizado, timestamp, ubicacion, hash]
        );

        res.status(201).json({
            status: 'ok',
            success: true,
            message: `${tipoNormalizado} registrada correctamente`,
            hash,
            timestamp
        });

        // Log en MongoDB con particionado
        Audit.create(cliente_id, { empleado_id, accion: "FICHAJE_" + tipoNormalizado, ip: req.ip });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;