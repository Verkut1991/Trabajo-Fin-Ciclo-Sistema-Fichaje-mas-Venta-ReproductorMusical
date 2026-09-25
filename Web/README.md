# FichaMaster Web

Frontend React con Create React App (`react-scripts`).

## Desarrollo local

```bash
npm install
npm start
```

Abre http://localhost:6010

## Servidor dam2 (SSH)

```bash
cp .env.example .env
# Edita REACT_APP_API_HOST si hace falta (p. ej. 192.168.1.82)
npm install
npm start
```

- Web: puerto **6010** (`PORT=6010` y `HOST=0.0.0.0` en `.env` para acceso en red)
- API Node: puerto **6003** en `../Servidor` (`node server.js`)

## Producción

```bash
npm run build
```

Genera la carpeta `build/` para servir con el servidor web del centro.

## Variables de entorno

Prefijo `REACT_APP_` para la configuración del API. Ver `.env.example`.
