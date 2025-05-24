package view;

import exceptions.CoordenadaFueraDeRangoException;
import gestordepartida.*;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
/**
 * Vista para la repetición de una partida guardada, permitiendo navegar entre los turnos históricos.
 * Muestra el tablero, el atril del jugador contrario y la información de puntos y jugador actual.
 * Incluye controles para avanzar, retroceder, saltar a un turno específico y salir.
 *
 * @author Albert Aulet Niubó
 */
public class RepeticionPartidaView extends JFrame {
    private final Partida partida;
    private final GestorDePartida gestorDePartida;
    private int currentTurnIndex;
    private JPanel panelTablero;
    private JPanel panelAtril;
    private JLabel lblJugador;
    private JLabel lblPuntos;
    private JTextField txtTurno;
    private GestorDeView gestorDeView;

    private static final int ANCHO = 1400;
    private static final int ALTO = 1000;
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Color COLOR_VERDE = new Color(50, 200, 100);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    /**
     * Constructor que inicializa la vista de repetición de partida.
     *
     * @param partida           Partida a visualizar.
     * @param gestorDePartida   Gestor que proporciona lógica relacionada con la partida.
     * @param gestorDeView     Gestor de perfiles (no utilizado directamente en esta versión).
     */
    public RepeticionPartidaView(Partida partida, GestorDePartida gestorDePartida, GestorDeView gestorDeView) {
        super("Repetición - " + partida.getNombre());
        if (partida == null || partida.getRondas() == null || partida.getRondas().isEmpty()) {
            throw new IllegalArgumentException("Partida no válida o sin turnos guardados");
        }
        this.partida = partida;
        this.gestorDePartida = gestorDePartida;
        this.gestorDeView = gestorDeView;
        this.currentTurnIndex = 0;
        initUI();
        cargarTurno(0);
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setOpaque(false);
        lblJugador = new JLabel();
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.BLACK);

