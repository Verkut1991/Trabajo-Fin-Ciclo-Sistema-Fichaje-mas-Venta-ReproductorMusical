-- phpMyAdmin SQL Dump
-- version 5.2.1deb3
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 03-01-2026 a las 17:37:16
-- Versión del servidor: 10.11.13-MariaDB-0ubuntu0.24.04.1
-- Versión de PHP: 8.3.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `tfg_demo`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TFG_clientes`
--

CREATE TABLE `TFG_clientes` (
  `id` int(11) NOT NULL,
  `nombre_empresa` varchar(150) NOT NULL,
  `email_admin` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `plan_contratado` enum('Basic','Pro','Enterprise') DEFAULT 'Basic',
  `fecha_registro` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TFG_empleados`
--

CREATE TABLE `TFG_empleados` (
  `id` int(11) NOT NULL,
  `cliente_id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `nif` varbinary(255) NOT NULL,
  `hash_password` varchar(255) NOT NULL,
  `pin_hash` varchar(255) DEFAULT NULL,
  `pin_enabled` tinyint(1) NOT NULL DEFAULT 0,
  `rol` enum('admin','empleado','rrhh') DEFAULT 'empleado',
  `politica_privacidad_aceptada` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TFG_fichajes`
--

CREATE TABLE `TFG_fichajes` (
  `id` int(11) NOT NULL,
  `empleado_id` int(11) NOT NULL,
  `tipo` enum('entrada','salida') NOT NULL,
  `timestamp` timestamp NULL DEFAULT current_timestamp(),
  `ubicacion` varchar(100) DEFAULT NULL,
  `hash_integridad` char(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TFG_suscripciones`
--

CREATE TABLE `TFG_suscripciones` (
  `id` int(11) NOT NULL,
  `userId` varchar(255) NOT NULL,
  `planId` varchar(50) NOT NULL,
  `workerCount` int(11) DEFAULT 1,
  `totalPrice` decimal(10,2) DEFAULT NULL,
  `purchaseDate` timestamp NULL DEFAULT current_timestamp(),
  `expiryDate` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `TFG_clientes`
--
ALTER TABLE `TFG_clientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email_admin` (`email_admin`);

--
-- Indices de la tabla `TFG_empleados`
--
ALTER TABLE `TFG_empleados`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `fk_empleado_cliente` (`cliente_id`);

--
-- Indices de la tabla `TFG_fichajes`
--
ALTER TABLE `TFG_fichajes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_fichaje_empleado` (`empleado_id`);

--
-- Indices de la tabla `TFG_suscripciones`
--
ALTER TABLE `TFG_suscripciones`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `TFG_clientes`
--
ALTER TABLE `TFG_clientes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `TFG_empleados`
--
ALTER TABLE `TFG_empleados`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `TFG_fichajes`
--
ALTER TABLE `TFG_fichajes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `TFG_suscripciones`
--
ALTER TABLE `TFG_suscripciones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `TFG_empleados`
--
ALTER TABLE `TFG_empleados`
  ADD CONSTRAINT `fk_empleado_cliente` FOREIGN KEY (`cliente_id`) REFERENCES `TFG_clientes` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `TFG_fichajes`
--
ALTER TABLE `TFG_fichajes`
  ADD CONSTRAINT `fk_fichaje_empleado` FOREIGN KEY (`empleado_id`) REFERENCES `TFG_empleados` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
