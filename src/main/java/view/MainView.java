package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MainView extends JFrame {
    private static final int WIDTH = 300;
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
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        try {
            // Load image (scaled to appropriate size)
            Image image = ImageIO.read(new File("src/main/java/view/Scrabble-Logo-2000.png"));
            Image scaledImage = image.getScaledInstance(WIDTH, HEIGHT/4, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(imageLabel);
            add(Box.createVerticalStrut(1)); // Space between image and buttons

            // Button panel setup
            JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5)); // Reduced gaps between buttons
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Smaller padding

            addButton(buttonPanel, "Gestión de perfil", () -> gestorDeView.mostrarGestionPerfil());
            buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add button panel to CENTER
            add(buttonPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        button.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(button);
    }
}