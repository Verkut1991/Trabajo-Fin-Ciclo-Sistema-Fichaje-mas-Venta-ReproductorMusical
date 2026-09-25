import { API_BASE_URL } from '../config/api';

// Petición POST JSON al API centralizado
export async function apiPost(endpoint, body) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        });
    } catch (err) {
        const hint = `No se alcanza ${API_BASE_URL}. ¿Está arrancado el Express (node server.js en Servidor)?`;
        const error = new Error(hint);
        error.cause = err;
        throw error;
    }

    let data = {};
    const text = await response.text();
    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            const error = new Error(`Respuesta no válida del servidor (HTTP ${response.status})`);
            error.status = response.status;
            throw error;
        }
    }
    return { response, data };
}
