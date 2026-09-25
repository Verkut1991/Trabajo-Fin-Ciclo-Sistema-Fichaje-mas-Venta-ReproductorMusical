const { getMongoDb } = require('./db');

// Helper para obtener colecciones centralizadamente
const collections = {
    getConfig: () => getMongoDb().collection('TFG_Configuracion'),
    getAudit: () => getMongoDb().collection('TFG_Auditoria')
};

// Funciones de utilidad particionadas por cliente_id
const Config = {
    findOne: (cliente_id) => collections.getConfig().findOne({ cliente_id: String(cliente_id) }),
    findOneAndUpdate: (cliente_id, update, options) => {
        return collections.getConfig().findOneAndUpdate(
            { cliente_id: String(cliente_id) },
            { $set: { ...update, cliente_id: String(cliente_id) } },
            { ...options, upsert: true }
        );
    }
};

const Audit = {
    create: (cliente_id, data) => {
        return collections.getAudit().insertOne({
            ...data,
            cliente_id: String(cliente_id),
            fecha: new Date()
        });
    },
    find: (cliente_id) => {
        return collections.getAudit().find({ cliente_id: String(cliente_id) }).sort({ fecha: -1 }).toArray();
    }
};

module.exports = { Config, Audit };