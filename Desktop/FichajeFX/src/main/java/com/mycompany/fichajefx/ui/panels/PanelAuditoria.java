package com.mycompany.fichajefx.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import com.mycompany.fichajefx.models.Fichaje;
import com.mycompany.fichajefx.services.PdfService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanelAuditoria extends JPanel {
    private JTable tablaFichajes;
    private DefaultTableModel tableModel;
    private List<Fichaje> auditData;
    private JLabel lblStatus;

    public PanelAuditoria() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Registros y Auditoría");
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(header, BorderLayout.WEST);

        JButton btnPdf = new JButton("Exportar a PDF");
        btnPdf.setBackground(new Color(79, 70, 229));
        btnPdf.setForeground(Color.WHITE);
        btnPdf.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPdf.addActionListener(e -> exportToPdf());

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(lblStatus);
        rightPanel.add(btnPdf);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "DNI", "Empleado", "Tipo", "Fecha/Hora", "Ubicación" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFichajes = new JTable(tableModel);
        tablaFichajes.setRowHeight(35);
        tablaFichajes.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tablaFichajes.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tablaFichajes.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaFichajes.setSelectionBackground(new Color(224, 231, 255));
        tablaFichajes.setSelectionForeground(Color.BLACK);
        tablaFichajes.setGridColor(new Color(203, 213, 225));

        add(new JScrollPane(tablaFichajes), BorderLayout.CENTER);

        loadLogs();
    }

    private void loadLogs() {
        new Thread(() -> {
            try {
                auditData = com.mycompany.fichajefx.network.ApiService
                        .getAuditoria(com.mycompany.fichajefx.ui.MainFrame.currentClientId);
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Fichaje f : auditData) {
                        tableModel.addRow(new Object[] {
                                f.getDni(),
                                f.getNombre_empleado(),
                                f.getTipo(),
                                f.getTimestamp(),
                                f.getUbicacion()
                        });
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("Error al cargar datos");
                    lblStatus.setForeground(Color.RED);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void exportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf"))
                path += ".pdf";

            final String finalPath = path;
            new Thread(() -> {
                try {
                    PdfService.generateAuditReport(finalPath, auditData);
                    SwingUtilities.invokeLater(
                            () -> JOptionPane.showMessageDialog(this, "Reporte generado en: " + finalPath));
                } catch (Exception e) {
                    SwingUtilities.invokeLater(
                            () -> JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage()));
                }
            }).start();
        }
    }
}
