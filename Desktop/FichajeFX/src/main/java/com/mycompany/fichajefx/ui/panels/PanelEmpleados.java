package com.mycompany.fichajefx.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mycompany.fichajefx.models.Empleado;
import com.mycompany.fichajefx.network.ApiService;
import com.mycompany.fichajefx.ui.MainFrame;
import java.util.List;
import java.util.ArrayList;

public class PanelEmpleados extends JPanel {
    private JTable tablaEmpleados;
    private DefaultTableModel tableModel;
    private List<Empleado> listaEmpleados = new ArrayList<>();

    public PanelEmpleados() {
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(15, 23, 42));

        JLabel header = new JLabel("Gestión de Personal");
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        headerPanel.add(header, BorderLayout.WEST);

        JLabel hintPin = new JLabel("PIN kiosko: se genera en el servidor y solo se muestra al crearlo");
        hintPin.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hintPin.setForeground(new Color(148, 163, 184));
        headerPanel.add(hintPin, BorderLayout.SOUTH);

        JButton btnAdd = new JButton("+ Nuevo Empleado");
        btnAdd.setBackground(new Color(16, 185, 129));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAdd.addActionListener(e -> showEmpleadoDialog(null));
        headerPanel.add(btnAdd, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Nombre", "Email", "NIF", "Rol", "PIN kiosko" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEmpleados = new JTable(tableModel);
        tablaEmpleados.setRowHeight(40);
        tablaEmpleados.setBackground(new Color(30, 41, 59));
        tablaEmpleados.setForeground(Color.WHITE);
        tablaEmpleados.setGridColor(new Color(51, 65, 85));
        tablaEmpleados.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablaEmpleados.setSelectionBackground(new Color(51, 65, 85));
        tablaEmpleados.setSelectionForeground(Color.WHITE);

        tablaEmpleados.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tablaEmpleados.getTableHeader().setBackground(new Color(51, 65, 85));
        tablaEmpleados.getTableHeader().setForeground(Color.WHITE);
        tablaEmpleados.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(71, 85, 105)));

        JScrollPane scrollPane = new JScrollPane(tablaEmpleados);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(new Color(15, 23, 42));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton btnGenerarPin = new JButton("Generar PIN");
        styleButton(btnGenerarPin, new Color(245, 158, 11));
        btnGenerarPin.addActionListener(e -> generarPinSeleccionado(false));

        JButton btnPinManual = new JButton("PIN manual");
        styleButton(btnPinManual, new Color(14, 165, 233));
        btnPinManual.addActionListener(e -> generarPinSeleccionado(true));

        JButton btnQuitarPin = new JButton("Quitar PIN");
        styleButton(btnQuitarPin, new Color(100, 116, 139));
        btnQuitarPin.addActionListener(e -> quitarPinSeleccionado());

        JButton btnEdit = new JButton("Editar");
        styleButton(btnEdit, new Color(99, 102, 241));
        btnEdit.addActionListener(e -> {
            int row = tablaEmpleados.getSelectedRow();
            if (row >= 0) {
                showEmpleadoDialog(listaEmpleados.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un empleado");
            }
        });

        JButton btnDelete = new JButton("Eliminar");
        styleButton(btnDelete, new Color(239, 68, 68));
        btnDelete.addActionListener(e -> {
            int row = tablaEmpleados.getSelectedRow();
            if (row >= 0) {
                confirmarEliminacion(listaEmpleados.get(row));
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un empleado");
            }
        });

        actionsPanel.add(btnGenerarPin);
        actionsPanel.add(btnPinManual);
        actionsPanel.add(btnQuitarPin);
        actionsPanel.add(btnEdit);
        actionsPanel.add(btnDelete);
        add(actionsPanel, BorderLayout.SOUTH);

        loadEmpleados();
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    private String textoEstadoPin(Empleado emp) {
        return emp.isPinEnabled() ? "Activo" : "Sin PIN";
    }

    private void loadEmpleados() {
        new Thread(() -> {
            try {
                listaEmpleados = ApiService.getEmpleados(MainFrame.currentClientId);
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Empleado emp : listaEmpleados) {
                        tableModel.addRow(new Object[] {
                                emp.getId(),
                                emp.getNombre(),
                                emp.getEmail(),
                                emp.getNif(),
                                emp.getRol(),
                                textoEstadoPin(emp)
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "No se pudo cargar empleados: " + e.getMessage()));
            }
        }).start();
    }

    private Empleado empleadoSeleccionado() {
        int row = tablaEmpleados.getSelectedRow();
        if (row < 0 || row >= listaEmpleados.size()) {
            return null;
        }
        return listaEmpleados.get(row);
    }

    private void generarPinSeleccionado(boolean manual) {
        Empleado emp = empleadoSeleccionado();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un empleado");
            return;
        }

        String pinManual = null;
        if (manual) {
            pinManual = JOptionPane.showInputDialog(this,
                    "Introduce un PIN de 4 caracteres (letras o números) para " + emp.getNombre() + ":",
                    "PIN manual",
                    JOptionPane.QUESTION_MESSAGE);
            if (pinManual == null) {
                return;
            }
            pinManual = pinManual.trim();
            if (!pinManual.matches("^[A-Za-z0-9]{4}$")) {
                JOptionPane.showMessageDialog(this, "El PIN debe tener exactamente 4 caracteres alfanuméricos");
                return;
            }
        } else if (emp.isPinEnabled()) {
            int opt = JOptionPane.showConfirmDialog(this,
                    emp.getNombre() + " ya tiene PIN activo. ¿Generar uno nuevo? (el anterior dejará de funcionar)",
                    "Reemplazar PIN",
                    JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION) {
                return;
            }
        }

        final String pinParaApi = pinManual;
        new Thread(() -> {
            try {
                String pin = ApiService.generarPinEmpleado(emp.getId(), pinParaApi);
                SwingUtilities.invokeLater(() -> {
                    mostrarPinGenerado(emp.getNombre(), pin);
                    loadEmpleados();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
            }
        }).start();
    }

    private void mostrarPinGenerado(String nombre, String pin) {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(new Color(30, 41, 59));
        JLabel titulo = new JLabel("PIN de kiosko para " + nombre);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel pinLabel = new JLabel(pin, SwingConstants.CENTER);
        pinLabel.setForeground(new Color(52, 211, 153));
        pinLabel.setFont(new Font("Monospaced", Font.BOLD, 42));
        pinLabel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        JLabel aviso = new JLabel("<html>Guárdalo ahora. Por seguridad no se puede volver a consultar.</html>");
        aviso.setForeground(new Color(148, 163, 184));
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(pinLabel, BorderLayout.CENTER);
        panel.add(aviso, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, panel, "PIN generado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void quitarPinSeleccionado() {
        Empleado emp = empleadoSeleccionado();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un empleado");
            return;
        }
        if (!emp.isPinEnabled()) {
            JOptionPane.showMessageDialog(this, "Este empleado no tiene PIN activo");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "¿Quitar el PIN de kiosko de " + emp.getNombre() + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) {
            return;
        }
        new Thread(() -> {
            try {
                ApiService.quitarPinEmpleado(emp.getId());
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "PIN desactivado");
                    loadEmpleados();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
            }
        }).start();
    }

    private void showEmpleadoDialog(Empleado empExistente) {
        boolean isEdit = empExistente != null;

        JTextField nameField = new JTextField(isEdit ? empExistente.getNombre() : "");
        JTextField emailField = new JTextField(isEdit ? empExistente.getEmail() : "");
        JTextField nifField = new JTextField(isEdit ? empExistente.getNif() : "");
        String[] roles = { "EMPLEADO", "ADMIN" };
        JComboBox<String> roleBox = new JComboBox<>(roles);
        if (isEdit)
            roleBox.setSelectedItem(empExistente.getRol());
        JPasswordField passField = new JPasswordField();

        Object[] message = {
                "Nombre:", nameField,
                "Email:", emailField,
                "NIF:", nifField,
                "Contraseña " + (isEdit ? "(vacío para mantener):" : ":"), passField,
                "Rol:", roleBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, isEdit ? "Editar Empleado" : "Añadir Empleado",
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Empleado emp = isEdit ? empExistente : new Empleado();
            emp.setNombre(nameField.getText());
            emp.setEmail(emailField.getText());
            emp.setNif(nifField.getText());
            emp.setRol((String) roleBox.getSelectedItem());

            new Thread(() -> {
                try {
                    String password = new String(passField.getPassword());
                    if (isEdit) {
                        ApiService.updateEmpleado(emp.getId(), emp, password);
                    } else {
                        ApiService.registrarEmpleado(MainFrame.currentClientId, emp, password);
                    }
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, isEdit ? "Empleado actualizado" : "Empleado registrado");
                        loadEmpleados();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void confirmarEliminacion(Empleado emp) {
        int opt = JOptionPane.showConfirmDialog(this, "¿Eliminar a " + emp.getNombre() + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            new Thread(() -> {
                try {
                    ApiService.deleteEmpleado(emp.getId());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Empleado eliminado");
                        loadEmpleados();
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
                }
            }).start();
        }
    }
}
