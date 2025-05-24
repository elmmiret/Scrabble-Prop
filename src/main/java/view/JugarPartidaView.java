package view;

import algorisme.Movimiento;
import gestordepartida.*;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JugarPartidaView extends JFrame {
    private static final int ANCHO = 1400;
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
    private JButton btnPedirPista;
    JPanel infoPanel;
    JPanel mainPanel;
    private Map<Point, Ficha> colocacionesTemporales = new HashMap<>();
    private JPanel cambiarFichasPanel;
    private JButton btnConfirmarCambio;
    private JButton btnCancelarCambio;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    JPanel botonesPanel;
    private Map<Ficha, Integer> fichasEnUso = new HashMap<>(); // Fichas colocadas temporalmente
    GestorDeView gestorDeView;
    private int pasarConsecutivos = 0;
    private boolean partidaFinalizada = false;
    GestorDePerfil gestorDePerfil;

    public JugarPartidaView(GestorDeView gestorDeView, Partida partida, GestorDePartida gestorDePartida, GestorDePerfil gestorDePerfil) {
        super("Jugar Partida");
        this.partida = partida;
        this.gestorDePartida = gestorDePartida;
        this.jugadorActual = partida.getRondas().get(partida.getRondas().size()-1).getJugador();
        this.atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual);
        this.gestorDeView = gestorDeView;
        this.gestorDePerfil = gestorDePerfil;
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
        if (partida.getModoPartida()==Partida.Modo.PvP)
        {
            lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
        }
        else //IA
        {
            if (jugadorActual == null) lblJugador = new JLabel("Turno de: IA");
            else lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
        }
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.BLACK);
        if (partida.getModoPartida()==Partida.Modo.PvP)
        {
            lblPuntos = new JLabel("Puntos: " + (jugadorActual.equals(partida.getCreador())
                    ? turnoActual.getPuntuacionJ1()
                    : turnoActual.getPuntuacionJ2()));
        }
        else //IA
        {
            if (jugadorActual == null) lblPuntos = new JLabel("Puntos: " + turnoActual.getPuntuacionJ2());
            else lblPuntos = new JLabel("Puntos: " + turnoActual.getPuntuacionJ1());
        }

        System.out.println("MILESTONE 1");
        lblPuntos.setFont(TITLE_FONT);
        JLabel lblPistas = new JLabel("Pistas restantes: " + obtenerPistasRestantes(turnoActual));
        lblPistas.setFont(LABEL_FONT);
        lblPuntos.setForeground(Color.BLACK);
        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);
        infoPanel.add(lblPistas);
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Panel del tablero
        panelTablero = new JPanel(new GridLayout(Tablero.FILAS, Tablero.COLUMNAS, 1, 1));
        panelTablero.setPreferredSize(new Dimension(
                Tablero.COLUMNAS * 45,
                Tablero.FILAS * 45
        ));
        cargarTablero();

        System.out.println("MILESTONE 1.1");

        mainPanel.add(new JScrollPane(panelTablero), BorderLayout.CENTER);

        // Panel lateral
        JPanel lateralPanel = new JPanel(new BorderLayout(10, 20));
        lateralPanel.setOpaque(false);

        // Atril
        panelAtril = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelAtril.setOpaque(false);
        cargarAtril();
        lateralPanel.add(panelAtril, BorderLayout.NORTH);

        System.out.println("MILESTONE 1.2");

        // Botones
        botonesPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        botonesPanel.setOpaque(false);
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        System.out.println("MILESTONE 1.2.1");
        addJugarButton(botonesPanel, "Confirmar", COLOR_VERDE, e -> confirmarColocacion());
        System.out.println("MILESTONE 1.2.2");
        addJugarButton(botonesPanel, "Cambiar Fichas", COLOR_AZUL, e -> cambiarFichas());
        System.out.println("MILESTONE 1.2.3");
        addJugarButton(botonesPanel, "Pedir Pista", COLOR_AZUL, e -> pedirPista());
        System.out.println("MILESTONE 1.2.4");
        addJugarButton(botonesPanel, "Pasar turno", COLOR_AZUL, e -> pasarTurno());
        System.out.println("MILESTONE 1.2.5");
        addJugarButton(botonesPanel, "Salir", COLOR_ROJO, e -> salirPartida());
        System.out.println("MILESTONE 1.2.6");

        // Inside the init() method, after creating the botonesPanel
        cambiarFichasPanel = new JPanel(new BorderLayout(10, 5));
        cambiarFichasPanel.setPreferredSize(new Dimension(200, 100)); // Altura máxima
        cambiarFichasPanel.setOpaque(false);
        cambiarFichasPanel.setVisible(false);

        System.out.println("MILESTONE 1.3");

        // Add checkboxes for tiles (dynamically populated later)
        JPanel checkboxPanel = new JPanel(new GridLayout(0, 1, 2, 2)); // Reducir espaciado entre filas
        checkboxPanel.setOpaque(false);
        cambiarFichasPanel.add(new JScrollPane(checkboxPanel), BorderLayout.CENTER);

        System.out.println("MILESTONE 2");

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
        System.out.println("MILESTONE 3");
    }

    private void pedirPista() {
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        if (jugadorActual != null) {
            // Verificar pistas disponibles
            int pistasDisponibles = turnoActual.getPistas(jugadorActual);
            if (pistasDisponibles <= 0) {
                JOptionPane.showMessageDialog(this, "No tienes pistas disponibles");
                return;
            }

            Movimiento mov = gestorDePartida.pedirPista(partida, jugadorActual);

            if (mov == null) {
                JOptionPane.showMessageDialog(this, "No hay movimientos posibles");
                return;
            }

            String mensaje = construirMensajePista(mov, partida.getDificultad());
            JOptionPane.showMessageDialog(this, mensaje, "Pista", JOptionPane.INFORMATION_MESSAGE);

            actualizarPanelInformacion();
        }
    }

    private String construirMensajePista(Movimiento mov, int dificultad) {
        StringBuilder sb = new StringBuilder();
        switch (dificultad) {
            case 1:
                char letra = (char) ('A' + mov.getFila());
                int columna = mov.getColumna() + 1;
                sb.append("Palabra y posición sugerida: ").append(letra).append(columna).append("\n");
                break;
            case 2:
                sb.append("Posible palabra: ");
                break;
            case 3:
                Collections.shuffle(mov.getPalabra());
                sb.append("Letras de posible palabra: ");
                break;
        }

        mov.getPalabra().forEach(letra -> sb.append(letra).append(" "));
        return sb.toString();
    }
    /**
    private void actualizarContadorPistas(Turno turno) {
        if (jugadorActual.equals(partida.getCreador())) {
            turno.setPistasRestantesJ1(turno.getPistasJ1());
        } else {
            turno.setPistasRestantesJ2(turno.getPistasJ2() - 1);
        }

    }
     */

    private void styleButton(JButton btn, Color color) {
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private void pasarTurno() {
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        if (partidaFinalizada) return;

        pasarConsecutivos++;
        if (pasarConsecutivos >= 2) {
            partida.getRondas().get(partida.getRondas().size() - 2).setTipoJugada(Turno.TipoJugada.finalizar);
            finalizarPartida("¡Dos pases consecutivos! Fin de la partida.");
            return;
        }

        revertirColocacionesTemporales(); // Limpiar antes de avanzar
        turnoActual.pasarTurno();
        turnoActual = partida.getRondas().get(partida.getRondas().size() - 1); // Get the new turn
        jugadorActual = turnoActual.getJugador(); // <-- Add this line to update the current player
        atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual); // Now uses the new player
        cargarTablero();
        cargarAtril();
        actualizarPanelInformacion();
        if(partida.getModoPartida() == Partida.Modo.PvIA) ejecutarTurnoIA();
    }

    private void finalizarPartida(String motivo) {
        partidaFinalizada = true;
        pasarConsecutivos = 0;

        // Obtener puntuaciones finales
        Turno ultimoTurno = partida.getRondas().getLast();
        int puntosJ1 = ultimoTurno.getPuntuacionJ1();
        int puntosJ2 = ultimoTurno.getPuntuacionJ2();

        // Determinar ganador
        String ganador = "";
        String perdedor = "";
        if (partida.getModoPartida() == Partida.Modo.PvP)
        {
            if (puntosJ1 > puntosJ2) {
                ganador = partida.getCreador().getUsername();
                perdedor = partida.getOponente().getUsername();
            }
            else if (puntosJ2 > puntosJ1) {
                ganador = partida.getOponente().getUsername();
                perdedor = partida.getCreador().getUsername();
            }
            else {
                ganador = "Empate";
                perdedor = "";
            }
        }
        else if (partida.getModoPartida() == Partida.Modo.PvIA)
        {
            if (puntosJ1 > puntosJ2)
            {
                ganador = partida.getCreador().getUsername();
                perdedor = "";
            }
            else
            {
                ganador = "";
                perdedor = "";
            }
        }

        // Mostrar diálogo con resultados
        String mensaje = String.format(
                "%s\nPuntos %s: %d\nPuntos %s: %d\nGanador: %s",
                motivo,
                partida.getCreador().getUsername(), puntosJ1,
                (partida.getModoPartida() == Partida.Modo.PvP)
                        ? partida.getOponente().getUsername() : "IA", puntosJ2,
                ganador
        );

        JOptionPane.showMessageDialog(this, mensaje, "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);

        // Actualizar estadísticas de perfiles
        actualizarEstadisticasPerfiles(ganador, perdedor, puntosJ1, puntosJ2);

        // Cerrar vista y volver al menú
        gestorDeView.volverMenuGestionPartida(this);
    }

    private void actualizarEstadisticasPerfiles(String ganador, String perdedor, int puntosJ1, int puntosJ2) {
        if (partida.getModoPartida() == Partida.Modo.PvP) {
            gestorDePerfil.incrementarPartidasJugadas(partida.getCreador().getUsername());
            gestorDePerfil.incrementarPartidasJugadas(partida.getOponente().getUsername());
            gestorDePerfil.incrementarPuntosJugador(ganador, ganador.equals(partida.getCreador().getUsername()) ? puntosJ1 : puntosJ2);
            gestorDePerfil.incrementarPuntosJugador(perdedor, perdedor.equals(partida.getCreador().getUsername()) ? puntosJ1 : puntosJ2);

            if (!ganador.equals("Empate")) {
                gestorDePerfil.incrementarPartidasGanadas(ganador);
                gestorDePerfil.incrementarPartidasPerdidas(perdedor);
            }
        } else {
            // Lógica para modo IA
        }
    }

    private void actualizarPanelInformacion() {
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        jugadorActual = turnoActual.getJugador();

        if (btnPedirPista != null) {
            boolean esTurnoHumano = jugadorActual != null;
            int pistasDisponibles = esTurnoHumano ?
                    obtenerPistasRestantes(turnoActual) : 0;

            btnPedirPista.setEnabled(esTurnoHumano && pistasDisponibles > 0);
            btnPedirPista.setToolTipText(!esTurnoHumano ?
                    "No disponible para IA" :
                    (pistasDisponibles > 0 ? "" : "Sin pistas disponibles"));
        }

        infoPanel.removeAll();

        // Nuevo layout con 3 filas
        infoPanel.setLayout(new GridLayout(3, 1, 10, 5));

        // Jugador y puntos
        if (partida.getModoPartida()==Partida.Modo.PvP)
        {
            lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
        }
        else //IA
        {
            if (jugadorActual == null) lblJugador = new JLabel("Turno de: IA");
            else lblJugador = new JLabel("Turno de: " + jugadorActual.getUsername());
        }
        lblJugador.setFont(TITLE_FONT);
        lblJugador.setForeground(Color.BLACK);

        if (partida.getModoPartida()==Partida.Modo.PvP)
        {
            lblPuntos = new JLabel("Puntos: " + (jugadorActual.equals(partida.getCreador())
                    ? turnoActual.getPuntuacionJ1()
                    : turnoActual.getPuntuacionJ2()));
        }
        else //IA
        {
            if (jugadorActual == null) lblPuntos = new JLabel("Puntos: " + turnoActual.getPuntuacionJ2());
            else lblPuntos = new JLabel("Puntos: " + turnoActual.getPuntuacionJ1());
        }

        lblPuntos.setFont(TITLE_FONT);
        lblPuntos.setForeground(Color.BLACK);

        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);


        // Contador de pistas
        JLabel lblPistas = new JLabel("Pistas restantes: " + obtenerPistasRestantes(turnoActual));
        lblPistas.setFont(LABEL_FONT);

        infoPanel.add(lblJugador);
        infoPanel.add(lblPuntos);
        infoPanel.add(lblPistas);

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private int obtenerPistasRestantes(Turno turno) {
        if (jugadorActual == null) {
            return 0;
        } else {
            return jugadorActual.equals(partida.getCreador()) ? turno.getPistasJ1() : turno.getPistasJ2();
        }
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

        if (text.equals("Pedir Pista")) {
            btnPedirPista = btn;
            boolean esTurnoHumano = jugadorActual != null;
            int pistasDisponibles = esTurnoHumano ?
                    obtenerPistasRestantes(partida.getRondas().getLast()) :
                    0;

            btn.setEnabled(esTurnoHumano && pistasDisponibles > 0);
            btn.setToolTipText(!esTurnoHumano ?
                    "No disponible para IA" :
                    (pistasDisponibles > 0 ? "" : "Sin pistas disponibles"));
        }
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

                    // Mantener las colocaciones temporales visibles
                    Point p = new Point(x, y);
                    if (colocacionesTemporales.containsKey(p)) {
                        Ficha f = colocacionesTemporales.get(p);
                        JLabel lbl = new JLabel(f.getLetra(), SwingConstants.CENTER);
                        lbl.setFont(LABEL_FONT);
                        celda.add(lbl);
                    }

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
                    Point punto = new Point(x, y);
                    Ficha fichaEnCelda = partida.getTablero().getFicha(x, y);

                    // Si hay una ficha temporal en esta celda
                    if (colocacionesTemporales.containsKey(punto)) {
                        Ficha f = colocacionesTemporales.get(punto);

                        // Devolver solo esta ficha
                        fichasEnUso.put(f, fichasEnUso.get(f) - 1);
                        if (fichasEnUso.get(f) == 0) fichasEnUso.remove(f);
                        colocacionesTemporales.remove(punto);

                        // Actualizar solo esta celda
                        celda.removeAll();
                        celda.revalidate();
                        celda.repaint();

                        // Actualizar el atril
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
            int totalEnAtril = entry.getValue();
            int enUso = fichasEnUso.getOrDefault(f, 0);

            for (int i = 0; i < totalEnAtril; i++) {
                JButton btnFicha = new JButton(f.getLetra());
                btnFicha.setFont(LABEL_FONT);
                btnFicha.setPreferredSize(new Dimension(50, 50));

                // Si está en uso, deshabilitar y oscurecer
                if (i < (totalEnAtril - enUso)) {
                    btnFicha.setBackground(Color.LIGHT_GRAY);
                    btnFicha.addActionListener(e -> seleccionarFicha(f));
                } else {
                    btnFicha.setBackground(new Color(100, 100, 100)); // Color oscuro
                    btnFicha.setEnabled(false);
                }

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
            // Verificar disponibilidad
            int disponibles = atrilActual.get(fichaSeleccionada) - fichasEnUso.getOrDefault(fichaSeleccionada, 0);
            if (disponibles > 0) {
                fichasEnUso.put(fichaSeleccionada, fichasEnUso.getOrDefault(fichaSeleccionada, 0) + 1);
                colocacionesTemporales.put(new Point(x, y), fichaSeleccionada);
                cargarAtril(); // Actualizar interfaz

                // Actualizar celda del tablero
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
    }

    private void confirmarColocacion() {
        if (colocacionesTemporales.isEmpty()) return;

        try {
            // 1. Validar orientación y continuidad
            List<Point> puntos = new ArrayList<>(colocacionesTemporales.keySet());
            boolean horizontal = puntos.stream().allMatch(p -> p.x == puntos.get(0).x);
            boolean vertical = puntos.stream().allMatch(p -> p.y == puntos.get(0).y);

            if (!horizontal && !vertical) {
                JOptionPane.showMessageDialog(this, "Las fichas deben formar una línea recta");
                return;
            }

            // 2. Ordenar las posiciones según la orientación
            puntos.sort((p1, p2) -> horizontal ? Integer.compare(p1.y, p2.y) : Integer.compare(p1.x, p2.x));

            // 3. Validar primera palabra debe pasar por el centro
            if (partida.getTablero().estaVacio()) {
                boolean centroCubierto = puntos.stream().anyMatch(p ->
                        p.x == Tablero.FILAS/2 && p.y == Tablero.COLUMNAS/2);

                if (!centroCubierto) {
                    JOptionPane.showMessageDialog(this, "La primera palabra debe pasar por el centro");
                    return;
                }
            }

            // 4. Construir la palabra completa (nuevas + existentes)
            Point start = findWordStart(puntos, horizontal);
            StringBuilder fullWord = new StringBuilder();
            List<Point> todasLasLetras = new ArrayList<>();

            if (horizontal) {
                for (int y = start.y; ; y++) {
                    Point p = new Point(start.x, y);
                    Ficha f = getFichaAtPosition(p.x, p.y);
                    if (f == null) break;
                    fullWord.append(f.getLetra());
                    todasLasLetras.add(p);
                }
            } else {
                for (int x = start.x; ; x++) {
                    Point p = new Point(x, start.y);
                    Ficha f = getFichaAtPosition(p.x, p.y);
                    if (f == null) break;
                    fullWord.append(f.getLetra());
                    todasLasLetras.add(p);
                }
            }

            // 5. Validar conexión con palabras existentes (excepto primera jugada)
            if (!partida.getTablero().estaVacio()) {
                boolean conectada = false;
                for (Point p : todasLasLetras) {
                    if (isAdyacenteAExistente(p.x, p.y)) {
                        conectada = true;
                        break;
                    }
                }
                if (!conectada) {
                    JOptionPane.showMessageDialog(this, "Debe conectar con palabras existentes");
                    return;
                }
            }

            // 5.1. Manejar comodines: Solicitar letras al usuario
            List<Integer> posicionesComodin = new ArrayList<>();
            for (int i = 0; i < fullWord.length(); i++) {
                if (fullWord.charAt(i) == '#') {
                    posicionesComodin.add(i);
                }
            }

            if (!posicionesComodin.isEmpty()) {
                for (int pos : posicionesComodin) {
                    String letraElegida = solicitarLetraComodin();
                    if (letraElegida == null) {
                        revertirColocacionesTemporales();
                        return; // Usuario canceló
                    }
                    fullWord.setCharAt(pos, letraElegida.charAt(0));

                    // Actualizar la ficha temporal para reflejar el cambio visual
                    Point p = todasLasLetras.get(pos);
                    Ficha fichaReal = new Ficha(letraElegida, partida.getPuntuacionFicha(letraElegida));
                    colocacionesTemporales.put(p, fichaReal);
                }
                cargarTablero(); // Actualizar vista con letras elegidas
            }

            // 6. Validar todas las palabras nuevas (horizontal + verticales)
            Set<String> palabrasValidadas = new HashSet<>();
            for (Point p : todasLasLetras) {
                // Palabra principal
                if (p.equals(start)) {
                    palabrasValidadas.add(fullWord.toString());
                }

                // Palabras cruzadas
                String palabraVertical = construirPalabraVertical(p);
                if (palabraVertical.length() > 1) {
                    palabrasValidadas.add(palabraVertical);
                }

                String palabraHorizontal = construirPalabraHorizontal(p);
                if (palabraHorizontal.length() > 1) {
                    palabrasValidadas.add(palabraHorizontal);
                }
            }

            // Validar todas las palabras con el DAWG
            for (String palabra : palabrasValidadas) {
                if (!partida.getDawg().existePalabra(palabra)) {
                    JOptionPane.showMessageDialog(this, "Palabra inválida: " + palabra);
                    revertirColocacionesTemporales();
                    return;
                }
            }

            // 7. Confirmar colocación
            Turno currentTurn = partida.getRondas().get(partida.getRondas().size() - 1);
            boolean isValid = currentTurn.colocarPalabra(
                    fullWord.toString(),
                    start.x,
                    start.y,
                    horizontal ? "horizontal" : "vertical"
            );

            if (isValid) {
                pasarConsecutivos = 0;
                if (atrilActual.isEmpty()) {
                    Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
                    turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                    turnoActual.pasarTurno();
                    finalizarPartida("¡Atril vacío! Fin de la partida.");
                    return;
                }
                Turno nuevoTurno = partida.getRondas().get(partida.getRondas().size() - 1);
                jugadorActual = nuevoTurno.getJugador(); // Actualizar jugador
                atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual);

                colocacionesTemporales.clear();
                fichasEnUso.clear();
                actualizarEstadoJuego();
                if(partida.getModoPartida() == Partida.Modo.PvIA) ejecutarTurnoIA();
            } else {
                revertirColocacionesTemporales();
            }

        } catch (Exception ex) {
            revertirColocacionesTemporales();
            ex.printStackTrace();
        }
        colocacionesTemporales.clear();
        fichasEnUso.clear();
        cargarAtril();
        cargarTablero();
    }

    private String solicitarLetraComodin() {
        Set<String> letrasValidas = partida.getMapaLetras().keySet();
        String[] opciones = letrasValidas.toArray(new String[0]);

        return (String) JOptionPane.showInputDialog(
                this,
                "Selecciona la letra para el comodín:",
                "Usar comodín",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                "A"
        );
    }

    private boolean isAdyacenteAExistente(int x, int y) throws CoordenadaFueraDeRangoException {
        int[][] direcciones = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] dir : direcciones) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < Tablero.FILAS && ny >= 0 && ny < Tablero.COLUMNAS) {
                if (partida.getTablero().getFicha(nx, ny) != null && !colocacionesTemporales.containsKey(new Point(nx, ny))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String construirPalabraVertical(Point p) throws CoordenadaFueraDeRangoException {
        StringBuilder sb = new StringBuilder();
        // Arriba (incluyendo la posición actual)
        for (int x = p.x; x >= 0; x--) {
            Ficha f = getFichaAtPosition(x, p.y);
            if (f == null) break;
            sb.insert(0, f.getLetra());
        }
        // Abajo (excluyendo la posición actual para evitar duplicados)
        for (int x = p.x + 1; x < Tablero.FILAS; x++) {
            Ficha f = getFichaAtPosition(x, p.y);
            if (f == null) break;
            sb.append(f.getLetra());
        }
        return sb.toString();
    }

    private String construirPalabraHorizontal(Point p) throws CoordenadaFueraDeRangoException {
        StringBuilder sb = new StringBuilder();
        // Izquierda
        for (int y = p.y; y >= 0; y--) {
            Ficha f = getFichaAtPosition(p.x, y);
            if (f == null) break;
            sb.insert(0, f.getLetra());
        }
        // Derecha
        for (int y = p.y + 1; y < Tablero.COLUMNAS; y++) {
            Ficha f = getFichaAtPosition(p.x, y);
            if (f == null) break;
            sb.append(f.getLetra());
        }
        return sb.toString();
    }

    private Ficha getFichaAtPosition(int x, int y) throws CoordenadaFueraDeRangoException {
        Point p = new Point(x, y);
        // Priorizar comodines ya sustituidos
        return colocacionesTemporales.containsKey(p) ?
                colocacionesTemporales.get(p) :
                partida.getTablero().getFicha(x, y);
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

    private void actualizarEstadoJuego() {
        cargarTablero();
        cargarAtril();
        actualizarPanelInformacion();
    }

    private void ejecutarTurnoIA() {
        Turno turnoIA = partida.getRondas().get(partida.getRondas().size() - 1);
        // Ejecutar lógica de la IA
        try {
            int dificultad = partida.getDificultad();
            turnoIA.jugarIA(dificultad);
            pasarConsecutivos = 0;
            if (atrilActual.isEmpty()) {
                finalizarPartida("¡Atril vacío! Fin de la partida.");
                return;
            }
            Turno nuevoTurno = partida.getRondas().get(partida.getRondas().size() - 1);
            jugadorActual = nuevoTurno.getJugador(); // Actualizar jugador
            atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual);;
            actualizarEstadoJuego();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void revertirColocacionesTemporales() {
        // Devolver todas las fichas temporales al atril
        colocacionesTemporales.forEach((pos, ficha) -> {
            fichasEnUso.put(ficha, fichasEnUso.getOrDefault(ficha, 0) - 1);
            if (fichasEnUso.get(ficha) <= 0) fichasEnUso.remove(ficha);
        });

        colocacionesTemporales.clear();
        cargarAtril();
        cargarTablero();
    }

    private void cambiarFichas() {
        revertirColocacionesTemporales();
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
        revertirColocacionesTemporales();
        List<String> fichasCambio = new ArrayList<>();
        for (JCheckBox check : checkBoxes) {
            if (check.isSelected()) {
                fichasCambio.add(check.getText());
            }
        }

        Turno turnoActual = partida.getRondas().get(partida.getRondas().size()-1);
        boolean exito = gestorDePartida.cambiarFichas(turnoActual, atrilActual, fichasCambio);

        if (exito) {
            pasarConsecutivos = 0;
            // Limpiar definitivamente después del cambio
            colocacionesTemporales.clear();
            fichasEnUso.clear();
            turnoActual = partida.getRondas().get(partida.getRondas().size() - 1); // Get the new turn
            jugadorActual = turnoActual.getJugador(); // <-- Add this line to update the current player
            atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual); // Now uses the new player
            cargarTablero();
            cargarAtril();

            actualizarPanelInformacion();
            if(partida.getModoPartida() == Partida.Modo.PvIA) ejecutarTurnoIA();

        } else {
            JOptionPane.showMessageDialog(this, "Error al cambiar fichas");
        }

        // Reset UI
        cancelarCambioFichas();
        cargarTablero();
        cargarAtril();
    }

    private void cancelarCambioFichas() {
        revertirColocacionesTemporales();
        botonesPanel.setVisible(true);
        cambiarFichasPanel.setVisible(false);
        checkBoxes.clear();
    }

    private void salirPartida() {
        gestorDeView.volverMenuGestionPartida(this);
    }

    private void cargarEstadoInicial() {
        if (partida.getModoPartida() == Partida.Modo.PvIA && jugadorActual == null) {
            ejecutarTurnoIA();
        }
    }

}