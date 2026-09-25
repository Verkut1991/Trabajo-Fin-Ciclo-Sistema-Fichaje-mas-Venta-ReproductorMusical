const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const { encryptNIF, decryptNIF } = require('../utiles/cifrado');
const { query } = require('../utiles/db');
const { isValidPin, hashPin, comparePin, generateRandomPin } = require('../utiles/pin');
const { requireTenantAuth } = require('../middleware/auth');
const pinLoginLimiter = require('../middleware/rateLimitPin');

// Comprueba que el PIN no este ya asignado a otro empleado del mismo tenant
async function assertPinUnique(clienteId, pin, excludeEmpleadoId = null) {
    const rows = await query(
        'SELECT id, pin_hash FROM TFG_empleados WHERE cliente_id = ? AND pin_enabled = 1 AND pin_hash IS NOT NULL',
        [clienteId]
    );
    for (const row of rows) {
        if (excludeEmpleadoId && row.id === excludeEmpleadoId) continue;
        const same = await comparePin(pin, row.pin_hash);
        if (same) {
            const err = new Error('PIN ya asignado a otro empleado');
            err.statusCode = 409;
            throw err;
        }
    }
}

// Devuelve datos de empleado sin campos sensibles
function toPublicEmpleado(dbEmpleado) {
    const { hash_password, pin_hash, nif, ...rest } = dbEmpleado;
    return {
        ...rest,
        nif: decryptNIF(nif),
        pin_enabled: !!dbEmpleado.pin_enabled
    };
}

// Asigna un PIN al empleado comprobando unicidad en el tenant
async function assignPinToEmpleado(empleadoId, clienteId, pin) {
    await assertPinUnique(clienteId, pin, empleadoId);
    const pinHash = await hashPin(pin);
    await query(
        'UPDATE TFG_empleados SET pin_hash = ?, pin_enabled = 1 WHERE id = ? AND cliente_id = ?',
        [pinHash, empleadoId, clienteId]
    );
}

// REGISTRAR EMPLEADO (POST) - Requiere cliente_id
router.post('/registro', async (req, res) => {
    const { cliente_id, nombre, email, password, nif, rol, politica_aceptada, pin } = req.body;
    if (!cliente_id) return res.status(400).json({ error: "Falta cliente_id" });

    try {
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);
        const encryptedNif = encryptNIF(nif);

        let pinHash = null;
        let pinEnabled = 0;
        if (pin && String(pin).trim() !== '') {
            if (!isValidPin(pin)) {
                return res.status(400).json({ error: 'El PIN debe tener exactamente 4 caracteres alfanuméricos' });
            }
            await assertPinUnique(cliente_id, pin);
            pinHash = await hashPin(pin);
            pinEnabled = 1;
        }

        const sql = `INSERT INTO TFG_empleados (cliente_id, nombre, email, nif, hash_password, pin_hash, pin_enabled, rol, politica_privacidad_aceptada) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`;
        await query(sql, [cliente_id, nombre, email, encryptedNif, hashedPassword, pinHash, pinEnabled, rol, politica_aceptada ? 1 : 0]);

        res.status(201).json({ message: "Empleado registrado con éxito." });
    } catch (error) {
        if (error.statusCode === 409) {
            return res.status(409).json({ error: error.message });
        }
        res.status(500).json({ error: error.message });
    }
});

