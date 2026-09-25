class_name KarboUi
extends RefCounted

## Paleta y estilos compartidos alineados con la app movil Karbo Fichaje.

const COLOR_BG := Color(0.058824, 0.090196, 0.164706)
const COLOR_BG_ALT := Color(0.007843, 0.023529, 0.090196)
const COLOR_SURFACE := Color(0.117647, 0.160784, 0.231373)
const COLOR_BORDER := Color(0.2, 0.254902, 0.333333)
const COLOR_PRIMARY := Color(0.388235, 0.4, 0.945098)
const COLOR_PRIMARY_LIGHT := Color(0.505882, 0.54902, 0.972549)
const COLOR_TEXT := Color(0.972549, 0.980392, 0.988235)
const COLOR_TEXT_SEC := Color(0.580392, 0.639216, 0.721569)
const COLOR_SUCCESS := Color(0.062745, 0.72549, 0.505882)
const COLOR_DANGER := Color(0.937255, 0.266667, 0.266667)
const COLOR_ERROR := Color(0.972549, 0.443137, 0.443137)


static func style_card() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = COLOR_SURFACE
	box.set_corner_radius_all(20)
	box.set_border_width_all(1)
	box.border_color = COLOR_BORDER
	box.content_margin_left = 24.0
	box.content_margin_top = 24.0
	box.content_margin_right = 24.0
	box.content_margin_bottom = 24.0
	return box


static func style_input() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0.094118, 0.129412, 0.196078)
	box.set_corner_radius_all(12)
	box.set_border_width_all(1)
	box.border_color = COLOR_BORDER
	box.content_margin_left = 16.0
	box.content_margin_top = 14.0
	box.content_margin_right = 16.0
	box.content_margin_bottom = 14.0
	return box


static func style_button_primary() -> StyleBoxFlat:
	return _button_box(COLOR_PRIMARY, COLOR_PRIMARY.lightened(0.08))


static func style_button_success() -> StyleBoxFlat:
	return _button_box(COLOR_SUCCESS, COLOR_SUCCESS.lightened(0.08))


static func style_button_danger() -> StyleBoxFlat:
	return _button_box(COLOR_DANGER, COLOR_DANGER.lightened(0.08))


static func style_button_ghost() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0, 0, 0, 0)
	box.set_corner_radius_all(12)
	box.set_border_width_all(1)
	box.border_color = COLOR_BORDER
	return box


static func style_status_working() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0.019608, 0.180392, 0.105882)
	box.set_corner_radius_all(100)
	box.set_border_width_all(1)
	box.border_color = COLOR_SUCCESS
	box.content_margin_left = 16.0
	box.content_margin_top = 10.0
	box.content_margin_right = 16.0
	box.content_margin_bottom = 10.0
	return box


static func style_status_rest() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = COLOR_SURFACE
	box.set_corner_radius_all(100)
	box.set_border_width_all(1)
	box.border_color = COLOR_BORDER
	box.content_margin_left = 16.0
	box.content_margin_top = 10.0
	box.content_margin_right = 16.0
	box.content_margin_bottom = 10.0
	return box


static func style_status_pending() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0.117647, 0.105882, 0.294118)
	box.set_corner_radius_all(100)
	box.set_border_width_all(1)
	box.border_color = COLOR_PRIMARY
	box.content_margin_left = 16.0
	box.content_margin_top = 10.0
	box.content_margin_right = 16.0
	box.content_margin_bottom = 10.0
	return box


static func style_time_chip() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0.192157, 0.180392, 0.505882)
	box.set_corner_radius_all(16)
	box.set_border_width_all(1)
	box.border_color = COLOR_PRIMARY
	box.content_margin_left = 28.0
	box.content_margin_top = 28.0
	box.content_margin_right = 28.0
	box.content_margin_bottom = 28.0
	return box


static func style_loading_overlay() -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = Color(0.02, 0.04, 0.09, 0.82)
	return box


static func _button_box(normal: Color, hover: Color) -> StyleBoxFlat:
	var box := StyleBoxFlat.new()
	box.bg_color = normal
	box.set_corner_radius_all(14)
	box.content_margin_top = 16.0
	box.content_margin_bottom = 16.0
	# Godot no tiene hover en StyleBoxFlat; el tema usa el mismo para normal/hover
	box.bg_color = normal
	return box


static func apply_label_title(label: Label, size: int = 36) -> void:
	label.add_theme_font_size_override("font_size", size)
	label.add_theme_color_override("font_color", COLOR_TEXT)


static func apply_label_subtitle(label: Label, size: int = 16) -> void:
	label.add_theme_font_size_override("font_size", size)
	label.add_theme_color_override("font_color", COLOR_TEXT_SEC)


static func apply_line_edit(edit: LineEdit) -> void:
	edit.add_theme_stylebox_override("normal", style_input())
	edit.add_theme_stylebox_override("focus", style_input())
	edit.add_theme_color_override("font_color", COLOR_TEXT)
	edit.add_theme_color_override("font_placeholder_color", COLOR_TEXT_SEC)


static func apply_primary_button(button: Button) -> void:
	var style := style_button_primary()
	button.add_theme_stylebox_override("normal", style)
	button.add_theme_stylebox_override("hover", style)
	button.add_theme_stylebox_override("pressed", style)
	button.add_theme_color_override("font_color", COLOR_TEXT)
	button.add_theme_color_override("font_disabled_color", COLOR_TEXT_SEC)


static func apply_success_button(button: Button) -> void:
	var style := style_button_success()
	button.add_theme_stylebox_override("normal", style)
	button.add_theme_stylebox_override("hover", style)
	button.add_theme_stylebox_override("pressed", style)
	button.add_theme_color_override("font_color", COLOR_TEXT)


static func apply_danger_button(button: Button) -> void:
	var style := style_button_danger()
	button.add_theme_stylebox_override("normal", style)
	button.add_theme_stylebox_override("hover", style)
	button.add_theme_stylebox_override("pressed", style)
	button.add_theme_color_override("font_color", COLOR_TEXT)


static func apply_ghost_button(button: Button) -> void:
	var style := style_button_ghost()
	button.add_theme_stylebox_override("normal", style)
	button.add_theme_stylebox_override("hover", style)
	button.add_theme_stylebox_override("pressed", style)
	button.add_theme_color_override("font_color", COLOR_TEXT_SEC)
