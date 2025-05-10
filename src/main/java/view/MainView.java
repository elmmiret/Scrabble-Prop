package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;

public class MainView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private final Color COLOR_FONDO = new Color(238, 238, 238);
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 24);

    GestorDeView gestorDeView;

    public MainView(GestorDeView gestorDeView) {
        super("Scrabble");
        this.gestorDeView = gestorDeView;
        setTema();
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Añadir tablero con animacion
        TableroMoviendo tableroMoviendo = new TableroMoviendo();
        tableroMoviendo.setBounds(0, 0, WIDTH, HEIGHT);
        layeredPane.add(tableroMoviendo, JLayeredPane.DEFAULT_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 40, 50));

        try {
            Image image = ImageIO.read(new File("src/main/java/view/Scrabble-Logo-2022.png"));
            int availableWidth = WIDTH - 100;
            Image scaledImage = image.getScaledInstance(availableWidth, -1, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imageLabel.setOpaque(false);  // Make transparent
            panel.add(imageLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 25));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            buttonPanel.setOpaque(false);  // Make transparent

            addMainButton(buttonPanel, "Gestión de perfil", COLOR_AZUL, () -> gestorDeView.mostrarGestionPerfil());
            addMainButton(buttonPanel, "Gestión de partida", COLOR_AZUL, () -> gestorDeView.mostrarGestionPartida());
            addMainButton(buttonPanel, "Ver ranking", COLOR_AZUL, () -> gestorDeView.mostrarRanking());
            addMainButton(buttonPanel, "Salir", COLOR_ROJO, this::salir);

            panel.add(buttonPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        panel.setBounds(0, 0, WIDTH, HEIGHT);
        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
        tableroMoviendo.iniciarMovimiento();
    }

    private void addMainButton(JPanel panel, String text, Color color, Runnable action) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            private final int radioEsquina = 35;
            private final BasicStroke grosorBorde = new BasicStroke(3f);
            private final Color colorBordeNormal = color.brighter();
            private final Color colorBordeHover = color.darker().darker();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                g2d.setStroke(grosorBorde);
                g2d.setColor(isHovering ? colorBordeHover : colorBordeNormal);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);

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
        button.setFont(BUTTON_FONT);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        button.setFocusPainted(false);
        button.setFocusable(false);

        panel.add(button);
    }

    private void setTema() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            UIManager.put("nimbusBase", COLOR_AZUL); // Base color for components
            UIManager.put("control", COLOR_FONDO); // General background
            UIManager.put("text", Color.BLACK); // Text color
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salir() {
        System.exit(0);
    }
}