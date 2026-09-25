extends Node

## Cliente HTTP para tenant JWT, login PIN, fichaje y tiempo trabajado hoy.

const DEFAULT_BASE_URL := "http://localhost:6003/api"
const UBICACION_KIOSKO := "Kiosko FichajeFX (Godot)"
const SETTINGS_PATH := "res://config/api_settings.cfg"

var base_url: String = DEFAULT_BASE_URL

signal tenant_login_completed(success: bool, cliente: Dictionary, tokens: Dictionary, error_message: String)
signal token_refreshed(success: bool, tokens: Dictionary, error_message: String)
signal login_completed(success: bool, empleado: Dictionary, error_message: String)
signal fichar_completed(success: bool, data: Dictionary, error_message: String)
signal tiempo_hoy_completed(success: bool, data: Dictionary, error_message: String)

var _http: HTTPRequest
var _queue: Array = []
var _busy: bool = false
var _current_job: Dictionary = {}
var _refreshing: bool = false
var _refresh_waiters: Array = []


func _ready() -> void:
	_load_base_url()
	_http = HTTPRequest.new()
	add_child(_http)
	_http.request_completed.connect(_on_request_completed)


func _load_base_url() -> void:
	if not FileAccess.file_exists(SETTINGS_PATH):
		base_url = DEFAULT_BASE_URL
		return
	var cfg := ConfigFile.new()
	if cfg.load(SETTINGS_PATH) != OK:
		base_url = DEFAULT_BASE_URL
		return
	base_url = str(cfg.get_value("api", "base_url", DEFAULT_BASE_URL))


func login_tenant_jwt(email_admin: String, password: String) -> void:
	_enqueue({
		"method": HTTPClient.METHOD_POST,
		"path": "/clientes/login-jwt",
		"body": {"email_admin": email_admin, "password": password},
		"signal_name": "tenant_login_completed"
	})


func refresh_tenant_token() -> void:
	if AppState.refresh_token.is_empty():
		token_refreshed.emit(false, {}, "No hay sesión de kiosko")
		return
	_enqueue({
		"method": HTTPClient.METHOD_POST,
		"path": "/clientes/refresh-token",
		"body": {"refreshToken": AppState.refresh_token},
		"signal_name": "token_refreshed"
	})


func login_empleado_pin(pin: String) -> void:
	_enqueue({
		"method": HTTPClient.METHOD_POST,
		"path": "/empleados/login-pin",
		"body": {"pin": pin},
		"signal_name": "login_completed",
		"auth": true
	})


func fichar(empleado_id: int, cliente_id: int, tipo: String) -> void:
	_enqueue({
		"method": HTTPClient.METHOD_POST,
		"path": "/fichajes/fichar",
		"body": {
			"empleado_id": empleado_id,
			"cliente_id": cliente_id,
			"tipo": tipo.to_lower(),
			"ubicacion": UBICACION_KIOSKO
		},
		"signal_name": "fichar_completed",
		"ok_codes": [201]
	})


func obtener_tiempo_hoy(empleado_id: int) -> void:
	_enqueue({
		"method": HTTPClient.METHOD_GET,
		"path": "/fichajes/tiempo-hoy/%d" % empleado_id,
		"body": null,
		"signal_name": "tiempo_hoy_completed"
	})


# Encola peticiones HTTP para ejecutarlas de forma secuencial
func _enqueue(job: Dictionary) -> void:
	_queue.append(job)
	if not _busy:
		_process_next()


# Lanza la siguiente peticion de la cola si el cliente HTTP esta libre
func _process_next() -> void:
	if _queue.is_empty():
		_busy = false
		return
	if _refreshing:
		return
	var next_job: Dictionary = _queue[0]
	if next_job.get("auth", false) and AppState.is_token_expired() and not AppState.refresh_token.is_empty():
		_refreshing = true
		_refresh_waiters.append("_process_next")
		_do_refresh_request()
		return
	_busy = true
	_current_job = _queue.pop_front()
	_send_job(_current_job)


# Envia una peticion HTTP al servidor
func _send_job(job: Dictionary) -> void:
	var url := base_url + str(job.path)
	var headers := PackedStringArray(["Content-Type: application/json"])
	if job.get("auth", false) and not AppState.access_token.is_empty():
		headers.append("Authorization: Bearer %s" % AppState.access_token)
	var body_text := ""
	if job.body != null:
		body_text = JSON.stringify(job.body)
	var err := _http.request(url, headers, job.method, body_text)
	if err != OK:
		var failed_job := _current_job.duplicate()
		_current_job = {}
		_emit_job_result(failed_job, false, {}, "No se pudo conectar con %s (¿Express arrancado?)" % base_url)
		_busy = false
		_process_next()