        lblPuntos = new JLabel();
        lblPuntos.setFont(TITLE_FONT);
        lblPuntos.setForeground(Color.BLACK);

        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        panelTablero = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS, 1, 1));
        panelTablero.setPreferredSize(new Dimension(
                Tablero.COLUMNAS * 45,
                Tablero.FILAS * 45
        ));
        mainPanel.add(new JScrollPane(panelTablero), BorderLayout.CENTER);

        JPanel lateralPanel = new JPanel(new BorderLayout(10, 20));
        lateralPanel.setOpaque(false);

        panelAtril = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelAtril.setOpaque(false);
        lateralPanel.add(panelAtril, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addStyledButton(controlsPanel, "← Anterior", COLOR_AZUL, e -> mostrarTurnoPrevio());
        addStyledButton(controlsPanel, "Siguiente →", COLOR_AZUL, e -> mostrarTurnoSiguiente());

        JPanel jumpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        txtTurno = new JTextField("1", 3);
        txtTurno.setFont(LABEL_FONT);
        JButton btnIr = new JButton("Ir");
        styleButton(btnIr, COLOR_VERDE);
        btnIr.addActionListener(e -> saltarATurno(txtTurno.getText()));

        jumpPanel.add(new JLabel("Turno:"));
        jumpPanel.add(txtTurno);
        jumpPanel.add(btnIr);
        controlsPanel.add(jumpPanel);

        addStyledButton(controlsPanel, "Salir", COLOR_ROJO, e -> {
            dispose();
            gestorDeView.mostrarGestionPartida();
        });

        lateralPanel.add(controlsPanel, BorderLayout.SOUTH);
        mainPanel.add(lateralPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private void addStyledButton(JPanel panel, String text, Color color, ActionListener action) {
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

    private void styleButton(JButton btn, Color color) {
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    /**
     * Carga y muestra la información de un turno específico.
     * Actualiza el tablero, el atril, el jugador actual y los puntos.
     *
     * @param turnIndex Índice del turno a cargar (basado en 0).
     */
    private void cargarTurno(int turnIndex) {
        if (partida.getRondas() == null || turnIndex < 0 || turnIndex >= partida.getRondas().size()) {
            JOptionPane.showMessageDialog(this, "Turno no válido");
            return;
        }

        Turno turno = partida.getRondas().get(turnIndex);
        if (turno == null) {
            JOptionPane.showMessageDialog(this, "Turno no existe");
            return;
        }

        txtTurno.setText(String.valueOf(turnIndex + 1));

        String nombreJugador = "IA";
        if (turno.getJugador() != null) {
            nombreJugador = turno.getJugador().getUsername();
        }
        lblJugador.setText("Turno de: " + nombreJugador);

        int puntos = 0;
        if (turno.getJugador() != null) {
            puntos = (turno.getJugador() == partida.getCreador()) ?
                    turno.getPuntuacionJ1() : turno.getPuntuacionJ2();
        }
        lblPuntos.setText("Puntos: " + puntos);

        cargarTableroHistorico(turno.getTableroTurno());
        cargarAtrilHistorico(turno);
    }

    /**
     * Renderiza el tablero histórico con las fichas y modificadores visuales.
     *
     * @param tablero Tablero correspondiente al turno actual.
     */
    private void cargarTableroHistorico(Tablero tablero) {
        panelTablero.removeAll();
        for (int x = 0; x < Tablero.FILAS; x++) {
            for (int y = 0; y < Tablero.COLUMNAS; y++) {
                JPanel celda = new JPanel(new BorderLayout());
                celda.setPreferredSize(new Dimension(45, 45));

                try {
                    Tablero.TipoModificador m = tablero.getTipoModificador(x, y);
                    celda.setBackground(obtenerColorModificador(m));

                    Ficha f = tablero.getFicha(x, y);
                    if (f != null) {
                        JLabel lbl = new JLabel(f.getLetra(), SwingConstants.CENTER);
                        lbl.setFont(LABEL_FONT);
                        celda.add(lbl);
                    }
                } catch (CoordenadaFueraDeRangoException e) {
                    celda.setBackground(new Color(245, 245, 220));
                }
                panelTablero.add(celda);
            }
        }
        panelTablero.revalidate();
        panelTablero.repaint();
    }

    /**
     * Devuelve el color asociado a un modificador del tablero.
     *
     * @param mod Tipo de modificador (doble/triple tanto de letra/palabra).
     * @return Color correspondiente al modificador. Blanco si no hay modificador.
     */
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

    /**
     * Carga el atril del jugador contrario durante el turno histórico.
     *
     * @param turno Turno actual del que se obtienen los datos del atril.
     */
    private void cargarAtrilHistorico(Turno turno) {
        panelAtril.removeAll();
        if (turno == null || gestorDePartida == null || partida == null) return;

        Map<Ficha, Integer>[] atriles = gestorDePartida.getAtrilesTurno(turno);
        if (atriles == null || atriles.length < 2) return;

        Perfil creador = partida.getCreador();
        Perfil oponente = partida.getOponente();

        Perfil jugadorTurno = turno.getJugador();
        Perfil jugadorContrario;

        if (jugadorTurno == null) { // Turno de la IA
            // Asumimos que el creador es el humano (si la IA es el oponente)
            jugadorContrario = creador;
        } else { // Turno de jugador humano
            jugadorContrario = jugadorTurno.equals(creador) ? oponente : creador;
        }

        //if (jugadorContrario == null) return;

        Map<Ficha, Integer> atrilJugador = gestorDePartida.obtenerAtrilJugador(partida, jugadorContrario);
        int indiceAtrilContrario = atriles[0].equals(atrilJugador) ? 0 : 1;

        // Validar y mostrar
        if (indiceAtrilContrario >= 0 && indiceAtrilContrario < atriles.length) {
            Map<Ficha, Integer> atrilMostrar = atriles[indiceAtrilContrario];
            atrilMostrar.forEach((ficha, cantidad) -> {
                for (int i = 0; i < cantidad; i++) {
                    JButton btn = new JButton(ficha.getLetra());
                    btn.setFont(LABEL_FONT);
                    btn.setPreferredSize(new Dimension(50, 50));
                    btn.setBackground(new Color(200, 200, 200));
                    btn.setEnabled(false);
                    panelAtril.add(btn);
                }
            });
        }
        panelAtril.revalidate();
        panelAtril.repaint();
    }

    /**
     * Navega al turno anterior si es posible.
     * Actualiza la interfaz automáticamente.
     */
    private void mostrarTurnoPrevio() {
        if (currentTurnIndex > 0) {
            currentTurnIndex--;
            cargarTurno(currentTurnIndex);
        }
    }

    /**
     * Navega al siguiente turno si es posible.
     * Actualiza la interfaz automáticamente.
     */
    private void mostrarTurnoSiguiente() {
        if (currentTurnIndex < partida.getRondas().size() - 2) {
            currentTurnIndex++;
            cargarTurno(currentTurnIndex);
        }
    }

    /**
     * Salta a un turno específico basado en la entrada del usuario.
     * Valida que el número esté dentro del rango permitido.
     *
     * @param input Texto ingresado por el usuario (debe ser un número entero).
     * @throws NumberFormatException Si la entrada no es un número válido.
     */
    private void saltarATurno(String input) {
        try {
            int totalTurnos = partida.getRondas().size()-1;
            if (totalTurnos == 0) {
                JOptionPane.showMessageDialog(this, "No hay turnos guardados");
                return;
            }
            int target = Integer.parseInt(input) - 1;
            if (target >= 0 && target < totalTurnos) {
                currentTurnIndex = target;
                cargarTurno(currentTurnIndex);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Turno fuera de rango (1-" + totalTurnos + ")");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido");
        }
    }
}