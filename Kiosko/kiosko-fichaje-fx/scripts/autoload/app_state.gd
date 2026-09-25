extends Node

## Estado de sesion del kiosko: tenant JWT y empleado activo por PIN.

const SESSION_PATH := "user://kiosk_session.cfg"

var empleado_id: int = -1
var cliente_id: int = -1
var nombre: String = ""
var email: String = ""
var rol: String = ""
var is_logged_in: bool = false

var nombre_empresa: String = ""
var tenant_email: String = ""
var access_token: String = ""
var refresh_token: String = ""
var token_expires_at: int = 0
var setup_completado: bool = false


func _ready() -> void:
	load_tenant_session()


# Indica si el kiosko ya tiene configurado el tenant con tokens validos
func is_setup_complete() -> bool:
	return setup_completado and cliente_id > 0 and not access_token.is_empty()


# Indica si el access token esta caducado o a punto de caducar
func is_token_expired() -> bool:
	if token_expires_at <= 0:
		return true
	return Time.get_unix_time_from_system() >= token_expires_at - 60


func set_tenant_session(cliente: Dictionary, tokens: Dictionary) -> void:
	cliente_id = int(cliente.get("id", -1))
	nombre_empresa = str(cliente.get("nombre_empresa", ""))
	tenant_email = str(cliente.get("email_admin", ""))
	access_token = str(tokens.get("accessToken", ""))
	refresh_token = str(tokens.get("refreshToken", ""))
	var expires_in := int(tokens.get("expiresIn", 3600))
	token_expires_at = int(Time.get_unix_time_from_system()) + expires_in
	setup_completado = cliente_id > 0 and not access_token.is_empty()
	save_tenant_session()


func update_access_token(new_token: String, expires_in: int) -> void:
	access_token = new_token
	token_expires_at = int(Time.get_unix_time_from_system()) + expires_in
	save_tenant_session()


func set_session(empleado: Dictionary) -> void:
	empleado_id = int(empleado.get("id", -1))
	if cliente_id <= 0:
		cliente_id = int(empleado.get("cliente_id", -1))
	nombre = str(empleado.get("nombre", ""))
	email = str(empleado.get("email", ""))
	rol = str(empleado.get("rol", ""))
	is_logged_in = empleado_id > 0 and cliente_id > 0


func clear_employee_session() -> void:
	empleado_id = -1
	nombre = ""
	email = ""
	rol = ""
	is_logged_in = false


func clear_tenant_session() -> void:
	clear_employee_session()
	cliente_id = -1
	nombre_empresa = ""
	tenant_email = ""
	access_token = ""
	refresh_token = ""
	token_expires_at = 0
	setup_completado = false
	if FileAccess.file_exists(SESSION_PATH):
		DirAccess.remove_absolute(SESSION_PATH)


func save_tenant_session() -> void:
	var cfg := ConfigFile.new()
	cfg.set_value("tenant", "cliente_id", cliente_id)
	cfg.set_value("tenant", "nombre_empresa", nombre_empresa)
	cfg.set_value("tenant", "tenant_email", tenant_email)
	cfg.set_value("tenant", "access_token", access_token)
	cfg.set_value("tenant", "refresh_token", refresh_token)
	cfg.set_value("tenant", "token_expires_at", token_expires_at)
	cfg.set_value("tenant", "setup_completado", setup_completado)
	cfg.save(SESSION_PATH)


func load_tenant_session() -> void:
	if not FileAccess.file_exists(SESSION_PATH):
		return
	var cfg := ConfigFile.new()
	if cfg.load(SESSION_PATH) != OK:
		return
	cliente_id = int(cfg.get_value("tenant", "cliente_id", -1))
	nombre_empresa = str(cfg.get_value("tenant", "nombre_empresa", ""))
	tenant_email = str(cfg.get_value("tenant", "tenant_email", ""))
	access_token = str(cfg.get_value("tenant", "access_token", ""))
	refresh_token = str(cfg.get_value("tenant", "refresh_token", ""))
	token_expires_at = int(cfg.get_value("tenant", "token_expires_at", 0))
	setup_completado = bool(cfg.get_value("tenant", "setup_completado", false))
