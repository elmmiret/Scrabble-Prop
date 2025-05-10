package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import gestordepartida.GestorDePartida;
import gestordepartida.Partida;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

public class GestionPartidaView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private final Color COLOR_FONDO = new Color(36, 36, 36);
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Color COLOR_NARANJA = new Color(240, 73, 48);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private final GestorDeView gestorDeView;
    private final GestorDePartida gestorDePartida;
    private final GestorDePerfil gestorDePerfil;

    public GestionPartidaView(GestorDeView gestorDeView, GestorDePartida gestorDePartida, GestorDePerfil gestorDePerfil) {
        super("Gestión de Partida");
        this.gestorDeView = gestorDeView;
        this.gestorDePartida = gestorDePartida;
        this.gestorDePerfil = gestorDePerfil;
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
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

        JLabel titleLabel = new JLabel("GESTIÓN DE PARTIDA");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(COLOR_NARANJA);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        buttonPanel.setOpaque(false);

        addGameButton(buttonPanel, "Crear nueva partida", COLOR_AZUL, this::crearNuevaPartida);
        addGameButton(buttonPanel, "Cargar partida", COLOR_AZUL, this::cargarPartida);
        addGameButton(buttonPanel, "Eliminar partida", COLOR_AZUL, this::eliminarPartida);
        addGameButton(buttonPanel, "Consultar partidas", COLOR_AZUL, this::consultarPartidas);
        addGameButton(buttonPanel, "Atrás", COLOR_ROJO, e -> gestorDeView.mostrarMain());

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.setBounds(0, 0, WIDTH, HEIGHT);
        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
        tableroMoviendo.iniciarMovimiento();
    }

    private void addGameButton(JPanel panel, String text, Color color, java.awt.event.ActionListener action) {
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

                // Background
                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                // Border
                g2d.setStroke(grosorBorde);
                g2d.setColor(isHovering ? colorBordeHover : colorBordeNormal);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);

                // Text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                g2d.drawString(getText(),
                        (int)((getWidth() - r.getWidth()) / 2),
                        (int)((getHeight() - r.getHeight()) / 2 + fm.getAscent()));
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
        button.addActionListener(action);
        button.setFocusPainted(false);
        panel.add(button);
    }

    private void crearNuevaPartida(ActionEvent e) {
        if (!gestorDePerfil.hayJugadores()) {
            JOptionPane.showMessageDialog(this, "No hay jugadores registrados para jugar.");
            return;
        }

        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        try {
            String idStr = JOptionPane.showInputDialog(this, "ID de partida:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            if (gestorDePartida.getPartidas().containsKey(id)) {
                JOptionPane.showMessageDialog(this, "¡Ya existe una partida con este ID!");
                return;
            }

            String nombre = JOptionPane.showInputDialog(this, "Nombre de la partida:");
            if (nombre == null) return;

            String[] idiomaOptions = {"CAT", "CAST", "ENG"};
            String idioma = (String) JOptionPane.showInputDialog(this,
                    "Selecciona idioma:", "Idioma",
                    JOptionPane.QUESTION_MESSAGE, null, idiomaOptions, idiomaOptions[0]);

            Partida.Idioma idiomaPartida = switch (idioma) {
                case "CAT" -> Partida.Idioma.CAT;
                case "CAST" -> Partida.Idioma.CAST;
                case "ENG" -> Partida.Idioma.ENG;
                default -> throw new IllegalArgumentException("Idioma inválido");
            };

            String[] modoOptions = {"PvP", "PvIA"};
            String modo = (String) JOptionPane.showInputDialog(this,
                    "Selecciona modo:", "Modo de juego",
                    JOptionPane.QUESTION_MESSAGE, null, modoOptions, modoOptions[0]);

            if ("PvP".equals(modo)) {
                Perfil oponente = autenticarUsuario();
                if (oponente == null || oponente.equals(jugador)) {
                    JOptionPane.showMessageDialog(this, "¡No puedes jugar contra ti mismo!");
                    return;
                }
                gestorDePartida.crearPartida(id, nombre, idiomaPartida, jugador, Partida.Modo.PvP, oponente, 0);
                JOptionPane.showMessageDialog(this, "Partida PvP creada exitosamente!");
            } else {
                String[] dificultadOptions = {"1", "2", "3"};
                String dificultadStr = (String) JOptionPane.showInputDialog(this,
                        "Selecciona dificultad:", "Dificultad IA",
                        JOptionPane.QUESTION_MESSAGE, null, dificultadOptions, dificultadOptions[0]);

                int dificultad = Integer.parseInt(dificultadStr);
                gestorDePartida.crearPartida(id, nombre, idiomaPartida, jugador, Partida.Modo.PvIA, null, dificultad);
                JOptionPane.showMessageDialog(this, "Partida PvIA creada exitosamente!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cargarPartida(ActionEvent e) {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        try {
            String idStr = JOptionPane.showInputDialog(this, "ID de partida:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            Partida partida = gestorDePartida.obtenerPartida(id);
            if (partida == null || !gestorDePartida.existePartidaJugador(jugador, id)) {
                JOptionPane.showMessageDialog(this, "Partida no encontrada.");
                return;
            }

            gestorDeView.mostrarPartida(partida);
            JOptionPane.showMessageDialog(this, "Partida cargada exitosamente!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void eliminarPartida(ActionEvent e) {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        try {
            String idStr = JOptionPane.showInputDialog(this, "ID de partida a eliminar:");
            if (idStr == null) return;
            int id = Integer.parseInt(idStr);

            if (gestorDePartida.eliminarPartida(id)) {
                JOptionPane.showMessageDialog(this, "Partida eliminada exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Partida no encontrada.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void consultarPartidas(ActionEvent e) {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        StringBuilder sb = new StringBuilder();
        List<Partida> partidas = gestorDePartida.obtenerPartidasJugador(jugador);

        if (partidas.isEmpty()) {
            sb.append("No tienes partidas activas.");
        } else {
            sb.append("Tus partidas activas:\n\n");
            for (Partida p : partidas) {
                sb.append(String.format("ID: %d\nNombre: %s\nModo: %s\n",
                        p.getId(), p.getNombre(), p.getModoPartida()));
                if (p.getModoPartida() == Partida.Modo.PvP) {
                    sb.append("Oponente: ").append(p.getOponente().getUsername()).append("\n");
                } else {
                    sb.append("Dificultad IA: ").append(p.getDificultad()).append("\n");
                }
                sb.append("---------------------\n");
            }
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Tus Partidas", JOptionPane.INFORMATION_MESSAGE);
    }

    private Perfil autenticarUsuario() {
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.isEmpty()) return null;

        if (!gestorDePerfil.existeJugador(username)) {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
            return null;
        }

        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, passwordField, "Password:", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return null;

        String password = new String(passwordField.getPassword());
        if (!gestorDePerfil.esPasswordCorrecta(username, password)) {
            JOptionPane.showMessageDialog(this, "Password incorrecta.");
            return null;
        }

        return gestorDePerfil.getPerfil(username);
    }
}