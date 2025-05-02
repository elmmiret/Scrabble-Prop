package view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    GestorDeView gestorDeView;

    public MainView(GestorDeView gestorDeView) {
        super("Scrabble");
        this.gestorDeView = gestorDeView;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addButton(panel, "Gestión de perfil", () -> gestorDeView.mostrarGestionPerfil());

        add(panel);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        button.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(button);
    }
}