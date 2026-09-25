extends Control

signal fichaje_ok(tipo: String, tiempo_formatted: String)
signal fichaje_error(message: String)
signal logout_requested

const ESTADO_TRABAJANDO := "TRABAJANDO"
const ESTADO_DESCANSANDO := "DESCANSANDO"

@onready var welcome_label: Label = %WelcomeLabel
@onready var time_value: Label = %TimeValue
@onready var status_panel: PanelContainer = %StatusPanel
@onready var status_dot: ColorRect = %StatusDot
@onready var status_label: Label = %StatusLabel
@onready var status_hint: Label = %StatusHint
@onready var time_panel: PanelContainer = %TimePanel
@onready var entrada_button: Button = %EntradaButton
@onready var salida_button: Button = %SalidaButton
@onready var logout_button: Button = %LogoutButton
@onready var status_error: Label = %StatusError
@onready var loading_panel: Panel = %LoadingPanel
@onready var section_title: Label = %SectionTitle

var _pending_tipo: String = ""


func _ready() -> void:
	_apply_theme()
	entrada_button.pressed.connect(func(): _registrar_fichaje("entrada"))
	salida_button.pressed.connect(func(): _registrar_fichaje("salida"))
	logout_button.pressed.connect(func(): logout_requested.emit())
	ApiService.fichar_completed.connect(_on_fichar_completed)
	ApiService.tiempo_hoy_completed.connect(_on_tiempo_hoy_completed)
	loading_panel.visible = false


func _apply_theme() -> void:
	time_panel.add_theme_stylebox_override("panel", KarboUi.style_time_chip())
	KarboUi.apply_success_button(entrada_button)
	KarboUi.apply_danger_button(salida_button)
	KarboUi.apply_ghost_button(logout_button)
	loading_panel.add_theme_stylebox_override("panel", KarboUi.style_loading_overlay())
	KarboUi.apply_label_title(section_title, 28)


func refresh() -> void:
	welcome_label.text = "Hola, %s" % AppState.nombre
	status_error.text = ""
	_cargar_tiempo_hoy()


func _cargar_tiempo_hoy() -> void:
	if not AppState.is_logged_in:
		return
	time_value.text = "…"
	status_label.text = "Consultando estado…"
	ApiService.obtener_tiempo_hoy(AppState.empleado_id)


func _on_tiempo_hoy_completed(success: bool, data: Dictionary, _error_message: String) -> void:
	if not _pending_tipo.is_empty():
		_set_loading(false)
		var tiempo_str := str(data.get("formatted", "--")) if success else "--"
		var tipo := _pending_tipo
		_pending_tipo = ""
		status_error.text = ""
		fichaje_ok.emit(tipo, tiempo_str)
		return
	if success:
		if data.has("formatted"):
			time_value.text = str(data.formatted)
		else:
			time_value.text = "--"
		_apply_estado(str(data.get("estado", "PENDIENTE")))
	else:
		time_value.text = "--"
		status_label.text = "Estado no disponible"
		status_hint.text = "No se pudo cargar la jornada"


# Actualiza chip de estado y habilitacion de botones segun la API
func _apply_estado(estado: String) -> void:
	match estado:
		ESTADO_TRABAJANDO:
			status_panel.add_theme_stylebox_override("panel", KarboUi.style_status_working())
			status_dot.color = KarboUi.COLOR_SUCCESS
			status_label.text = "Trabajando"
			status_label.add_theme_color_override("font_color", KarboUi.COLOR_SUCCESS)
			status_hint.text = "Estás en jornada. Ficha salida al terminar."
			_set_button_state(false, true)
		ESTADO_DESCANSANDO:
			status_panel.add_theme_stylebox_override("panel", KarboUi.style_status_rest())
			status_dot.color = KarboUi.COLOR_TEXT_SEC
			status_label.text = "En descanso"
			status_label.add_theme_color_override("font_color", KarboUi.COLOR_TEXT_SEC)
			status_hint.text = "Fuera de jornada. Ficha entrada para volver a trabajar."
			_set_button_state(true, false)
		_:
			status_panel.add_theme_stylebox_override("panel", KarboUi.style_status_pending())
			status_dot.color = KarboUi.COLOR_PRIMARY_LIGHT
			status_label.text = "Jornada no iniciada"
			status_label.add_theme_color_override("font_color", KarboUi.COLOR_PRIMARY_LIGHT)
			status_hint.text = "Aún no has fichado hoy. Pulsa entrada para empezar."
			_set_button_state(true, false)


func _set_button_state(entrada_on: bool, salida_on: bool) -> void:
	entrada_button.disabled = not entrada_on
	salida_button.disabled = not salida_on
	entrada_button.modulate.a = 1.0 if entrada_on else 0.45
	salida_button.modulate.a = 1.0 if salida_on else 0.45


func _registrar_fichaje(tipo: String) -> void:
	if not AppState.is_logged_in:
		fichaje_error.emit("Sesión no válida")
		return
	_pending_tipo = tipo
	_set_loading(true)
	status_error.text = "Registrando %s..." % tipo
	ApiService.fichar(AppState.empleado_id, AppState.cliente_id, tipo)


func _on_fichar_completed(success: bool, data: Dictionary, error_message: String) -> void:
	if not success:
		_set_loading(false)
		var msg := error_message if not error_message.is_empty() else "No se pudo registrar el fichaje"
		status_error.text = msg
		_cargar_tiempo_hoy()
		fichaje_error.emit(msg)
		return
	if data.get("success", false):
		ApiService.obtener_tiempo_hoy(AppState.empleado_id)
	else:
		_set_loading(false)
		status_error.text = "Respuesta inesperada del servidor"
		_cargar_tiempo_hoy()
		fichaje_error.emit("Respuesta inesperada del servidor")


func _set_loading(loading: bool) -> void:
	loading_panel.visible = loading
	if loading:
		entrada_button.disabled = true
		salida_button.disabled = true
		logout_button.disabled = true
	else:
		logout_button.disabled = false
