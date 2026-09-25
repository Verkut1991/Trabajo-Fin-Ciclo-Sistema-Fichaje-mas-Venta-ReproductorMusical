-- Migracion: PIN de kiosko para empleados
-- Ejecutar en la base de datos tfg_demo antes de usar login-pin

ALTER TABLE `TFG_empleados`
  ADD COLUMN `pin_hash` VARCHAR(255) NULL DEFAULT NULL AFTER `hash_password`,
  ADD COLUMN `pin_enabled` TINYINT(1) NOT NULL DEFAULT 0 AFTER `pin_hash`;
