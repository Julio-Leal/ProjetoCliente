package br.com.cliente;

import javax.swing.SwingUtilities;

public class ClienteMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }
}
