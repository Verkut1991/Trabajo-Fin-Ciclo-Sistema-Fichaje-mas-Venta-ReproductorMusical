const bcrypt = require('bcrypt');
const crypto = require('crypto');

const PIN_REGEX = /^[A-Za-z0-9]{4}$/;
const PIN_CHARSET = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';

// Comprueba que el PIN tenga exactamente 4 caracteres alfanumericos
function isValidPin(pin) {
    return typeof pin === 'string' && PIN_REGEX.test(pin);
}

// Genera el hash bcrypt del PIN del empleado
async function hashPin(pin) {
    const salt = await bcrypt.genSalt(10);
    return bcrypt.hash(pin, salt);
}

// Compara un PIN en claro con su hash almacenado
async function comparePin(pin, pinHash) {
    if (!pinHash) return false;
    return bcrypt.compare(pin, pinHash);
}

// Genera un PIN aleatorio de 4 caracteres para el kiosko
function generateRandomPin() {
    let pin = '';
    for (let i = 0; i < 4; i++) {
        pin += PIN_CHARSET[crypto.randomInt(0, PIN_CHARSET.length)];
    }
    return pin;
}

module.exports = { isValidPin, hashPin, comparePin, generateRandomPin, PIN_REGEX };
