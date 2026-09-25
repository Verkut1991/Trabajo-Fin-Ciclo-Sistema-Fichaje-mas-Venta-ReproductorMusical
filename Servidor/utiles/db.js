const mysql = require('mysql');
const { MongoClient } = require('mongodb');
const util = require('util');

// Configura MySQL desde variables de entorno sin credenciales en claro
const mysqlConfig = {
    host: process.env.MYSQL_HOST || 'localhost',
    port: parseInt(process.env.MYSQL_PORT || '3306', 10),
    user: process.env.MYSQL_USER || 'tfg',
    password: process.env.MYSQL_PASSWORD || 'change-me',
    database: process.env.MYSQL_DATABASE || 'tfg_demo',
    connectionLimit: 10
};

const mysqlPool = mysql.createPool(mysqlConfig);

const query = util.promisify(mysqlPool.query).bind(mysqlPool);

// Configura MongoDB desde el entorno
const mongoUri = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const mongoClient = new MongoClient(mongoUri);

let mongoDb = null;

async function connectMongo() {
    try {
        await mongoClient.connect();
        mongoDb = mongoClient.db(process.env.MONGODB_DB || 'tfg_demo');
        console.log('MongoDB conectado');
    } catch (error) {
        console.error('Error en MongoDB:', error);
    }
}

module.exports = {
    mysqlPool,
    query,
    connectMongo,
    getMongoDb: () => mongoDb
};
