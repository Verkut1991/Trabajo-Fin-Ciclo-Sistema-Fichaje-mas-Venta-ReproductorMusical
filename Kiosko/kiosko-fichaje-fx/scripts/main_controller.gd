extends Control

@onready var login_view: Control = %LoginView
@onready var fichaje_view: Control = %FichajeView
@onready var resultado_view: Control = %ResultadoView


func _ready() -> void:
	login_view.login_success.connect(_show_fichaje)
	fichaje_view.logout_requested.connect(_show_login)
	fichaje_view.fichaje_ok.connect(_on_fichaje_ok)
	fichaje_view.fichaje_error.connect(_on_fichaje_error)
	resultado_view.continuar_pressed.connect(_on_resultado_continuar)
	_show_login()


func _show_login() -> void:
	AppState.clear_employee_session()
	login_view.visible = true
	fichaje_view.visible = false
	resultado_view.visible = false
	if login_view.has_method("refresh"):
		login_view.refresh()


func _show_fichaje() -> void:
	login_view.visible = false
	fichaje_view.visible = true
	resultado_view.visible = false
	fichaje_view.refresh()


func _on_resultado_continuar() -> void:
	AppState.clear_employee_session()
	_show_login()


func _on_fichaje_ok(tipo: String, tiempo_formatted: String) -> void:
	login_view.visible = false
	fichaje_view.visible = false
	resultado_view.visible = true
	resultado_view.mostrar_exito(tipo, tiempo_formatted)


func _on_fichaje_error(message: String) -> void:
	login_view.visible = false
	fichaje_view.visible = false
	resultado_view.visible = true
	resultado_view.mostrar_error(message)
