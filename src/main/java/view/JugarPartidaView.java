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
    private static final int ANCHO = 1200;
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
    private List<Point> colocacionesTemporales = new ArrayList<>();

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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setOpaque(false);
        lblJugador = new JLabel("Jugador: " + jugadorActual.getUsername());
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.WHITE);
        lblPuntos = new JLabel("Puntos: " + partida.getRondas().get(partida.getRondas().size()-1).getPuntuacionJ1());
        lblPuntos.setFont(TITLE_FONT);
        lblPuntos.setForeground(Color.WHITE);
        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel del tablero
        panelTablero = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS, 1, 1));
        panelTablero.setPreferredSize(new Dimension(
                Tablero.COLUMNAS * 45,  // 45 pixels per column (40+5 margin)
                Tablero.FILAS * 45       // 45 pixels per row
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
        JPanel botonesPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        addJugarButton(botonesPanel, "Confirmar", COLOR_VERDE, e -> confirmarColocacion());
        addJugarButton(botonesPanel, "Cambiar Fichas", COLOR_AZUL, e -> cambiarFichas());
        addJugarButton(botonesPanel, "Salir", COLOR_ROJO, e -> salirPartida());
        lateralPanel.add(botonesPanel, BorderLayout.SOUTH);

        mainPanel.add(lateralPanel, BorderLayout.EAST);

        contentPane.add(mainPanel, BorderLayout.CENTER);
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
                if (fichaSeleccionada != null) {
                    try {
                        colocarFichaTemporal(x, y);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
            for (int i = 0; i < entry.getValue(); i++) {
                Ficha f = entry.getKey();
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
        if (partida.getTablero().getFicha(x, y) == null) {
            colocacionesTemporales.add(new Point(x, y));
            int index = x * Tablero.COLUMNAS + y;
            JPanel celda = (JPanel) panelTablero.getComponent(index);
            celda.removeAll();
            JLabel lbl = new JLabel(fichaSeleccionada.getLetra(), SwingConstants.CENTER);
            lbl.setFont(LABEL_FONT);
            celda.add(lbl);
            celda.revalidate();
            celda.repaint();
            fichaSeleccionada = null;
        }
    }

    private void confirmarColocacion() {
        if (colocacionesTemporales.isEmpty()) return;

        try {
            // Construir la palabra colocada
            StringBuilder palabra = new StringBuilder();
            int x = colocacionesTemporales.get(0).x;
            int y = colocacionesTemporales.get(0).y;
            String orientacion = "horizontal";
            if (colocacionesTemporales.size() > 1) {
                if (colocacionesTemporales.get(1).x != x) orientacion = "vertical";
            }

            for (Point p : colocacionesTemporales) {
                Ficha f = partida.getTablero().getFicha(p.x, p.y);
                if (f == null) {
                    palabra.append(fichaSeleccionada.getLetra());
                }
            }

            Turno turnoActual = partida.getRondas().get(partida.getRondas().size()-1);
            boolean exito = turnoActual.colocarPalabra(
                    palabra.toString(),
                    x,
                    y,
                    orientacion
            );

            if (exito) {
                actualizarEstadoJuego();
                colocacionesTemporales.clear();
            } else {
                JOptionPane.showMessageDialog(this, "Movimiento inválido");
                revertirColocacionesTemporales();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            revertirColocacionesTemporales();
        }
    }

    private void actualizarEstadoJuego() {
        cargarTablero();
        cargarAtril();
        lblPuntos.setText("Puntos: " + (
                jugadorActual.equals(partida.getCreador()) ?
                        partida.getRondas().get(partida.getRondas().size()-1).getPuntuacionJ1() :
                        partida.getRondas().get(partida.getRondas().size()-1).getPuntuacionJ2()
        ));
        // Cambiar turno
        jugadorActual = partida.getRondas().get(partida.getRondas().size()-1).getJugador();
        lblJugador.setText("Jugador: " + jugadorActual.getUsername());
        if (partida.getModoPartida() == Partida.Modo.PvIA && !jugadorActual.equals(partida.getCreador())) {
            ejecutarTurnoIA();
        }
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
        colocacionesTemporales.forEach(p -> {
            JPanel celda = (JPanel) panelTablero.getComponent(p.y * Tablero.FILAS + p.x);
            celda.removeAll();
            celda.revalidate();
            celda.repaint();
        });
        colocacionesTemporales.clear();
    }

    private void cambiarFichas() {
        List<String> fichasCambio = new ArrayList<>();
        JPanel dialogPanel = new JPanel(new GridLayout(0, 1));
        for (Map.Entry<Ficha, Integer> entry : atrilActual.entrySet()) {
            JCheckBox check = new JCheckBox(entry.getKey().getLetra());
            dialogPanel.add(check);
        }

        int result = JOptionPane.showConfirmDialog(
                this, dialogPanel, "Selecciona fichas a cambiar", JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            for (Component comp : dialogPanel.getComponents()) {
                JCheckBox check = (JCheckBox) comp;
                if (check.isSelected()) {
                    fichasCambio.add(check.getText());
                }
            }
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size()-1);
            boolean exito = gestorDePartida.cambiarFichas(
                    turnoActual,
                    atrilActual,
                    fichasCambio
            );
            if (exito) {
                cargarAtril();
                JOptionPane.showMessageDialog(this, "Fichas cambiadas exitosamente");
            } else {
                JOptionPane.showMessageDialog(this, "Error al cambiar fichas");
            }
        }
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