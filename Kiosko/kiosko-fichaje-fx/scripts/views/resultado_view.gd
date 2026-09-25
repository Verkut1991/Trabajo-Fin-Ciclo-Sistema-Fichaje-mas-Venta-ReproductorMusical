extends Control

signal continuar_pressed

@onready var background: ColorRect = %Background
@onready var card: PanelContainer = %Card
@onready var icon_label: Label = %IconLabel
@onready var titulo_label: Label = %TituloLabel
@onready var detalle_label: Label = %DetalleLabel
@onready var tiempo_panel: PanelContainer = %TiempoPanel
@onready var tiempo_label: Label = %TiempoLabel
@onready var continuar_button: Button = %ContinuarButton


func _ready() -> void:
	_apply_theme()
	continuar_button.pressed.connect(func(): continuar_pressed.emit())


func _apply_theme() -> void:
	card.add_theme_stylebox_override("panel", KarboUi.style_card())
	tiempo_panel.add_theme_stylebox_override("panel", KarboUi.style_time_chip())
	KarboUi.apply_primary_button(continuar_button)


func mostrar_exito(tipo: String, tiempo_formatted: String) -> void:
	background.color = Color(0.04, 0.12, 0.09)
	icon_label.text = "✓"
	icon_label.add_theme_color_override("font_color", KarboUi.COLOR_SUCCESS)
	titulo_label.text = "Fichaje registrado"
	var tipo_texto := "entrada" if tipo.to_lower() == "entrada" else "salida"
	detalle_label.text = "Se ha registrado correctamente tu fichaje de %s." % tipo_texto
	tiempo_label.text = tiempo_formatted
	tiempo_panel.visible = true


func mostrar_error(message: String) -> void:
	background.color = Color(0.14, 0.05, 0.05)
	icon_label.text = "!"
	icon_label.add_theme_color_override("font_color", KarboUi.COLOR_ERROR)
	titulo_label.text = "Error al fichar"
	detalle_label.text = message
	tiempo_panel.visible = false
