extends Control

signal login_success
signal login_failed(message: String)

const PIN_REGEX := "^[A-Za-z0-9]{4}$"

@onready var card: PanelContainer = %Card
@onready var title_label: Label = %Title
@onready var subtitle_label: Label = %Subtitle
@onready var empresa_label: Label = %EmpresaLabel
@onready var setup_stack: VBoxContainer = %SetupStack
@onready var pin_stack: VBoxContainer = %PinStack
@onready var email_input: LineEdit = %EmailInput
@onready var password_input: LineEdit = %PasswordInput
@onready var setup_button: Button = %SetupButton
@onready var pin_input: LineEdit = %PinInput
@onready var pin_button: Button = %PinButton
@onready var reset_setup_button: Button = %ResetSetupButton
@onready var status_label: Label = %StatusLabel
@onready var loading_panel: Panel = %LoadingPanel


func _ready() -> void:
	_apply_theme()
	setup_button.pressed.connect(_on_setup_pressed)
	pin_button.pressed.connect(_on_pin_pressed)
	reset_setup_button.pressed.connect(_on_reset_setup_pressed)
	pin_input.text_submitted.connect(func(_t): _on_pin_pressed())
	ApiService.tenant_login_completed.connect(_on_tenant_login_completed)
	ApiService.login_completed.connect(_on_pin_login_completed)
	loading_panel.visible = false
	_apply_mode()


func refresh() -> void:
	_apply_mode()
	status_label.text = ""
	pin_input.text = ""


func _apply_theme() -> void:
	card.add_theme_stylebox_override("panel", KarboUi.style_card())
	KarboUi.apply_line_edit(email_input)
	KarboUi.apply_line_edit(password_input)
	KarboUi.apply_line_edit(pin_input)
	KarboUi.apply_primary_button(setup_button)
	KarboUi.apply_primary_button(pin_button)
	KarboUi.apply_ghost_button(reset_setup_button)
	loading_panel.add_theme_stylebox_override("panel", KarboUi.style_loading_overlay())


# Muestra setup de tenant o pantalla de PIN segun el estado guardado
func _apply_mode() -> void:
	var setup_done := AppState.is_setup_complete()
	setup_stack.visible = not setup_done
	pin_stack.visible = setup_done
	empresa_label.visible = setup_done
	if setup_done:
		title_label.text = "Fichaje"
		subtitle_label.text = "Introduce tu PIN para identificarte"
		empresa_label.text = AppState.nombre_empresa if not AppState.nombre_empresa.is_empty() else "Empresa vinculada"
	else:
		title_label.text = "Configuración"
		subtitle_label.text = "Vincula este kiosko con tu empresa"
		empresa_label.text = ""


func _on_setup_pressed() -> void:
	var email := email_input.text.strip_edges()
	var password := password_input.text
	if email.is_empty() or password.is_empty():
		status_label.text = "Introduce email y contraseña de administrador"
		return
	_set_loading(true)
	status_label.text = "Vinculando kiosko..."
	ApiService.login_tenant_jwt(email, password)


func _on_pin_pressed() -> void:
	var pin := pin_input.text.strip_edges()
	if not _is_valid_pin(pin):
		status_label.text = "El PIN debe tener 4 caracteres alfanuméricos"
		return
	if not AppState.is_setup_complete():
		status_label.text = "Primero configura el kiosko"
		_apply_mode()
		return
	_set_loading(true)
	status_label.text = "Verificando PIN..."
	ApiService.login_empleado_pin(pin)


func _on_reset_setup_pressed() -> void:
	AppState.clear_tenant_session()
	email_input.text = ""
	password_input.text = ""
	pin_input.text = ""
	status_label.text = ""
	_apply_mode()


func _on_tenant_login_completed(success: bool, cliente: Dictionary, tokens: Dictionary, error_message: String) -> void:
	_set_loading(false)
	if success and not cliente.is_empty():
		AppState.set_tenant_session(cliente, tokens)
		status_label.text = ""
		password_input.text = ""
		_apply_mode()
	else:
		var msg := error_message if not error_message.is_empty() else "No se pudo vincular el kiosko"
		status_label.text = msg


func _on_pin_login_completed(success: bool, empleado: Dictionary, error_message: String) -> void:
	_set_loading(false)
	if success and not empleado.is_empty():
		AppState.set_session(empleado)
		status_label.text = ""
		pin_input.text = ""
		login_success.emit()
	else:
		var msg := error_message if not error_message.is_empty() else "PIN incorrecto"
		status_label.text = msg
		login_failed.emit(msg)


# Valida formato de PIN de 4 caracteres alfanumericos
func _is_valid_pin(pin: String) -> bool:
	var regex := RegEx.new()
	regex.compile(PIN_REGEX)
	return regex.search(pin) != null


func _set_loading(loading: bool) -> void:
	loading_panel.visible = loading
	setup_button.disabled = loading
	pin_button.disabled = loading
	email_input.editable = not loading
	password_input.editable = not loading
	pin_input.editable = not loading