# Solicita un nuevo access token usando el refresh token guardado
func _do_refresh_request() -> void:
	var url := base_url + "/clientes/refresh-token"
	var headers := PackedStringArray(["Content-Type: application/json"])
	var body_text := JSON.stringify({"refreshToken": AppState.refresh_token})
	var err := _http.request(url, headers, HTTPClient.METHOD_POST, body_text)
	if err != OK:
		_refreshing = false
		token_refreshed.emit(false, {}, "No se pudo renovar la sesión")
		_refresh_waiters.clear()
		_busy = false


# Procesa la respuesta HTTP y emite la senal correspondiente al tipo de peticion
func _on_request_completed(result: int, response_code: int, _headers: PackedStringArray, body: PackedByteArray) -> void:
	if _refreshing and _current_job.is_empty():
		_refreshing = false
		var parsed_refresh := _parse_json(body)
		if result == HTTPRequest.RESULT_SUCCESS and response_code >= 200 and response_code < 300:
			var new_token := str(parsed_refresh.get("accessToken", ""))
			var expires_in := int(parsed_refresh.get("expiresIn", 3600))
			if not new_token.is_empty():
				AppState.update_access_token(new_token, expires_in)
				token_refreshed.emit(true, parsed_refresh, "")
			else:
				token_refreshed.emit(false, {}, "Respuesta de renovación inválida")
		else:
			var msg := str(parsed_refresh.get("error", "No se pudo renovar la sesión"))
			token_refreshed.emit(false, {}, msg)
		_refresh_waiters.clear()
		_process_next()
		return

	var job := _current_job
	_current_job = {}
	if job.is_empty():
		_busy = false
		_process_next()
		return

	if result != HTTPRequest.RESULT_SUCCESS:
		_emit_job_result(job, false, {}, "Error de red (%s)" % str(result))
	elif response_code == 401 and job.get("auth", false) and not job.get("retried", false):
		if not AppState.refresh_token.is_empty():
			job["retried"] = true
			_queue.push_front(job)
			_busy = false
			_refreshing = true
			_do_refresh_request()
			return
		_emit_job_result(job, false, _parse_json(body), "Sesión del kiosko expirada")
	elif not _is_ok_code(job, response_code):
		var parsed_err := _parse_json(body)
		var msg := str(parsed_err.get("error", "Error HTTP %d" % response_code))
		_emit_job_result(job, false, parsed_err, msg)
	else:
		var parsed := _parse_json(body)
		_emit_job_result(job, true, parsed, "")
	_busy = false
	_process_next()


func _is_ok_code(job: Dictionary, response_code: int) -> bool:
	if job.has("ok_codes"):
		return response_code in job.ok_codes
	return response_code >= 200 and response_code < 300


func _parse_json(body: PackedByteArray) -> Dictionary:
	if body.is_empty():
		return {}
	var text := body.get_string_from_utf8()
	var parsed = JSON.parse_string(text)
	if parsed is Dictionary:
		return parsed
	return {}


func _emit_job_result(job: Dictionary, success: bool, data: Dictionary, error_message: String) -> void:
	match job.signal_name:
		"tenant_login_completed":
			var cliente: Dictionary = {}
			var tokens: Dictionary = {}
			if success:
				if data.has("cliente") and data.cliente is Dictionary:
					cliente = data.cliente
				tokens = {
					"accessToken": str(data.get("accessToken", "")),
					"refreshToken": str(data.get("refreshToken", "")),
					"expiresIn": int(data.get("expiresIn", 3600))
				}
			tenant_login_completed.emit(success, cliente, tokens, error_message)
		"token_refreshed":
			token_refreshed.emit(success, data, error_message)
		"login_completed":
			var empleado: Dictionary = {}
			if success and data.has("empleado") and data.empleado is Dictionary:
				empleado = data.empleado
			login_completed.emit(success, empleado, error_message)
		"fichar_completed":
			fichar_completed.emit(success, data, error_message)
		"tiempo_hoy_completed":
			tiempo_hoy_completed.emit(success, data, error_message)
