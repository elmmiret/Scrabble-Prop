package view;

import gestordepartida.*;
import gestordeperfil.Perfil;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JugarPartidaView extends JFrame {
    private static final int ANCHO = 1500;
    private static final int ALTO = 1000;
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Color COLOR_VERDE = new Color(50, 200, 100);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    private GestorDePartida gestorDePartida;
    private Partida partida;
    private Perfil jugadorActual;
    private Map<Ficha, Integer> atrilActual;
    private Ficha fichaSeleccionada;
    private JPanel panelTablero;
    private JPanel panelAtril;
    private JLabel lblJugador;
    private JLabel lblPuntos;
    JPanel infoPanel;
    JPanel mainPanel;
    private Map<Point, Ficha> colocacionesTemporales = new HashMap<>();
    private Map<Ficha, Integer> fichasTemporales = new HashMap<>(); // Track tiles in use
    private JPanel cambiarFichasPanel;
    private JButton btnConfirmarCambio;
    private JButton btnCancelarCambio;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    JPanel botonesPanel;

    public JugarPartidaView(Partida partida, GestorDePartida gestorDePartida) {
        super("Jugar Partida");
        this.partida = partida;
        this.gestorDePartida = gestorDePartida;
        this.jugadorActual = partida.getRondas().get(partida.getRondas().size()-1).getJugador();
        this.atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual);
        init();
        cargarEstadoInicial();
    }

    private void init() {

        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Panel principal
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de información
        infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setOpaque(false);
        lblJugador = new JLabel("Turno de: " + turnoActual.getJugador().getUsername());
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.BLACK);
        lblPuntos = new JLabel("Puntos: " + turnoActual.getPuntuacionJ1());
        lblPuntos.setFont(TITLE_FONT);
        lblPuntos.setForeground(Color.BLACK);
        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel del tablero
        panelTablero = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS, 1, 1));
        panelTablero.setPreferredSize(new Dimension(
                Tablero.COLUMNAS * 45,
                Tablero.FILAS * 45
        ));
        cargarTablero();
        mainPanel.add(new JScrollPane(panelTablero), BorderLayout.CENTER);

        // Panel lateral
        JPanel lateralPanel = new JPanel(new BorderLayout(10, 20));
        lateralPanel.setOpaque(false);

        // Atril
        panelAtril = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelAtril.setOpaque(false);
        cargarAtril();
        lateralPanel.add(panelAtril, BorderLayout.NORTH);

        // Botones
        botonesPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        addJugarButton(botonesPanel, "Confirmar", COLOR_VERDE, e -> confirmarColocacion());
        addJugarButton(botonesPanel, "Cambiar Fichas", COLOR_AZUL, e -> cambiarFichas());
        addJugarButton(botonesPanel, "Pasar turno", COLOR_AZUL, e -> pasarTurno());
        addJugarButton(botonesPanel, "Salir", COLOR_ROJO, e -> salirPartida());

        // Inside the init() method, after creating the botonesPanel
        cambiarFichasPanel = new JPanel(new BorderLayout(10, 5));
        cambiarFichasPanel.setPreferredSize(new Dimension(200, 100)); // Altura máxima
        cambiarFichasPanel.setOpaque(false);
        cambiarFichasPanel.setVisible(false); // Hidden by default

        // Add checkboxes for tiles (dynamically populated later)
        JPanel checkboxPanel = new JPanel(new GridLayout(0, 1, 2, 2)); // Reducir espaciado entre filas
        checkboxPanel.setOpaque(false);
        cambiarFichasPanel.add(new JScrollPane(checkboxPanel), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        btnPanel.setPreferredSize(new Dimension(0, 35)); // Altura fija compacta
        btnPanel.setOpaque(false);
        btnConfirmarCambio = new JButton("Confirmar");
        btnCancelarCambio = new JButton("Cancelar");
        styleButton(btnConfirmarCambio, COLOR_VERDE);
        styleButton(btnCancelarCambio, COLOR_ROJO);
        btnPanel.add(btnConfirmarCambio);
        btnPanel.add(btnCancelarCambio);
        cambiarFichasPanel.add(btnPanel, BorderLayout.SOUTH);

        lateralPanel.add(cambiarFichasPanel, BorderLayout.CENTER);
        lateralPanel.add(botonesPanel, BorderLayout.SOUTH);

        mainPanel.add(lateralPanel, BorderLayout.EAST);

        contentPane.add(mainPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn, Color color) {
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void pasarTurno() {
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        turnoActual.pasarTurno();
        turnoActual = partida.getRondas().get(partida.getRondas().size() - 1); // Get the new turn
        jugadorActual = turnoActual.getJugador(); // <-- Add this line to update the current player
        atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual); // Now uses the new player
        cargarTablero();
        cargarAtril();

        // Remove old components and update labels
        infoPanel.removeAll();

        lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.BLACK);

        lblPuntos = new JLabel("Puntos: " + (
                jugadorActual.equals(partida.getCreador()) ?
                        turnoActual.getPuntuacionJ1() :
                        turnoActual.getPuntuacionJ2()
        ));
        lblPuntos.setFont(TITLE_FONT);
        lblPuntos.setForeground(Color.BLACK);

        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private void addJugarButton(JPanel panel, String text, Color color, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isArmed() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.addActionListener(action);
        panel.add(btn);
    }

    private void cargarTablero() {
        panelTablero.removeAll();
        try {
            for (int x = 0; x < Tablero.FILAS; x++) {
                for (int y = 0; y < Tablero.COLUMNAS; y++) {
                    JPanel celda = crearCeldaTablero(x, y);
                    panelTablero.add(celda);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    private JPanel crearCeldaTablero(int x, int y) {
        JPanel celda = new JPanel(new BorderLayout());
        celda.setPreferredSize(new Dimension(40, 40));
        try {
            Tablero.TipoModificador mod = partida.getTablero().getTipoModificador(x, y);
            Color colorFondo = obtenerColorModificador(mod);
            celda.setBackground(colorFondo);

            Ficha ficha = partida.getTablero().getFicha(x, y);
            if (ficha != null) {
                JLabel lbl = new JLabel(ficha.getLetra(), SwingConstants.CENTER);
                lbl.setFont(LABEL_FONT);
                lbl.setForeground(Color.BLACK);
                celda.add(lbl);
            }
        } catch (CoordenadaFueraDeRangoException e) {
            celda.setBackground(Color.WHITE);
        }

        celda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Ficha fichaEnCelda = partida.getTablero().getFicha(x, y);
                    Point punto = new Point(x, y);
                    if (colocacionesTemporales.containsKey(punto)) {
                        Ficha f = colocacionesTemporales.get(punto);
                        atrilActual.put(f, atrilActual.getOrDefault(f, 0) + 1);
                        colocacionesTemporales.remove(punto);
                        // Update UI
                        cargarTablero();
                        cargarAtril();
                    } else if (fichaSeleccionada != null) {
                        colocarFichaTemporal(x, y);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return celda;
    }

    private Color obtenerColorModificador(Tablero.TipoModificador mod) {
        if (mod == null) return new Color(245, 245, 220);
        switch (mod) {
            case dobleTantoDeLetra: return new Color(173, 216, 230);
            case tripleTantoDeLetra: return new Color(65, 105, 225);
            case dobleTantoDePalabra: return new Color(255, 165, 0);
            case tripleTantoDePalabra: return new Color(220, 20, 60);
            default: return Color.WHITE;
        }
    }

    private void cargarAtril() {
        panelAtril.removeAll();
        for (Map.Entry<Ficha, Integer> entry : atrilActual.entrySet()) {
            Ficha f = entry.getKey();
            int available = entry.getValue();
            // Only show tiles that are NOT in temporary use
            available -= fichasTemporales.getOrDefault(f, 0);
            for (int i = 0; i < available; i++) {
                JButton btnFicha = new JButton(f.getLetra());
                btnFicha.setFont(LABEL_FONT);
                btnFicha.setPreferredSize(new Dimension(50, 50));
                btnFicha.setBackground(Color.LIGHT_GRAY);
                btnFicha.addActionListener(e -> seleccionarFicha(f));
                panelAtril.add(btnFicha);
            }
        }
        panelAtril.revalidate();
        panelAtril.repaint();
    }

    private void seleccionarFicha(Ficha ficha) {
        fichaSeleccionada = ficha;
        Arrays.stream(panelAtril.getComponents())
                .forEach(c -> ((JButton) c).setBackground(Color.LIGHT_GRAY));

        Arrays.stream(panelAtril.getComponents())
                .filter(c -> ((JButton) c).getText().equals(ficha.getLetra()))
                .findFirst()
                .ifPresent(c -> ((JButton) c).setBackground(Color.YELLOW));
    }

    private void colocarFichaTemporal(int x, int y) throws CasillaOcupadaException, CoordenadaFueraDeRangoException {
        if (partida.getTablero().getFicha(x, y) == null && fichaSeleccionada != null) {
            int count = atrilActual.getOrDefault(fichaSeleccionada, 0);
            if (count > 0) {
                atrilActual.put(fichaSeleccionada, count - 1);
                fichasTemporales.put(fichaSeleccionada, fichasTemporales.getOrDefault(fichaSeleccionada, 0) + 1);
                colocacionesTemporales.put(new Point(x, y), fichaSeleccionada);
                // Update the Atril UI immediately
                cargarAtril();

                colocacionesTemporales.put(new Point(x, y), fichaSeleccionada);
                int index = x * Tablero.COLUMNAS + y;
                JPanel celda = (JPanel) panelTablero.getComponent(index);
                celda.removeAll();
                JLabel lbl = new JLabel(fichaSeleccionada.getLetra(), SwingConstants.CENTER);
                lbl.setFont(LABEL_FONT);
                celda.add(lbl);
                celda.revalidate();
                celda.repaint();
                fichaSeleccionada = null; // Deselect after placement
            }
        }
    }

    private void confirmarColocacion() {
        if (colocacionesTemporales.isEmpty()) return;

        try {
            // Determine orientation (horizontal/vertical)
            List<Point> puntos = new ArrayList<>(colocacionesTemporales.keySet());
            boolean horizontal = puntos.stream().allMatch(p -> p.x == puntos.get(0).x);
            boolean vertical = puntos.stream().allMatch(p -> p.y == puntos.get(0).y);

            if (!horizontal && !vertical) {
                JOptionPane.showMessageDialog(this, "Tiles must form a straight line.");
                return;
            }

            // Sort points based on orientation
            puntos.sort((p1, p2) -> horizontal ? Integer.compare(p1.y, p2.y) : Integer.compare(p1.x, p2.x));

            // Check continuity including existing tiles
            if (!isContiguousWithExisting(puntos, horizontal)) {
                JOptionPane.showMessageDialog(this, "Tiles must be contiguous or connected to existing ones.");
                return;
            }

            // Find the full word start position
            Point start = findWordStart(puntos, horizontal);
            int xStart = start.x;
            int yStart = start.y;

            // Build the complete word (new + existing tiles)
            StringBuilder fullWord = new StringBuilder();
            if (horizontal) {
                for (int y = yStart; ; y++) {
                    Ficha f = getFichaAtPosition(xStart, y);
                    if (f == null) break;
                    fullWord.append(f.getLetra());
                }
            } else {
                for (int x = xStart; ; x++) {
                    Ficha f = getFichaAtPosition(x, yStart);
                    if (f == null) break;
                    fullWord.append(f.getLetra());
                }
            }

            // Validate the word with backend logic
            Turno currentTurn = partida.getRondas().get(partida.getRondas().size() - 1);
            System.out.println(fullWord);
            boolean isValid = currentTurn.colocarPalabra(
                    fullWord.toString(),
                    xStart,
                    horizontal ? yStart : xStart,
                    horizontal ? "horizontal" : "vertical"
            );

            if (isValid) {
                colocacionesTemporales.clear();
                fichasTemporales.clear();
                actualizarEstadoJuego();
            } else {
                revertTemporaryPlacements();
                JOptionPane.showMessageDialog(this, "Invalid word.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            revertTemporaryPlacements();
        }
    }

    private Ficha getFichaAtPosition(int x, int y) throws CoordenadaFueraDeRangoException {
        // Prioritize newly placed tiles, then check the board
        Ficha tempFicha = colocacionesTemporales.get(new Point(x, y));
        return (tempFicha != null) ? tempFicha : partida.getTablero().getFicha(x, y);
    }

    private boolean isContiguousWithExisting(List<Point> points, boolean isHorizontal) {
        int expectedCoord = isHorizontal ? points.get(0).y : points.get(0).x;
        for (Point p : points) {
            int currentCoord = isHorizontal ? p.y : p.x;
            if (currentCoord != expectedCoord) {
                // Check if gaps are filled by existing tiles
                for (int i = expectedCoord + 1; i < currentCoord; i++) {
                    int xCheck = isHorizontal ? p.x : i;
                    int yCheck = isHorizontal ? i : p.y;
                    try {
                        if (getFichaAtPosition(xCheck, yCheck) == null) return false;
                    } catch (CoordenadaFueraDeRangoException e) {
                        return false;
                    }
                }
            }
            expectedCoord = currentCoord + 1;
        }
        return true;
    }

    private Point findWordStart(List<Point> points, boolean isHorizontal) throws CoordenadaFueraDeRangoException {
        int coord = isHorizontal ? points.get(0).y : points.get(0).x;
        int fixed = isHorizontal ? points.get(0).x : points.get(0).y;

        // Move left/up to find the actual start
        while (true) {
            int prevCoord = coord - 1;
            int xCheck = isHorizontal ? fixed : prevCoord;
            int yCheck = isHorizontal ? prevCoord : fixed;
            if (getFichaAtPosition(xCheck, yCheck) == null) break;
            coord = prevCoord;
        }

        return isHorizontal ? new Point(fixed, coord) : new Point(coord, fixed);
    }

    private void validateCrosswords(Point placement) throws CoordenadaFueraDeRangoException {
        // Check vertical crosswords for horizontal placements and vice versa
        boolean isHorizontal = colocacionesTemporales.keySet().stream().allMatch(p -> p.x == placement.x);

        if (isHorizontal) {
            // Check vertical words at each new tile
            for (Point p : colocacionesTemporales.keySet()) {
                StringBuilder verticalWord = new StringBuilder();
                for (int x = p.x; ; x--) {
                    Ficha f = getFichaAtPosition(x, p.y);
                    if (f == null) break;
                    verticalWord.insert(0, f.getLetra());
                }
                for (int x = p.x + 1; ; x++) {
                    Ficha f = getFichaAtPosition(x, p.y);
                    if (f == null) break;
                    verticalWord.append(f.getLetra());
                }
                //if (verticalWord.length() > 1 && !partida.dawg.existePalabra(verticalWord.toString())) {
                   // throw new InvalidWordException("Invalid crossword: " + verticalWord);
                //}
            }
        } else {
            // Similar logic for vertical placements
        }
    }

    private void revertTemporaryPlacements() {
        colocacionesTemporales.forEach((pos, ficha) -> {
            atrilActual.put(ficha, atrilActual.getOrDefault(ficha, 0) + 1);
        });
        colocacionesTemporales.clear();
        fichasTemporales.clear();
        refreshUI();
    }

    private void refreshUI() {
        cargarTablero();
        cargarAtril();
    }

    private void actualizarEstadoJuego() {
        cargarTablero();
        cargarAtril();
        // Update points and current player
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size()-1);
        lblPuntos.setText("Puntos: " + (jugadorActual.equals(partida.getCreador())
                ? turnoActual.getPuntuacionJ1()
                : turnoActual.getPuntuacionJ2()));
        jugadorActual = turnoActual.getJugador();
        lblJugador.setText("Turno de: " + jugadorActual.getUsername());
    }

    private void ejecutarTurnoIA() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Turno turnoIA = partida.getRondas().get(partida.getRondas().size()-1);
                turnoIA.jugarIA();
                return null;
            }

            @Override
            protected void done() {
                actualizarEstadoJuego();
            }
        }.execute();
    }

    private void revertirColocacionesTemporales() {
        colocacionesTemporales.forEach((p, f) -> {
            atrilActual.put(f, atrilActual.getOrDefault(f, 0) + 1);
        });
        colocacionesTemporales.clear();
        fichasTemporales.clear();
        cargarAtril();
        cargarTablero();
    }

    private void cambiarFichas() {
        // Hide regular buttons and show tile selection UI
        botonesPanel.setVisible(false);
        cambiarFichasPanel.setVisible(true);

        // Populate checkboxes
        JPanel checkboxPanel = (JPanel) ((JScrollPane) cambiarFichasPanel.getComponent(0)).getViewport().getView();
        checkboxPanel.removeAll();
        checkBoxes.clear();

        // Create checkboxes for each tile
        List<Ficha> fichasEnAtril = new ArrayList<>();
        for (Map.Entry<Ficha, Integer> entry : atrilActual.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                fichasEnAtril.add(entry.getKey());
            }
        }

        for (Ficha ficha : fichasEnAtril) {
            JCheckBox check = new JCheckBox(ficha.getLetra());
            check.setFont(LABEL_FONT);
            check.setOpaque(false);
            checkboxPanel.add(check);
            checkBoxes.add(check);
        }

        // Limpiar listeners previos de los botones
        for (ActionListener al : btnConfirmarCambio.getActionListeners()) {
            btnConfirmarCambio.removeActionListener(al);
        }
        for (ActionListener al : btnCancelarCambio.getActionListeners()) {
            btnCancelarCambio.removeActionListener(al);
        }
        // Add action listeners
        btnConfirmarCambio.addActionListener(e -> confirmarCambioFichas());
        btnCancelarCambio.addActionListener(e -> cancelarCambioFichas());

        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void confirmarCambioFichas() {
        List<String> fichasCambio = new ArrayList<>();
        for (JCheckBox check : checkBoxes) {
            if (check.isSelected()) {
                fichasCambio.add(check.getText());
            }
        }

        Turno turnoActual = partida.getRondas().get(partida.getRondas().size()-1);
        boolean exito = gestorDePartida.cambiarFichas(turnoActual, atrilActual, fichasCambio);

        if (exito) {
            turnoActual = partida.getRondas().get(partida.getRondas().size() - 1); // Get the new turn
            jugadorActual = turnoActual.getJugador(); // <-- Add this line to update the current player
            atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual); // Now uses the new player
            cargarTablero();
            cargarAtril();

            // Remove old components and update labels
            infoPanel.removeAll();

            lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
            lblJugador.setFont(TITLE_FONT);
            lblJugador.setForeground(Color.BLACK);

            lblPuntos = new JLabel("Puntos: " + (
                    jugadorActual.equals(partida.getCreador()) ?
                            turnoActual.getPuntuacionJ1() :
                            turnoActual.getPuntuacionJ2()
            ));
            lblPuntos.setFont(TITLE_FONT);
            lblPuntos.setForeground(Color.BLACK);

            infoPanel.add(lblJugador);
            infoPanel.add(lblPuntos);

            infoPanel.revalidate();
            infoPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Error al cambiar fichas");
        }

        // Reset UI
        cancelarCambioFichas();
    }

    private void cancelarCambioFichas() {
        botonesPanel.setVisible(true);
        cambiarFichasPanel.setVisible(false);
        checkBoxes.clear();
    }

    private void salirPartida() {
        this.dispose();
    }

    private void cargarEstadoInicial() {
        if (partida.getModoPartida() == Partida.Modo.PvIA && !jugadorActual.equals(partida.getCreador())) {
            ejecutarTurnoIA();
        }
    }

}