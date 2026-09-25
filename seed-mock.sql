-- Fictional sample row for local demo (not real people or companies)
-- Import after tablasTFG.sql

INSERT INTO `TFG_clientes` (`id`, `nombre_empresa`, `email_admin`, `password_hash`, `plan_contratado`, `fecha_registro`) VALUES
(1, 'Acme Demo S.L.', 'admin@example.com', '$2b$10$abcdefghijklmnopqrstuv', 'Basic', '2026-01-01 00:00:00');

-- Employees and clock-ins: create via the API when running locally
