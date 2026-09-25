# FichajeFX

End-of-cycle project that combines a multi-client **time-clock (fichaje)** system with **plan sales** and a workplace **music-player** product experience. A single organization registers, employees clock in and out from phone or kiosk, admins manage that organization from a desktop console, and a public web funnel sells subscription tiers against the same API.

> Local demo. Copy the env examples, point them at your own MySQL and MongoDB, and run. Do not use the placeholder secrets in production.

A companion music-player component was developed alongside this suite for the workplace listening angle.

## Goal

Small businesses need attendance that is mono-tenant (one organization), pin-friendly at a shared kiosk, and sold as a product—not a spreadsheet. This suite solves that with one central API and thin clients for web marketing/sales, staff administration, employee mobile, and lobby kiosk.

## Stack

| Layer | Tech |
|-------|------|
| API | Node.js, Express, JWT, bcrypt |
| Relational data | MySQL / MariaDB (`TFG_*` tables) |
| Document data | MongoDB (official driver) |
| Marketing / sales web | React (Create React App) |
| Admin desktop | Java Swing (Maven) |
| Employee app | Android (Gradle) |
| Lobby kiosk | Godot (GDScript) |

## Features

- **Organization auth** — company register/login; JWT access + refresh for the kiosk
- **Employees** — email/password login, optional PIN for fast kiosk check-in
- **Clock events** — clock-in / clock-out with integrity hashing and encrypted NIF fields
- **Plan sales** — web pricing cards call `/ventas/comprar` to upgrade Basic / Pro / Enterprise
- **Admin desktop** — Java console for business administration against the API
- **Android client** — employee flows on emulator (`10.0.2.2`)
- **Godot kiosk** — PIN login and clock UI for a shared lobby device

## Project layout

```
.
├── Servidor/                 # Express API (port 6003)
│   ├── rutas/                # clientes, empleados, fichajes, ventas, configuracion
│   ├── utiles/               # db, jwt, cifrado, pin
│   ├── migrations/
│   └── .env.example
├── Web/                      # React marketing + plan purchase (port 6010)
├── Desktop/FichajeFX/        # Java Swing admin
├── Movil/                    # Android employee app
├── Kiosko/kiosko-fichaje-fx/ # Godot kiosk
├── tablasTFG.sql             # MySQL schema
└── seed-mock.sql             # minimal fictional organization row
```

## Run the demo

**Requirements:** Node.js 18+, MySQL/MariaDB, MongoDB, JDK 17+ (desktop), Android Studio and/or Godot 4 (optional clients).

### 1. Databases

```bash
# create an empty MySQL database, then:
mysql -u tfg -p tfg_demo < tablasTFG.sql
mysql -u tfg -p tfg_demo < seed-mock.sql
# start MongoDB locally (default mongodb://localhost:27017, db tfg_demo)
```

### 2. API

```bash
cd Servidor
cp .env.example .env
# edit secrets / DB credentials
npm install
npm start
```

Default listen port is **6003** (`PORT`).

### 3. Web (optional)

```bash
cd Web
cp .env.example .env
npm install
# HOST/PORT from .env.example → http://localhost:6010
npm start
```

`REACT_APP_API_*` should point at the API (default `http://localhost:6003/api`).

### 4. Desktop admin (optional)

```bash
cd Desktop/FichajeFX
cp api.properties.example api.properties
mvn -q package
# run the main UI class against http://localhost:6003
```

### 5. Android / Godot (optional)

- **Android:** open `Movil/` in Android Studio; Retrofit uses `http://10.0.2.2:6003/`.
- **Kiosk:** open `Kiosko/kiosko-fichaje-fx/` in Godot; API base defaults to `http://localhost:6003/api`.

### Environment variables

**API (`Servidor/.env`)**

| Variable | Description |
|----------|-------------|
| `PORT` | API port (default `6003`) |
| `MYSQL_HOST` / `MYSQL_PORT` | MySQL host and port |
| `MYSQL_USER` / `MYSQL_PASSWORD` | MySQL credentials |
| `MYSQL_DATABASE` | Schema name (default `tfg_demo`) |
| `MONGODB_URI` / `MONGODB_DB` | MongoDB connection |
| `JWT_ACCESS_SECRET` / `JWT_REFRESH_SECRET` | JWT signing secrets |
| `ENCRYPTION_KEY` | 32-byte key for NIF encryption |
| `HASH_SECRET` | HMAC secret for clock-event integrity |

**Web (`Web/.env`)**

| Variable | Description |
|----------|-------------|
| `HOST` / `PORT` | CRA bind (default `6010`) |
| `REACT_APP_API_PROTOCOL` / `HOST` / `PORT` / `PATH` | API base URL pieces |

## License

ISC for the API (`Servidor/package.json`). Other clients inherit no explicit license declaration in-tree.
