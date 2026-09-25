package com.mycompany.fichajefx;

import javax.swing.*;
import java.awt.*;
import com.mycompany.fichajefx.ui.MainFrame;

public class FichajeFX {
    public static void main(String[] args) {
        // Establecer Look and Feel del sistema para mejor apariencia
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new com.mycompany.fichajefx.ui.LoginFrame().setVisible(true);
        });
    }
}
