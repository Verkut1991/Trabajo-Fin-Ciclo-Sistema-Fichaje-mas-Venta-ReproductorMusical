package com.mycompany.fichajefx.ui.panels;

import com.mycompany.fichajefx.models.BrandingConfig;
import com.mycompany.fichajefx.network.ApiService;
import com.mycompany.fichajefx.ui.MainFrame;
import java.awt.*;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.*;

public class PanelBranding extends JPanel {
    private JTextField txtNombreSuite;
    private JTextField txtColorPrimary;
    private JTextField txtLogoUrl;
    private JComboBox<String> comboPaletas;
    private JButton btnSave;
    private JLabel lblStatus;
    private JPanel overlay;

    public PanelBranding() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel("Personalización de la Suite");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setForeground(new Color(15, 23, 42));
        add(header, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // Fields
        gbc.gridy = 0;
        JLabel lbl1 = new JLabel("Nombre de la Suite:");
        lbl1.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl1.setForeground(new Color(51, 65, 85));
        form.add(lbl1, gbc);
        gbc.gridy = 1;
        txtNombreSuite = new JTextField();
        txtNombreSuite.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNombreSuite.setBorder(createFieldBorder());
        form.add(txtNombreSuite, gbc);

        gbc.gridy = 2;
        JLabel lblPaleta = new JLabel("Paletas de Colores Predefinidas:");
        lblPaleta.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblPaleta.setForeground(new Color(51, 65, 85));
        form.add(lblPaleta, gbc);
        gbc.gridy = 3;
        String[] paletas = { "Manual", "Ocean Blue (#3b82f6)", "Sunset Orange (#f97316)", "Midnight (#1e293b)",
                "Emerald (#10b981)" };
        comboPaletas = new JComboBox<>(paletas);
        comboPaletas.addActionListener(e -> applyPalette());
        form.add(comboPaletas, gbc);

        gbc.gridy = 4;
        JLabel lbl2 = new JLabel("Color Principal (HEX):");
        lbl2.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl2.setForeground(new Color(51, 65, 85));
        form.add(lbl2, gbc);
        gbc.gridy = 5;
        txtColorPrimary = new JTextField();
        txtColorPrimary.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtColorPrimary.setBorder(createFieldBorder());
        form.add(txtColorPrimary, gbc);

        gbc.gridy = 6;
        JLabel lbl3 = new JLabel("Logo URL:");
        lbl3.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl3.setForeground(new Color(51, 65, 85));
        form.add(lbl3, gbc);
        gbc.gridy = 7;
        txtLogoUrl = new JTextField();
        txtLogoUrl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtLogoUrl.setBorder(createFieldBorder());
        form.add(txtLogoUrl, gbc);

        // Button Group
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(Color.WHITE);
        btnSave = new JButton("Guardar Cambios");
        btnSave.setBackground(new Color(79, 70, 229));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btnSave.addActionListener(e -> saveConfig());
        btnPanel.add(btnSave);

        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(16, 185, 129));
        btnPanel.add(lblStatus);

        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 0, 0);
        form.add(btnPanel, gbc);

        mainContent.add(form, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Overlay for Paywall
        overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(255, 255, 255, 200));
        JLabel paywallMsg = new JLabel(
                "<html><center><h2>Función Premium</h2>Esta característica requiere el plan de <b>10€/mes</b>.<br>Contacta con administración para activar.</center></html>");
        paywallMsg.setHorizontalAlignment(SwingConstants.CENTER);
        overlay.add(paywallMsg);
        overlay.setVisible(false);

        loadInitialData();
    }

    private javax.swing.border.Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    private void applyPalette() {
        String selected = (String) comboPaletas.getSelectedItem();
        if (selected.contains("#3b82f6"))
            txtColorPrimary.setText("#3b82f6");
        else if (selected.contains("#f97316"))
            txtColorPrimary.setText("#f97316");
        else if (selected.contains("#1e293b"))
            txtColorPrimary.setText("#1e293b");
        else if (selected.contains("#10b981"))
            txtColorPrimary.setText("#10b981");
    }

    private void loadInitialData() {
        new Thread(() -> {
            boolean isEnterprise = "Enterprise".equalsIgnoreCase(MainFrame.currentPlan);
            try {
                BrandingConfig config = ApiService.getBranding(MainFrame.currentClientId);
                SwingUtilities.invokeLater(() -> {
                    if (config != null) {
                        txtNombreSuite.setText(config.getNombre_suite());
                        if (config.getColores() != null) {
                            txtColorPrimary.setText(config.getColores().get("primary"));
                        }
                        txtLogoUrl.setText(config.getLogo_url());
                    }

                    if (!isEnterprise) {
                        lockFeatures();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void lockFeatures() {
        txtNombreSuite.setEnabled(false);
        txtColorPrimary.setEnabled(false);
        txtLogoUrl.setEnabled(false);
        comboPaletas.setEnabled(false);
        btnSave.setEnabled(false);

        // Agregar el overlay de paywall al frente
        JLayeredPane layeredPane = getRootPane().getLayeredPane();
        // Nota: En una implementación real, usaríamos un JLayer o simplemente
        // deshabilitar los componentes es suficiente,
        // pero para efecto visual de "bloqueo" usaremos el overlay.
        setComponentPopupMenu(null);
        lblStatus.setText("⚠ Versión no Premium - Funciones bloqueadas");
        lblStatus.setForeground(Color.RED);
    }

    private void saveConfig() {
        BrandingConfig config = new BrandingConfig();
        config.setCliente_id(MainFrame.currentClientId);
        config.setNombre_suite(txtNombreSuite.getText());
        config.setLogo_url(txtLogoUrl.getText());

        var colores = new HashMap<String, String>();
        colores.put("primary", txtColorPrimary.getText());
        config.setColores(colores);

        new Thread(() -> {
            try {
                ApiService.updateBranding(MainFrame.currentClientId, config);
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("✓ Configuración actualizada con éxito");
                    Timer timer = new Timer(3000, ae -> lblStatus.setText(" "));
                    timer.setRepeats(false);
                    timer.start();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()));
            }
        }).start();
    }
}
