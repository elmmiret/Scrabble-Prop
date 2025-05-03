package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class MainView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private final Color COLOR_FONDO = new Color(36, 36, 36);
    private final Color COLOR_AZUL = new Color(15, 100, 150);
    private final Color COLOR_ROJO = new Color(150, 30, 20);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 20);

    GestorDeView gestorDeView;

    public MainView(GestorDeView gestorDeView) {
        super("Scrabble");
        this.gestorDeView = gestorDeView;
        init();
        setTema();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);

        try {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));
            panel.setBackground(COLOR_FONDO);

            // Logo scrabble
            Image image = ImageIO.read(new File("src/main/java/view/Scrabble-Logo-2000.png"));
            Image scaledImage = image.getScaledInstance(WIDTH-40, (HEIGHT/4)-20, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(imageLabel, BorderLayout.NORTH);

            // Panel de botones
            JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 25));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
            buttonPanel.setBackground(COLOR_FONDO);

            addButton(buttonPanel, "Gestión de perfil", COLOR_AZUL, () -> gestorDeView.mostrarGestionPerfil());
            addButton(buttonPanel, "Salir", COLOR_ROJO, this::salir);

            panel.add(buttonPanel, BorderLayout.CENTER);
            add(panel);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando la imagen: " + e.getMessage());
        }
    }

    private void addButton(JPanel panel, String text, Color color, Runnable action) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            private final int radioEsquina = 35; // Increased from 25 to 35 for more rounding
            private final BasicStroke grosorBorde = new BasicStroke(2f);
            private final Color colorBorde = color.brighter().brighter();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                // Fondo
                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                // Resaltar borde cuando hovereando
                if (isHovering) {
                    g2d.setStroke(grosorBorde);
                    g2d.setColor(colorBorde);
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);
                }

                // Mostrar texto
                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(this.getText(), g2d);
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(this.getText(), x, y);

                g2d.dispose();
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        isHovering = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        isHovering = false;
                        repaint();
                    }
                });
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT.deriveFont(22f)); // Slightly larger font
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // More vertical padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        button.setFocusPainted(false);
        button.setFocusable(false);

        panel.add(button);
    }

    private void setTema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", COLOR_FONDO);
            UIManager.put("Panel.background", COLOR_FONDO);
            UIManager.put("Button.background", COLOR_AZUL);
            UIManager.put("Button.foreground", Color.WHITE);
        } catch (Exception ignored) {}
    }

    private void salir() {
        System.exit(0);
    }
}