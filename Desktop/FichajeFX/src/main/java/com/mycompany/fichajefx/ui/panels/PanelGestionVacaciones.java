package com.mycompany.fichajefx.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelGestionVacaciones extends JPanel {
    public PanelGestionVacaciones() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel("Gestion vacaciones");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        header.setForeground(new Color(15, 23, 42));
        add(header, BorderLayout.NORTH);

        JLabel placeholder = new JLabel("Funcion Premium", SwingConstants.CENTER);
        placeholder.setFont(new Font("SansSerif", Font.BOLD, 22));
        placeholder.setForeground(new Color(100, 116, 139));
        add(placeholder, BorderLayout.CENTER);
    }
}
