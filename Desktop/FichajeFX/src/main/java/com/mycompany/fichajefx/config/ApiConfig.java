package com.mycompany.fichajefx.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ApiConfig {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ApiConfig.class.getClassLoader().getResourceAsStream("api.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private ApiConfig() {
    }

    // Lee propiedad del sistema o del fichero api.properties
    private static String get(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        return props.getProperty(key, defaultValue);
    }

    public static String getProtocol() {
        return get("api.protocol", "http");
    }

    public static String getHost() {
        return get("api.host", "localhost");
    }

    public static String getPort() {
        return get("api.port", "6003");
    }

    public static String getPath() {
        return get("api.path", "/api");
    }

    public static String getBaseUrl() {
        return getProtocol() + "://" + getHost() + ":" + getPort() + getPath();
    }

    // Construye la URL completa de un endpoint
    public static String url(String endpoint) {
        String path = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
        return getBaseUrl() + path;
    }

    public static final class Endpoints {
        public static final String CLIENTES_LOGIN = "/clientes/login";
        public static final String CONFIGURACION = "/configuracion";
        public static final String CONFIGURACION_UPDATE = "/configuracion/update";
        public static final String EMPLEADOS_LISTA = "/empleados/lista";
        public static final String EMPLEADOS_REGISTRO = "/empleados/registro";
        public static final String EMPLEADOS_UPDATE = "/empleados/update";
        public static final String EMPLEADOS_DELETE = "/empleados/delete";
        public static final String EMPLEADOS_GENERAR_PIN = "/empleados/generar-pin";
        public static final String EMPLEADOS_QUITAR_PIN = "/empleados/quitar-pin";
        public static final String FICHAJES_AUDITORIA = "/fichajes/auditoria";

        private Endpoints() {
        }
    }
}
