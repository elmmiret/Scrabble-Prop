package view;

import gestordeperfil.Perfil;
import ranking.Ranking;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;

public class RankingView extends JFrame {
    private static final int ANCHO = 400;
    private static final int ALTO = 700;
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Color COLOR_NARANJA = new Color(240, 73, 48);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font RANKING_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final GestorDeView gestorDeView;
    private final Ranking ranking;

    public RankingView(GestorDeView gestorDeView, Ranking ranking) {
        super("Ranking");
        this.gestorDeView = gestorDeView;
        this.ranking = ranking;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ANCHO, ALTO));

        TableroMoviendo tableroMoviendo = new TableroMoviendo();
        tableroMoviendo.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(tableroMoviendo, JLayeredPane.DEFAULT_LAYER);

        JPanel mainPanel = createMainPanel();
        mainPanel.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(mainPanel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
        tableroMoviendo.iniciarMovimiento();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel titleLabel = new JLabel("RANKING");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(COLOR_NARANJA);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(75, 20, 75, 20));
        buttonPanel.setOpaque(false);

        addRankingButton(buttonPanel, "Por Puntos", COLOR_AZUL, () -> mostrarRanking("puntos"));
        addRankingButton(buttonPanel, "Por Partidas Jugadas", COLOR_AZUL, () -> mostrarRanking("partidasJugadas"));
        addRankingButton(buttonPanel, "Por Victorias", COLOR_AZUL, () -> mostrarRanking("victorias"));
        addRankingButton(buttonPanel, "Por Derrotas", COLOR_AZUL, () -> mostrarRanking("derrotas"));
        addRankingButton(buttonPanel, "Atrás", COLOR_ROJO, gestorDeView::mostrarMain);

        panel.add(buttonPanel, BorderLayout.CENTER);
        return panel;
    }

    private void addRankingButton(JPanel panel, String text, Color color, Runnable action) {
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

                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                g2d.setStroke(grosorBorde);
                g2d.setColor(isHovering ? colorBordeHover : colorBordeNormal);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);

                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                g2d.drawString(getText(),
                        (int)((getWidth() - r.getWidth()) / 2),
                        (int)((getHeight() - r.getHeight()) / 2 + fm.getAscent())
                );
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
        panel.add(button);
    }

    private void mostrarRanking(String tipo) {
        JDialog dialog = new JDialog(this, "Ranking", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0); // Reduced vertical spacing

        JLabel title = new JLabel("RANKING " + tipo.toUpperCase());
        title.setFont(BUTTON_FONT);
        title.setForeground(Color.BLACK);
        content.add(title, gbc);

        TreeSet<Perfil> rkg = getRankingSet(tipo);
        int position = 1;

        for (Perfil perfil : rkg) {
            JPanel entryPanel = new JPanel(new BorderLayout(10, 0));
            entryPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

            JLabel posLabel = new JLabel(position++ + ".");
            posLabel.setFont(RANKING_FONT);
            posLabel.setForeground(Color.BLACK);

            JLabel nameLabel = new JLabel(perfil.getUsername());
            nameLabel.setFont(RANKING_FONT);
            nameLabel.setForeground(Color.BLACK);

            leftPanel.add(posLabel);
            leftPanel.add(nameLabel);

            JLabel statsLabel = new JLabel(getStatsText(tipo, perfil));
            statsLabel.setFont(RANKING_FONT);

            entryPanel.add(leftPanel, BorderLayout.WEST);
            entryPanel.add(statsLabel, BorderLayout.EAST);
            content.add(entryPanel, gbc);
        }

        if (rkg.isEmpty()) {
            JLabel emptyLabel = new JLabel("No hay datos disponibles");
            emptyLabel.setFont(RANKING_FONT);
            content.add(emptyLabel, gbc);
        }

        // añadir padding
        gbc.weighty = 1;
        content.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

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
}