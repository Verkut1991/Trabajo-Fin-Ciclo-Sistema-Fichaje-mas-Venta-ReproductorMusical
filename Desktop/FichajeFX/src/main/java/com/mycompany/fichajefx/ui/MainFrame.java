package com.mycompany.fichajefx.ui;

import javax.swing.*;
import java.awt.*;
import com.mycompany.fichajefx.ui.panels.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sesión Estática Activa
    public static String currentClientId = "";
    public static String currentPlan = "Basic";
    public static String currentCompanyName = "";

    public MainFrame() {
        setTitle("Kompot Player Fichaje - Administración del Negocio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content Area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add Panels
        contentPanel.add(new PanelBranding(), "BRANDING");
        contentPanel.add(new PanelEmpleados(), "EMPLEADOS");
        contentPanel.add(new PanelAuditoria(), "AUDITORIA");
        contentPanel.add(new PanelOlvidos(), "OLVIDOS");
        contentPanel.add(new PanelGestionVacaciones(), "GESTION_VACACIONES");

        add(contentPanel, BorderLayout.CENTER);

        // Show initial panel
        cardLayout.show(contentPanel, "BRANDING");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(15, 23, 42)); // Mismo que bg-dark de la web, más oscuro
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85))); // Borde derecho

        JLabel title = new JLabel("Kompot Player Fichaje");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createNavButton("Suite Branding", "BRANDING"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Gestionar Personal", "EMPLEADOS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Auditoría y PDF", "AUDITORIA"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Olvidos", "OLVIDOS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createNavButton("Gestión vacaciones", "GESTION_VACACIONES"));

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(30, 41, 59)); // Más contraste sobre el fondo oscuro
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14)); // Más negrita
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        return btn;
    }
}
