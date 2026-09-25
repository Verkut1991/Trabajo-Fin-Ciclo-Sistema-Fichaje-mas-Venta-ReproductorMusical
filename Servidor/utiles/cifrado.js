const crypto = require('crypto');

// Configuración de cifrado (Usa 32 caracteres para la clave en .env)
const ENCRYPTION_KEY = process.env.ENCRYPTION_KEY || '0123456789abcdef0123456789abcdef';
const IV_LENGTH = 16;

const encryptNIF = (text) => {
    let iv = crypto.randomBytes(IV_LENGTH);
    let cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(ENCRYPTION_KEY), iv);
    let encrypted = cipher.update(text);
    encrypted = Buffer.concat([encrypted, cipher.final()]);
    return iv.toString('hex') + ':' + encrypted.toString('hex');
};

const decryptNIF = (text) => {
    if (!text) return '';
    // Si viene como Buffer (a veces pasa con MySQL), convertir a string
    const encryptedStr = text.toString();

    if (!encryptedStr.includes(':')) return encryptedStr;

    let textParts = encryptedStr.split(':');
    let iv = Buffer.from(textParts.shift(), 'hex');
    let encryptedText = Buffer.from(textParts.join(':'), 'hex');
    let decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(ENCRYPTION_KEY), iv);
    let decrypted = decipher.update(encryptedText);
    decrypted = Buffer.concat([decrypted, decipher.final()]);
    return decrypted.toString();
};

const generateIntegrityHash = (data) => {
    // Añadimos el cliente_id al contenido del hash para evitar suplantaciones entre empresas
    const content = `${data.cliente_id}-${data.empleado_id}-${data.timestamp}-${data.tipo}`;
    return crypto.createHmac('sha256', process.env.HASH_SECRET || 'change-me-hash')
        .update(content)
        .digest('hex');
};

module.exports = { encryptNIF, decryptNIF, generateIntegrityHash };