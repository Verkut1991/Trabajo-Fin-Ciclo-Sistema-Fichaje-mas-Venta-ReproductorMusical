const express = require('express');
const http = require('http');
const cors = require('cors');
const { connectMongo } = require('./utiles/db');

// Conectar a las bases de datos al arrancar
connectMongo().catch(console.error);



const app = express();

// Middlewares
app.use(cors());
app.use(express.json());

// Importar Rutas
const fichajesRoutes = require('./rutas/fichajes');
const empleadosRoutes = require('./rutas/empleados');
const configuracionRoutes = require('./rutas/configuracion');
const ventasRoutes = require('./rutas/ventas');
const clientesRoutes = require('./rutas/clientes');

// Uso de Rutas
app.use('/api/fichajes', fichajesRoutes);
app.use('/api/empleados', empleadosRoutes);
app.use('/api/configuracion', configuracionRoutes);
app.use('/api/ventas', ventasRoutes);
app.use('/api/clientes', clientesRoutes);

// Servidor HTTP
const PORT = process.env.PORT || 6003;
const server = http.createServer(app);

server.listen(PORT, () => {
    console.log(`🚀 Servidor centralizado corriendo en http://localhost:${PORT}`);
});