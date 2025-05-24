package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;
import gestordepartida.GestorDePartida;
import gestordepartida.Partida;
import gestordepartida.Turno;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

public class GestionPartidaView extends JFrame {
    private static final int ANCHO = 400;
    private static final int ALTO = 700;
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
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ANCHO, ALTO));

        // Añadir tablero con animacion
        TableroMoviendo tableroMoviendo = new TableroMoviendo();
        tableroMoviendo.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(tableroMoviendo, JLayeredPane.DEFAULT_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

        JLabel titleLabel = new JLabel("GESTIÓN DE PARTIDA");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(COLOR_NARANJA);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));
        buttonPanel.setOpaque(false);

        addPartidaButton(buttonPanel, "Crear nueva partida", COLOR_AZUL, this::crearNuevaPartida);
        addPartidaButton(buttonPanel, "Cargar partida", COLOR_AZUL, this::cargarPartida);
        addPartidaButton(buttonPanel, "Eliminar partida", COLOR_AZUL, this::eliminarPartida);
        addPartidaButton(buttonPanel, "Consultar partidas", COLOR_AZUL, this::mostrarPartidas);
        addPartidaButton(buttonPanel, "Ver repetición", COLOR_AZUL, this::verRepeticion);
        addPartidaButton(buttonPanel, "Atrás", COLOR_ROJO, e -> gestorDeView.mostrarMain());

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
        tableroMoviendo.iniciarMovimiento();
    }

    private void addPartidaButton(JPanel panel, String text, Color color, java.awt.event.ActionListener action) {
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

                // Fondo
                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                // Borde
                g2d.setStroke(grosorBorde);
                g2d.setColor(isHovering ? colorBordeHover : colorBordeNormal);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);

                // Texto
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
            JTextField idField = new JTextField();
            JTextField nombreField = new JTextField();
            JComboBox<String> idiomaCombo = new JComboBox<>(new String[]{"CAT", "CAST", "ENG"});
            JComboBox<String> modoCombo = new JComboBox<>(new String[]{"PvP", "PvIA"});

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("ID de partida:"));
            panel.add(idField);
            panel.add(new JLabel("Nombre de partida:"));
            panel.add(nombreField);
            panel.add(new JLabel("Idioma:"));
            panel.add(idiomaCombo);
            panel.add(new JLabel("Modo de juego:"));
            panel.add(modoCombo);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Nueva Configuración de Partida",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) return;

            String idStr = idField.getText().trim();
            String nombre = nombreField.getText().trim();
            String idioma = (String) idiomaCombo.getSelectedItem();
            String modo = (String) modoCombo.getSelectedItem();

            if (idStr.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID y Nombre son campos obligatorios");
                return;
            }

            int id = Integer.parseInt(idStr);
            if (gestorDePartida.getPartidas().containsKey(id)) {
                JOptionPane.showMessageDialog(this, "¡Ya existe una partida con este ID!");
                return;
            }

            Partida.Idioma idiomaPartida = switch (idioma) {
                case "CAT" -> Partida.Idioma.CAT;
                case "CAST" -> Partida.Idioma.CAST;
                case "ENG" -> Partida.Idioma.ENG;
                default -> throw new IllegalArgumentException("Idioma inválido");
            };

            if ("PvP".equals(modo)) {
                Perfil oponente = autenticarUsuario();
                if (oponente == null || oponente.equals(jugador)) {
                    JOptionPane.showMessageDialog(this, "¡No puedes jugar contra ti mismo!");
                    return;
                }
                Partida partida = gestorDePartida.crearPartida(id, nombre, idiomaPartida, jugador, Partida.Modo.PvP, oponente, 0);
                gestorDeView.mostrarPartida(partida);
                JOptionPane.showMessageDialog(this, "Partida PvP creada exitosamente!");
            } else {
                String[] dificultadOptions = {"1", "2", "3"};
                String dificultadStr = (String) JOptionPane.showInputDialog(
                        this,
                        "Selecciona dificultad:",
                        "Dificultad IA",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        dificultadOptions,
                        dificultadOptions[0]
                );

                if (dificultadStr == null) return;

                int dificultad = Integer.parseInt(dificultadStr);
                Partida partida = gestorDePartida.crearPartida(id, nombre, idiomaPartida, jugador, Partida.Modo.PvIA, null, dificultad);
                gestorDeView.mostrarPartida(partida);
                JOptionPane.showMessageDialog(this, "Partida PvIA creada exitosamente!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID debe ser un número válido");
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
            if (partida.getRondas().get(partida.getRondas().size() - 2).getTipoJugada().equals(Turno.TipoJugada.finalizar))
            {
                JOptionPane.showMessageDialog(this, "No se ha podido cargar la partida: ya se ha finalizado.");
                return;
            }
            if (partida == null || !gestorDePartida.existePartidaJugador(jugador, id)) {
                JOptionPane.showMessageDialog(this, "Partida no encontrada.");
                return;
            }

            System.out.println("Voy a mostrar la partida");
            gestorDeView.mostrarPartida(partida);
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

    private void mostrarPartidas(ActionEvent e) {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        List<Partida> partidas = gestorDePartida.obtenerPartidasJugador(jugador);

        JDialog dialog = new JDialog(this, "Tus Partidas", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);

        JLabel title = new JLabel("TUS PARTIDAS ACTIVAS");
        title.setFont(BUTTON_FONT);
        title.setForeground(Color.BLACK);
        content.add(title, gbc);

        if (partidas.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tienes partidas activas");
            emptyLabel.setFont(BUTTON_FONT);
            content.add(emptyLabel, gbc);
        } else {
            for (Partida p : partidas) {
                JPanel entryPanel = new JPanel(new BorderLayout(10, 0));
                entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                entryPanel.setBackground(new Color(240, 240, 240));

                JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                leftPanel.setBackground(new Color(240, 240, 240));

                JLabel idLabel = new JLabel("ID: " + p.getId());
                idLabel.setFont(BUTTON_FONT);
                idLabel.setForeground(COLOR_AZUL);

                JLabel nameLabel = new JLabel(p.getNombre());
                nameLabel.setFont(BUTTON_FONT);
                nameLabel.setForeground(Color.BLACK);

                leftPanel.add(idLabel);
                leftPanel.add(nameLabel);

                JPanel rightPanel = new JPanel(new GridLayout(2, 1));
                rightPanel.setBackground(new Color(240, 240, 240));

                JLabel modeLabel = new JLabel("Modo: " + p.getModoPartida());
                modeLabel.setFont(BUTTON_FONT);
                modeLabel.setForeground(Color.BLACK);

                JLabel detailLabel = new JLabel(
                        p.getModoPartida() == Partida.Modo.PvP ?
                                "Oponente: " + p.getOponente().getUsername() :
                                "Dificultad IA: " + p.getDificultad()
                );
                detailLabel.setFont(BUTTON_FONT);
                detailLabel.setForeground(Color.BLACK);

                rightPanel.add(modeLabel);
                rightPanel.add(detailLabel);

                entryPanel.add(leftPanel, BorderLayout.WEST);
                entryPanel.add(rightPanel, BorderLayout.EAST);

                content.add(entryPanel, gbc);
                content.add(new JSeparator(), gbc);
            }
        }

        gbc.weighty = 1;
        content.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void mostrarPartidas(ActionEvent e, boolean paraRepeticion) {  // Añadir parámetro
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        List<Partida> partidas = gestorDePartida.obtenerPartidasJugador(jugador);

        JDialog dialog = new JDialog(this, "Tus Partidas", true);
        dialog.setSize(600, 500);  // Aumentar tamaño
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);

        JLabel title = new JLabel(paraRepeticion ? "SELECCIONA PARTIDA PARA REPRODUCIR" : "TUS PARTIDAS ACTIVAS");
        title.setFont(BUTTON_FONT);
        title.setForeground(Color.BLACK);
        content.add(title, gbc);

        if (partidas.isEmpty()) {
            JLabel emptyLabel = new JLabel("No tienes partidas activas");
            emptyLabel.setFont(BUTTON_FONT);
            content.add(emptyLabel, gbc);
        } else {
            for (Partida p : partidas) {
                JPanel entryPanel = crearPanelPartida(p);

                if (paraRepeticion) {
                    entryPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    entryPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            dialog.dispose();
                            iniciarRepeticion(p);
                        }
                    });
                }

                content.add(entryPanel, gbc);
                content.add(new JSeparator(), gbc);
            }
        }

        gbc.weighty = 1;
        content.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }


    private JPanel crearPanelPartida(Partida p) {
        JPanel entryPanel = new JPanel(new BorderLayout(10, 0));
        entryPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        entryPanel.setBackground(new Color(240, 240, 240));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(new Color(240, 240, 240));

        JLabel idLabel = new JLabel("ID: " + p.getId());
        idLabel.setFont(BUTTON_FONT);
        idLabel.setForeground(COLOR_AZUL);

        JLabel nameLabel = new JLabel(p.getNombre());
        nameLabel.setFont(BUTTON_FONT);
        nameLabel.setForeground(Color.BLACK);

        leftPanel.add(idLabel);
        leftPanel.add(nameLabel);

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.setBackground(new Color(240, 240, 240));

        JLabel modeLabel = new JLabel("Modo: " + p.getModoPartida());
        modeLabel.setFont(BUTTON_FONT);
        modeLabel.setForeground(Color.BLACK);

        JLabel detailLabel = new JLabel(
                p.getModoPartida() == Partida.Modo.PvP ?
                        "Oponente: " + p.getOponente().getUsername() :
                        "Dificultad IA: " + p.getDificultad()
        );
        detailLabel.setFont(BUTTON_FONT);
        detailLabel.setForeground(Color.BLACK);

        rightPanel.add(modeLabel);
        rightPanel.add(detailLabel);

        entryPanel.add(leftPanel, BorderLayout.WEST);
        entryPanel.add(rightPanel, BorderLayout.EAST);

        return entryPanel;
    }

    private Perfil autenticarUsuario() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Log in Jugador", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (gestorDePerfil.existeJugador(username)) {
                if (gestorDePerfil.esPasswordCorrecta(username, password)) {
                    return gestorDePerfil.getPerfil(username);
                } else JOptionPane.showMessageDialog(this, "Password incorrecta");
            } else JOptionPane.showMessageDialog(this, "No existe ningún perfil con este username");
        }
        return null;
    }

    // En el método verRepeticion
    private void verRepeticion(ActionEvent e) {
        mostrarPartidas(e, true);
    }

    private void iniciarRepeticion(Partida partida) {
        SwingUtilities.invokeLater(() -> {
            new RepeticionPartidaView(
                    partida,
                    gestorDePartida,
                    gestorDePerfil
            ).setVisible(true);
        });
    }
}