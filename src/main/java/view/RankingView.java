package view;

import gestordeperfil.Perfil;
import ranking.Ranking;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;

public class RankingView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private final Color COLOR_FONDO = new Color(36, 36, 36);
    private final Color COLOR_AZUL = new Color(15, 100, 150);
    private final Color COLOR_ROJO = new Color(150, 30, 20);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font RANKING_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final GestorDeView gestorDeView;
    private final Ranking ranking;
    private JPanel contentPanel;

    public RankingView(GestorDeView gestorDeView, Ranking ranking) {
        super("Ranking");
        this.gestorDeView = gestorDeView;
        this.ranking = ranking;
        init();
        setTheme();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(COLOR_FONDO);

        JLabel titleLabel = new JLabel("Ranking");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Ranking selection buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(65, 20, 65, 20));
        buttonPanel.setBackground(COLOR_FONDO);

        addRankingButton(buttonPanel, "Por Puntos", COLOR_AZUL, () -> mostrarRanking("puntos"));
        addRankingButton(buttonPanel, "Por Partidas Jugadas", COLOR_AZUL, () -> mostrarRanking("partidasJugadas"));
        addRankingButton(buttonPanel, "Por Victorias", COLOR_AZUL, () -> mostrarRanking("victorias"));
        addRankingButton(buttonPanel, "Por Derrotas", COLOR_AZUL, () -> mostrarRanking("derrotas"));
        addRankingButton(buttonPanel, "Atrás", COLOR_ROJO, gestorDeView::mostrarMain);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_FONDO);

        add(mainPanel);
    }

    private void addRankingButton(JPanel panel, String text, Color color, Runnable action) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            private final int radioEsquina = 35;
            private final BasicStroke grosorBorde = new BasicStroke(2f);
            private final Color colorBorde = color.brighter().brighter();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                if (isHovering) {
                    g2d.setStroke(grosorBorde);
                    g2d.setColor(colorBorde);
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);
                }

                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(this.getText(), g2d);
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(this.getText(), x, y);

                g2d.dispose();
            }

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        isHovering = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent evt) {
                        isHovering = false;
                        repaint();
                    }
                });
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        button.setFocusPainted(false);
        button.setFocusable(false);

        panel.add(button);
    }

    private void mostrarRanking(String tipo) {
        JDialog dialog = new JDialog(this, "Ranking", true);
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(COLOR_FONDO);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(COLOR_FONDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0); // Reduced vertical spacing

        // Add title
        JLabel title = new JLabel("RANKING " + tipo.toUpperCase());
        title.setFont(BUTTON_FONT);
        title.setForeground(Color.WHITE);
        content.add(title, gbc);

        // Add entries
        TreeSet<Perfil> rkg = getRankingSet(tipo);
        int position = 1;

        for (Perfil perfil : rkg) {
            JPanel entryPanel = new JPanel(new BorderLayout(10, 0));
            entryPanel.setBackground(COLOR_FONDO);
            entryPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            leftPanel.setBackground(COLOR_FONDO);

            JLabel posLabel = new JLabel(position++ + ".");
            posLabel.setFont(RANKING_FONT);
            posLabel.setForeground(Color.WHITE);

            JLabel nameLabel = new JLabel(perfil.getUsername());
            nameLabel.setFont(RANKING_FONT);
            nameLabel.setForeground(Color.WHITE);

            leftPanel.add(posLabel);
            leftPanel.add(nameLabel);

            JLabel statsLabel = new JLabel(getStatsText(tipo, perfil));
            statsLabel.setFont(RANKING_FONT);
            statsLabel.setForeground(new Color(200, 200, 200));

            entryPanel.add(leftPanel, BorderLayout.WEST);
            entryPanel.add(statsLabel, BorderLayout.EAST);
            content.add(entryPanel, gbc);
        }

        if (rkg.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay datos disponibles");
            emptyLabel.setFont(RANKING_FONT);
            emptyLabel.setForeground(Color.WHITE);
            content.add(emptyLabel, gbc);
        }

        // añadir padding
        gbc.weighty = 1;
        content.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_FONDO);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private TreeSet<Perfil> getRankingSet(String tipo) {
        switch (tipo) {
            case "puntos": return ranking.getRankingPuntos();
            case "partidasJugadas": return ranking.getRankingPartidasJugadas();
            case "victorias": return ranking.getRankingVictorias();
            case "derrotas": return ranking.getRankingDerrotas();
            default: return new TreeSet<>();
        }
    }

    private String getStatsText(String tipo, Perfil perfil) {
        return switch (tipo) {
            case "puntos" -> "- Puntos: " + perfil.getPuntos();
            case "partidasJugadas" -> "- Partidas: " + perfil.getPartidasJugadas();
            case "victorias" -> "- Victorias: " + perfil.getPartidasGanadas();
            case "derrotas" -> "- Derrotas: " + perfil.getPartidasPerdidas();
            default -> "";
        };
    }

    private void setTheme() {
        try {
            UIManager.put("OptionPane.background", COLOR_FONDO);
            UIManager.put("Panel.background", COLOR_FONDO);
            UIManager.put("Button.background", COLOR_AZUL);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
        } catch (Exception ignored) {}
    }
}