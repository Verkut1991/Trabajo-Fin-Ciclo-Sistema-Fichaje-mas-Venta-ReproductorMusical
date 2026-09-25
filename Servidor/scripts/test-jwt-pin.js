/**
 * Pruebas manuales de JWT tenant y login PIN.
 * Uso: node scripts/test-jwt-pin.js [baseUrl]
 */
const bcrypt = require('bcrypt');
const { isValidPin, hashPin } = require('../utiles/pin');
const { signAccessToken, verifyAccessToken, signRefreshToken, verifyRefreshToken } = require('../utiles/jwt');

const baseUrl = process.argv[2] || 'http://localhost:6003/api';

async function request(path, method, body, token) {
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers.Authorization = `Bearer ${token}`;
    const res = await fetch(`${baseUrl}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined
    });
    const data = await res.json().catch(() => ({}));
    return { status: res.status, data };
}

async function run() {
    console.log('--- Utilidades PIN/JWT ---');
    console.assert(isValidPin('PQ50') === true, 'PIN alfanumerico valido');
    console.assert(isValidPin('123') === false, 'PIN corto invalido');
    const hash = await hashPin('PQ50');
    console.assert(await bcrypt.compare('PQ50', hash), 'hash PIN ok');

    const fakeCliente = { id: 1, email_admin: 'test@test.com' };
    const access = signAccessToken(fakeCliente);
    const refresh = signRefreshToken(fakeCliente);
    console.assert(verifyAccessToken(access), 'access token ok');
    console.assert(verifyRefreshToken(refresh), 'refresh token ok');

    console.log('\n--- API (requiere servidor y credenciales reales) ---');
    console.log('Base:', baseUrl);
    console.log('Omitiendo llamadas HTTP si no hay TEST_EMAIL/TEST_PASSWORD en entorno.');
    if (!process.env.TEST_EMAIL || !process.env.TEST_PASSWORD) {
        console.log('Definir TEST_EMAIL y TEST_PASSWORD para probar login-jwt y login-pin.');
        return;
    }
    const login = await request('/clientes/login-jwt', 'POST', {
        email_admin: process.env.TEST_EMAIL,
        password: process.env.TEST_PASSWORD
    });
    console.log('login-jwt:', login.status, login.data.success);
    if (!login.data.accessToken) return;

    const pinTry = await request('/empleados/login-pin', 'POST', { pin: process.env.TEST_PIN || '0000' }, login.data.accessToken);
    console.log('login-pin:', pinTry.status, pinTry.data.success || pinTry.data.error);
}

run().catch(console.error);
