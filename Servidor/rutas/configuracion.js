const express = require('express');
const router = express.Router();
const { Config } = require('../utiles/mongo_esquema');

// OBTENER CONFIGURACIÓN (GET)
router.get('/:cliente_id', async (req, res) => {
    try {
        const config = await Config.findOne(req.params.cliente_id);
        res.json(config || { cliente_id: req.params.cliente_id, nombre_suite: "FichaMaster", colores: { primary: "#6366f1" }, logo_url: "" });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// ACTUALIZAR CONFIGURACIÓN (POST)
router.post('/update', async (req, res) => {
    const { cliente_id, nombre_suite, colores, logo_url } = req.body;
    if (!cliente_id) return res.status(400).json({ error: "Falta cliente_id" });

    try {
        await Config.findOneAndUpdate(cliente_id, { nombre_suite, colores, logo_url });
        res.json({ success: true, message: "Branding actualizado" });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;