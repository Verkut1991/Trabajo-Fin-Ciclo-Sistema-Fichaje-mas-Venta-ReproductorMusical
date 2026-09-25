const express = require('express');
const router = express.Router();
const { query } = require('../utiles/db');

// PROCESAR COMPRA Y ACTUALIZAR PLAN
router.post('/comprar', async (req, res) => {
    const { cliente_id, planId } = req.body; // planId: 'Basic', 'Pro', 'Enterprise'
    try {
        const sql = `UPDATE TFG_clientes SET plan_contratado = ? WHERE id = ?`;
        await query(sql, [planId, cliente_id]);

        res.status(200).json({ success: true, message: `Plan actualizado a ${planId}` });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// CONSULTAR ESTADO (Para Web/App)
router.get('/status/:cliente_id', async (req, res) => {
    try {
        const rows = await query('SELECT plan_contratado FROM TFG_clientes WHERE id = ?', [req.params.cliente_id]);
        if (rows.length === 0) return res.status(404).json({ error: "Cliente no encontrado" });
        res.json({ plan: rows[0].plan_contratado });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
