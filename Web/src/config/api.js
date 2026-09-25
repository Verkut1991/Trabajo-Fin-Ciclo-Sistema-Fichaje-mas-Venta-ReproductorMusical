const protocol = process.env.REACT_APP_API_PROTOCOL || 'http';
const host = process.env.REACT_APP_API_HOST || 'localhost';
const port = process.env.REACT_APP_API_PORT || '6003';
const apiPath = process.env.REACT_APP_API_PATH || '/api';

export const API_BASE_URL = `${protocol}://${host}:${port}${apiPath}`;

export const ENDPOINTS = {
    clientes: {
        login: '/clientes/login',
        registro: '/clientes/registro',
    },
    ventas: {
        comprar: '/ventas/comprar',
    },
};