// LISTAR EMPLEADOS DE UNA EMPRESA (GET)
router.get('/lista/:cliente_id', async (req, res) => {
    try {
        const rows = await query('SELECT * FROM TFG_empleados WHERE cliente_id = ?', [req.params.cliente_id]);
        const employees = rows.map(emp => toPublicEmpleado(emp));
        res.json(employees);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// LOGIN DE EMPLEADO (POST) - legacy email/password
router.post('/login', async (req, res) => {
    const { email, password } = req.body;
    try {
        const rows = await query('SELECT * FROM TFG_empleados WHERE email = ?', [email]);
        if (rows.length === 0) return res.status(401).json({ error: "Credenciales inválidas" });

        const dbEmpleado = rows[0];
        const passwordMatch = await bcrypt.compare(password, dbEmpleado.hash_password);
        if (!passwordMatch) return res.status(401).json({ error: "Credenciales inválidas" });

        res.json({
            message: "Login exitoso",
            empleado: toPublicEmpleado(dbEmpleado)
        });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// GENERAR O ASIGNAR PIN DE KIOSKO (el PIN en claro solo se devuelve aqui)
router.post('/generar-pin/:id', async (req, res) => {
    const empleadoId = parseInt(req.params.id, 10);
    const customPin = req.body && req.body.pin != null ? String(req.body.pin).trim() : '';

    try {
        const rows = await query(
            'SELECT id, cliente_id, nombre FROM TFG_empleados WHERE id = ?',
            [empleadoId]
        );
        if (rows.length === 0) {
            return res.status(404).json({ error: 'Empleado no encontrado' });
        }
        const emp = rows[0];
        let pin = customPin;

        if (pin) {
            if (!isValidPin(pin)) {
                return res.status(400).json({ error: 'El PIN debe tener exactamente 4 caracteres alfanuméricos' });
            }
            await assignPinToEmpleado(empleadoId, emp.cliente_id, pin);
        } else {
            let assigned = false;
            for (let attempt = 0; attempt < 40; attempt++) {
                pin = generateRandomPin();
                try {
                    await assignPinToEmpleado(empleadoId, emp.cliente_id, pin);
                    assigned = true;
                    break;
                } catch (err) {
                    if (err.statusCode !== 409) throw err;
                }
            }
            if (!assigned) {
                return res.status(409).json({ error: 'No se pudo generar un PIN único. Inténtalo de nuevo.' });
            }
        }

        res.json({
            success: true,
            pin,
            pin_enabled: true,
            empleado_id: empleadoId,
            nombre: emp.nombre,
            message: 'PIN asignado. Guárdalo ahora: no se puede consultar de nuevo.'
        });
    } catch (error) {
        if (error.statusCode === 409) {
            return res.status(409).json({ error: error.message });
        }
        console.error('Error en generar-pin:', error);
        res.status(500).json({ error: error.message });
    }
});

// QUITAR PIN DE KIOSKO
router.post('/quitar-pin/:id', async (req, res) => {
    const empleadoId = parseInt(req.params.id, 10);
    try {
        const result = await query(
            'UPDATE TFG_empleados SET pin_hash = NULL, pin_enabled = 0 WHERE id = ?',
            [empleadoId]
        );
        if (result.affectedRows === 0) {
            return res.status(404).json({ error: 'Empleado no encontrado' });
        }
        res.json({ success: true, pin_enabled: false, message: 'PIN del kiosko desactivado' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// LOGIN RAPIDO POR PIN PARA KIOSKO (requiere JWT tenant)
router.post('/login-pin', pinLoginLimiter, requireTenantAuth, async (req, res) => {
    const { pin } = req.body;
    const clienteId = req.tenant.clienteId;

    if (!isValidPin(pin)) {
        return res.status(400).json({ error: 'PIN inválido' });
    }

    try {
        const rows = await query(
            'SELECT id, cliente_id, nombre, email, rol, pin_hash, pin_enabled FROM TFG_empleados WHERE cliente_id = ? AND pin_enabled = 1',
            [clienteId]
        );

        let matched = null;
        for (const emp of rows) {
            if (await comparePin(pin, emp.pin_hash)) {
                matched = emp;
                break;
            }
        }

        if (!matched) {
            return res.status(401).json({ error: 'PIN incorrecto' });
        }

        res.json({
            success: true,
            empleado: {
                id: matched.id,
                cliente_id: matched.cliente_id,
                nombre: matched.nombre,
                email: matched.email,
                rol: matched.rol
            }
        });
    } catch (error) {
        console.error('Error en login-pin:', error);
        res.status(500).json({ error: error.message });
    }
});

// ACTUALIZAR EMPLEADO (PUT)
router.put('/update/:id', async (req, res) => {
    const { nombre, email, password, nif, rol, pin, pin_enabled } = req.body;
    const { id } = req.params;

    try {
        const existing = await query('SELECT cliente_id FROM TFG_empleados WHERE id = ?', [id]);
        if (existing.length === 0) {
            return res.status(404).json({ error: 'Empleado no encontrado' });
        }
        const clienteId = existing[0].cliente_id;

        let sql = "UPDATE TFG_empleados SET nombre = ?, email = ?, nif = ?, rol = ? ";
        let params = [nombre, email, encryptNIF(nif), rol];

        if (password && password.trim() !== "") {
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash(password, salt);
            sql += ", hash_password = ? ";
            params.push(hashedPassword);
        }

        if (pin !== undefined) {
            if (pin === null || String(pin).trim() === '') {
                sql += ", pin_hash = NULL, pin_enabled = 0 ";
            } else {
                if (!isValidPin(pin)) {
                    return res.status(400).json({ error: 'El PIN debe tener exactamente 4 caracteres alfanuméricos' });
                }
                await assertPinUnique(clienteId, pin, parseInt(id, 10));
                const pinHash = await hashPin(pin);
                sql += ", pin_hash = ?, pin_enabled = ? ";
                params.push(pinHash, pin_enabled === false ? 0 : 1);
            }
        } else if (pin_enabled !== undefined) {
            sql += ", pin_enabled = ? ";
            params.push(pin_enabled ? 1 : 0);
        }

        sql += " WHERE id = ?";
        params.push(id);

        await query(sql, params);
        res.json({ success: true, message: "Empleado actualizado con éxito." });
    } catch (error) {
        if (error.statusCode === 409) {
            return res.status(409).json({ error: error.message });
        }
        res.status(500).json({ error: error.message });
    }
});

// ELIMINAR EMPLEADO (DELETE)
router.delete('/delete/:id', async (req, res) => {
    try {
        await query('DELETE FROM TFG_empleados WHERE id = ?', [req.params.id]);
        res.json({ success: true, message: "Empleado eliminado." });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
