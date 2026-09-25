package com.mycompany.fichajefx.ui;

import javax.swing.*;
import java.awt.*;
import com.mycompany.fichajefx.network.ApiService;
import com.fasterxml.jackson.databind.JsonNode;

public class LoginFrame extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    public LoginFrame() {
        setTitle("FichajeFX - Acceso Empresa");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(15, 23, 42)); // Dark bg
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        JLabel title = new JLabel("Acceso Administración");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(title, gbc);

        gbc.gridy = 1;
        JLabel lblEmail = new JLabel("Email de Empresa:");
        lblEmail.setForeground(new Color(148, 163, 184));
        mainPanel.add(lblEmail, gbc);
        gbc.gridy = 2;
        txtEmail = new JTextField();
        txtEmail.setBackground(new Color(30, 41, 59));
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setCaretColor(Color.WHITE);
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        mainPanel.add(txtEmail, gbc);

        gbc.gridy = 3;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(new Color(148, 163, 184));
        mainPanel.add(lblPass, gbc);
        gbc.gridy = 4;
        txtPassword = new JPasswordField();
        txtPassword.setBackground(new Color(30, 41, 59));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        mainPanel.add(txtPassword, gbc);

        gbc.gridy = 5;
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBackground(new Color(99, 102, 241)); // Indigo accent
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnLogin.addActionListener(e -> performLogin());
        mainPanel.add(btnLogin, gbc);

        gbc.gridy = 6;
        lblError = new JLabel(" ");
        lblError.setForeground(new Color(248, 113, 113)); // Soft red
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblError, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String email = txtEmail.getText();
        String pass = new String(txtPassword.getPassword());

        btnLogin.setEnabled(false);
        lblError.setText("Conectando...");

        new Thread(() -> {
            try {
                JsonNode res = ApiService.loginCliente(email, pass);
                if (res != null && res.get("success").asBoolean()) {
                    JsonNode cliente = res.get("cliente");
                    String id = cliente.get("id").asText();
                    String plan = cliente.get("plan_contratado").asText();
                    String nombre = cliente.get("nombre_empresa").asText();

                    // Guardar sesión estática simple
                    MainFrame.currentClientId = id;
                    MainFrame.currentPlan = plan;
                    MainFrame.currentCompanyName = nombre;

                    SwingUtilities.invokeLater(() -> {
                        new MainFrame().setVisible(true);
                        this.dispose();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        lblError.setText("Credenciales incorrectas");
                        btnLogin.setEnabled(true);
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    lblError.setText("Error de servidor");
                    btnLogin.setEnabled(true);
                });
            }
        }).start();
    }
}
