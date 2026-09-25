package com.mycompany.fichajefx.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.fichajefx.config.ApiConfig;
import com.mycompany.fichajefx.models.BrandingConfig;
import com.mycompany.fichajefx.models.Empleado;
import com.mycompany.fichajefx.models.Fichaje;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiService {
        private static final ObjectMapper mapper = new ObjectMapper();

        // CLIENT LOGIN (EMPRESA)
        public static JsonNode loginCliente(String email, String password) throws Exception {
                var body = new java.util.HashMap<String, String>();
                body.put("email_admin", email);
                body.put("password", password);

                String json = mapper.writeValueAsString(body);
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.CLIENTES_LOGIN)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                return mapper.readTree(response.body());
        }

        // BRANDING (Scoped by cliente_id)
        public static BrandingConfig getBranding(String clienteId) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.CONFIGURACION + "/" + clienteId)))
                                .GET()
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                return mapper.readValue(response.body(), BrandingConfig.class);
        }

        public static void updateBranding(String clienteId, BrandingConfig config) throws Exception {
                config.setCliente_id(clienteId);
                String json = mapper.writeValueAsString(config);
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.CONFIGURACION_UPDATE)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();
                NetworkClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        }

        // EMPLEADOS (Scoped by cliente_id)
        public static List<Empleado> getEmpleados(String clienteId) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_LISTA + "/" + clienteId)))
                                .GET()
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                return mapper.readValue(response.body(),
                                mapper.getTypeFactory().constructCollectionType(List.class, Empleado.class));
        }

        public static void registrarEmpleado(String clienteId, Empleado emp, String password) throws Exception {
                var body = new java.util.HashMap<String, Object>();
                body.put("cliente_id", clienteId);
                body.put("nombre", emp.getNombre());
                body.put("email", emp.getEmail());
                body.put("nif", emp.getNif());
                body.put("rol", emp.getRol());
                body.put("password", password);
                body.put("politica_aceptada", true);

                String json = mapper.writeValueAsString(body);
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_REGISTRO)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();
                NetworkClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        }

        // AUDITORIA (Scoped by cliente_id)
        public static List<Fichaje> getAuditoria(String clienteId) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.FICHAJES_AUDITORIA + "/" + clienteId)))
                                .GET()
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                return mapper.readValue(response.body(),
                                mapper.getTypeFactory().constructCollectionType(List.class, Fichaje.class));
        }

        public static void updateEmpleado(int id, Empleado emp, String password) throws Exception {
                var body = new java.util.HashMap<String, Object>();
                body.put("nombre", emp.getNombre());
                body.put("email", emp.getEmail());
                body.put("nif", emp.getNif());
                body.put("rol", emp.getRol());
                if (password != null && !password.isEmpty()) {
                        body.put("password", password);
                }

                String json = mapper.writeValueAsString(body);
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_UPDATE + "/" + id)))
                                .header("Content-Type", "application/json")
                                .PUT(HttpRequest.BodyPublishers.ofString(json))
                                .build();
                NetworkClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        }

        public static void deleteEmpleado(int id) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_DELETE + "/" + id)))
                                .DELETE()
                                .build();
                NetworkClient.getClient().send(request, HttpResponse.BodyHandlers.ofString());
        }

        // Genera PIN de kiosko en el servidor y devuelve el codigo en claro una sola vez
        public static String generarPinEmpleado(int empleadoId, String pinManual) throws Exception {
                var body = new java.util.HashMap<String, Object>();
                if (pinManual != null && !pinManual.isBlank()) {
                        body.put("pin", pinManual.trim());
                }
                String json = mapper.writeValueAsString(body);
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_GENERAR_PIN + "/" + empleadoId)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                JsonNode root = mapper.readTree(response.body());
                if (response.statusCode() >= 400) {
                        String err = root.has("error") ? root.get("error").asText() : "Error al generar PIN";
                        throw new Exception(err);
                }
                if (!root.has("pin")) {
                        throw new Exception("Respuesta sin PIN del servidor");
                }
                return root.get("pin").asText();
        }

        public static void quitarPinEmpleado(int empleadoId) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ApiConfig.url(ApiConfig.Endpoints.EMPLEADOS_QUITAR_PIN + "/" + empleadoId)))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                                .build();
                HttpResponse<String> response = NetworkClient.getClient().send(request,
                                HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 400) {
                        JsonNode root = mapper.readTree(response.body());
                        String err = root.has("error") ? root.get("error").asText() : "Error al quitar PIN";
                        throw new Exception(err);
                }
        }
}
