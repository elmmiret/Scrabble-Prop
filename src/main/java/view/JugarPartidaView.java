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

/**
 * Clase que representa la interfaz gráfica para jugar una partida de Scrabble.
 * Gestiona todos los elementos visuales y la interacción del usuario durante el juego,
 * incluyendo el tablero, el atril de fichas, las acciones del turno y la comunicación
 * con los gestores de partida y perfiles. Coordina la lógica de colocación de fichas,
 * validación de movimientos, cambio de turnos y finalización de partidas.
 *
 * @author Marc Ribas Acon
 */

public class JugarPartidaView extends JFrame {
    /** Ancho predeterminado de la ventana */
    private static final int ANCHO = 1400;
    /** Alto predeterminado de la ventana */
    private static final int ALTO = 1000;
    /** Color azul utilizado en botones y elementos de la interfaz */
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    /** Color rojo utilizado para botones críticos como salir */
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    /** Color verde utilizado para acciones de confirmación */
    private final Color COLOR_VERDE = new Color(50, 200, 100);
    /** Color azul cielo utilizado para el multiplicador de doble tanto de letra*/
    private static final Color COLOR_DOBLE_LETRA = new Color(173, 216, 230);
    /** Color azul eléctrico utilizado para el multiplicador de triple tanto de letra*/
    private static final Color COLOR_TRIPLE_LETRA = new Color(65, 105, 225);
    /** Color naranja utilizado para el multiplicador de doble tanto de palabra*/
    private static final Color COLOR_DOBLE_PALABRA = new Color(255, 165, 0);
    /** Color rojo utilizado para el multiplicador de triple tanto de palabra*/
    private static final Color COLOR_TRIPLE_PALABRA = new Color(220, 20, 60);
    /** Color dorado utilizado para el centro del mapa*/
    private static final Color COLOR_CENTRO = new Color(234, 200, 106); // Dorado
    /** Fuente utilizada en botones principales */
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    /** Fuente utilizada en títulos y encabezados */
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    /** Fuente utilizada en etiquetas y texto general */
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    /** Gestor de dominio relacionado con operaciones de partida */
    private GestorDePartida gestorDePartida;
    /** Instancia de la partida en curso */
    private Partida partida;
    /** Perfil del jugador activo en el turno actual */
    private Perfil jugadorActual;
    /** Mapa de fichas disponibles en el atril del jugador actual */
    private Map<Ficha, Integer> atrilActual;
    /** Ficha seleccionada por el usuario para colocar */
    private Ficha fichaSeleccionada;
    /** Panel gráfico que representa el tablero de juego */
    private JPanel panelTablero;
    /** Panel gráfico que muestra las fichas del atril */
    private JPanel panelAtril;
    /** Etiqueta para mostrar el jugador actual */
    private JLabel lblJugador;
    /** Etiqueta para mostrar la puntuación actual */
    private JLabel lblPuntos;
    /** Botón para solicitar pistas estratégicas */
    private JButton btnPedirPista;
    /** Panel que contiene información del jugador y puntuación */
    private JPanel infoPanel;
    /** Panel principal que organiza todos los componentes */
    private JPanel mainPanel;
    /** Mapa temporal de fichas colocadas durante un turno */
    private Map<Point, Ficha> colocacionesTemporales = new HashMap<>();
    /** Panel para la interfaz de cambio de fichas */
    private JPanel cambiarFichasPanel;
    /** Botón para confirmar el cambio de fichas */
    private JButton btnConfirmarCambio;
    /** Botón para cancelar el cambio de fichas */
    private JButton btnCancelarCambio;
    /** Lista de checkboxes para seleccionar fichas a cambiar */
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    /** Panel que contiene los botones de acciones principales */
    private JPanel botonesPanel;
    /** Mapa de fichas utilizadas durante el turno actual */
    private Map<Ficha, Integer> fichasEnUso = new HashMap<>();
    /** Gestor de vistas para navegación entre pantallas */
    private GestorDeView gestorDeView;
    /** Contador de turnos pasados consecutivamente */
    private int pasarConsecutivos = 0;
    /** Indicador de si la partida ha finalizado */
    private boolean partidaFinalizada = false;
    /** Gestor de perfiles para actualizar estadísticas */
    private GestorDePerfil gestorDePerfil;
    /** Mapa para almacenar los comodines usados en el tablero para poder revertir su colocación correctamente*/
    private Map<Point, Ficha> comodinesOriginales = new HashMap<>();

    /**
     * Constructora que inicializa la vista de juego con una partida existente.
     *
     * @param gestorDeView      Gestor de vistas para navegación entre pantallas
     * @param partida           Partida a jugar
     * @param gestorDePartida   Gestor de lógica de partida
     * @param gestorDePerfil    Gestor de perfiles para actualización de estadísticas
     */
    public JugarPartidaView(GestorDeView gestorDeView, Partida partida, GestorDePartida gestorDePartida, GestorDePerfil gestorDePerfil) {
        super("Jugar Partida");
        this.partida = partida;
        this.gestorDePartida = gestorDePartida;
        this.jugadorActual = partida.getRondas().get(partida.getRondas().size()-1).getJugador();
        this.atrilActual = gestorDePartida.obtenerAtrilJugador(partida, jugadorActual);
        this.gestorDeView = gestorDeView;
        this.gestorDePerfil = gestorDePerfil;
        System.out.println("MILESTONE 1");
        init();
        System.out.println("MILESTONE 2");
        cargarEstadoInicial();
        System.out.println("MILESTONE 3");
    }

    /**
     * Configura los componentes gráficos principales de la interfaz:
     * - Panel de información con jugador actual y puntuación
     * - Tablero de juego interactivo
     * - Atril con fichas disponibles
     * - Botones de control del turno
     * - Sistema de capas para animación de fondo
     */
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
        infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
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

        JPanel atrilLeyendaPanel = new JPanel();
        atrilLeyendaPanel.setLayout(new BoxLayout(atrilLeyendaPanel, BoxLayout.Y_AXIS));
        atrilLeyendaPanel.setOpaque(false);

// Atril
        panelAtril = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelAtril.setOpaque(false);
        cargarAtril();
        atrilLeyendaPanel.add(panelAtril); // Solo agregar aquí

// Leyenda
        atrilLeyendaPanel.add(crearPanelLeyenda());

// Agregar el contenedor completo al lateralPanel
        lateralPanel.add(atrilLeyendaPanel, BorderLayout.NORTH);


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


    /**
     * Función para crear el panel de Java Swing para almacenar la
     * leyenda de los multiplicadores
     *
     * @return El panel que contiene la leyenda de los multiplicadores
     */

    private JPanel crearPanelLeyenda() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1, 5, 5)); // Una columna, múltiples filas
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.setOpaque(false);

        // Obtener textos según idioma
        String[] textos = obtenerTextosLeyenda();

        // Añadir elementos a la leyenda
        panel.add(crearItemLeyenda(COLOR_DOBLE_LETRA, textos[0]));
        panel.add(crearItemLeyenda(COLOR_TRIPLE_LETRA, textos[1]));
        panel.add(crearItemLeyenda(COLOR_DOBLE_PALABRA, textos[2]));
        panel.add(crearItemLeyenda(COLOR_TRIPLE_PALABRA, textos[3]));
        panel.add(crearItemLeyenda(COLOR_CENTRO, textos[4]));

        return panel;
    }

    /**
     * Función para crear la casilla y la descripción de la leyenda de multiplicadores
     * @param color Color del multiplicador
     * @param texto Descripción del multiplicador
     * @return JPanel con los elementos de la leyenda
     */
    private JPanel crearItemLeyenda(Color color, String texto) {
        JPanel panelItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelItem.setOpaque(false);

        // Cuadrado de color
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(color);
        colorPanel.setPreferredSize(new Dimension(20, 20));
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Texto descriptivo
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panelItem.add(colorPanel);
        panelItem.add(label);

        return panelItem;
    }

    /**
     * Función para obtener los textos que representan a cada
     * color de la leyenda de multiplicadores
     *
     * @return El texto que contiene la leyenda del multiplicador
     */

    private String[] obtenerTextosLeyenda() {
        Partida.Idioma idioma = partida.getIdioma();
        switch (idioma) {
            case CAT:
                return new String[] {
                        "Doble tant de lletra",
                        "Triple tant de lletra",
                        "Doble tant de paraula",
                        "Triple tant de paraula",
                        "Centre"
                };
            case CAST:
                return new String[] {
                        "Doble tanto de letra",
                        "Triple tanto de letra",
                        "Doble tanto de palabra",
                        "Triple tanto de palabra",
                        "Centro"
                };
            case ENG:
                return new String[] {
                        "Double letter score",
                        "Triple letter score",
                        "Double word score",
                        "Triple word score",
                        "Center"
                };
            default:
                return new String[5];
        }
    }

    /**
     * Solicita una pista estratégica al gestor de partida:
     * - Reduce el contador de pistas disponibles
     * - Muestra sugerencia adaptada a la dificultad
     * - Actualiza la interfaz con nueva información
     */
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

    /**
     * Construye el mensaje de pista estratégica según la dificultad del juego:
     * - Dificultad 1: Muestra posición sugerida
     * - Dificultad 2: Muestra palabra sugerida
     *
     * @param mov Movimiento sugerido por el algoritmo
     * @param dificultad Nivel de dificultad actual de la partida
     * @return Mensaje formateado con la información de la pista
     */
    private String construirMensajePista(Movimiento mov, int dificultad) {
        StringBuilder sb = new StringBuilder();
        sb.append("Posible palabra: ");

        mov.getPalabra().forEach(letra -> sb.append(letra).append(" "));
        return sb.toString();
    }

    /**
     * Estiliza un botón con colores y fuentes predeterminados.
     *
     * @param btn Botón a personalizar
     * @param color Color de fondo del botón
     */
    private void styleButton(JButton btn, Color color) {
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    /**
     * Maneja la lógica para pasar el turno al siguiente jugador:
     * - Incrementa el contador de pases consecutivos
     * - Finaliza la partida si hay dos pases seguidos
     * - Actualiza el jugador actual y recarga los componentes visuales
     * - Ejecuta el turno de la IA si corresponde
     */
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

    /**
     * Finaliza la partida y muestra los resultados:
     * - Calcula puntuaciones finales
     * - Determina ganador según modo de juego
     * - Actualiza estadísticas de perfiles
     * - Vuelve al menú principal
     *
     * @param motivo Descripción textual del motivo de finalización
     */
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

    /**
     * Actualiza las estadísticas de los perfiles participantes al finalizar la partida:
     * - Incrementa partidas jugadas/ganadas/perdidas
     * - Actualiza puntuaciones en el ranking
     * - Maneja casos especiales para partidas contra IA
     *
     * @param ganador Usuario del jugador ganador
     * @param perdedor Usuario del jugador perdedor (vacío en modo IA)
     * @param puntosJ1 Puntuación final del jugador creador
     * @param puntosJ2 Puntuación final del oponente/IA
     */
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

    /**
     * Actualiza los componentes de información en tiempo real:
     * - Jugador actual
     * - Puntuación
     * - Contador de pistas restantes
     */
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

    /**
     * Obtiene el número de pistas restantes para el jugador actual.
     *
     * @param turno Turno actual del juego
     * @return Cantidad de pistas disponibles (0 si es turno de IA)
     */
    private int obtenerPistasRestantes(Turno turno) {
        if (jugadorActual == null) {
            return 0;
        } else {
            return jugadorActual.equals(partida.getCreador()) ? turno.getPistasJ1() : turno.getPistasJ2();
        }
    }

    /**
     * Crea un botón personalizado con efectos visuales para jugar la partida
     *
     * @param panel   Panel contenedor del botón
     * @param text    Texto del botón
     * @param color   Color base del botón
     * @param action  Acción a ejecutar al hacer clic
     */
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

    /**
     * Carga y actualiza la representación visual del tablero de juego.
     * Incluye celdas con modificadores de puntuación y fichas colocadas.
     */
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

    /**
     * Crea una celda del tablero con su modificador gráfico correspondiente:
     * - Configura colores según tipo de casilla
     * - Añade listeners para interacción con fichas
     * - Muestra fichas temporales o permanentes
     *
     * @param x Coordenada X en el tablero
     * @param y Coordenada Y en el tablero
     * @return Panel configurado que representa una celda del tablero
     */
    private JPanel crearCeldaTablero(int x, int y) {
        JPanel celda = new JPanel(new BorderLayout());
        celda.setPreferredSize(new Dimension(40, 40));
        try {
            Tablero.TipoModificador mod = partida.getTablero().getTipoModificador(x, y);
            if (x == Tablero.FILAS/2 && y == Tablero.COLUMNAS/2) {
                celda.setBackground(COLOR_CENTRO);
            }
            else
            {
                Color colorFondo = obtenerColorModificador(mod);
                celda.setBackground(colorFondo);
            }

            Ficha ficha = partida.getTablero().getFicha(x, y);
            if (ficha != null) {
                JLabel lbl = new JLabel("<html>" + formatearFicha(ficha) + "</html>", SwingConstants.CENTER);
                lbl.setFont(LABEL_FONT);
                celda.add(lbl);
            }

        } catch (CoordenadaFueraDeRangoException e) {
            celda.setBackground(Color.WHITE);
        }

        Point p = new Point(x, y);
        if (colocacionesTemporales.containsKey(p)) {
            Ficha f = colocacionesTemporales.get(p);
            JLabel lbl = new JLabel("<html>" + formatearFicha(f) + "</html>", SwingConstants.CENTER);
            lbl.setFont(LABEL_FONT);
            celda.add(lbl);
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

    /**
     * Determina el color de fondo para una casilla según su modificador.
     *
     * @param mod Tipo de modificador de la casilla
     * @return Color correspondiente al modificador (blanco si no tiene)
     */
    private Color obtenerColorModificador(Tablero.TipoModificador mod) {
        if (mod == null) return new Color(245, 245, 220); // Beige claro para casillas normales

        switch (mod) {
            case dobleTantoDeLetra: return COLOR_DOBLE_LETRA;
            case tripleTantoDeLetra: return COLOR_TRIPLE_LETRA;
            case dobleTantoDePalabra: return COLOR_DOBLE_PALABRA;
            case tripleTantoDePalabra: return COLOR_TRIPLE_PALABRA;
            default: return new Color(245, 245, 220);
        }
    }

    /**
     * Actualiza la visualización del atril del jugador, mostrando:
     * - Fichas disponibles en gris claro
     * - Fichas en uso en tono oscuro
     * - Ficha seleccionada resaltada en amarillo
     */
    private void cargarAtril() {
        panelAtril.removeAll();
        for (Map.Entry<Ficha, Integer> entry : atrilActual.entrySet()) {
            Ficha f = entry.getKey();
            int totalEnAtril = entry.getValue();
            int enUso = fichasEnUso.getOrDefault(f, 0);

            for (int i = 0; i < totalEnAtril; i++) {
                JButton btnFicha = new JButton("<html>" + formatearFicha(f) + "</html>");
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

    /**
     * Formatea las fichas de manera que se muestre su puntuación en la esquina
     * superior derecha
     *
     * @param ficha Ficha a formatear
     * @return String en el formato deseado
     */

    private String formatearFicha(Ficha ficha) {
        return ficha.getLetra() + " <small><sup>" + ficha.getPuntuacion() + "</sup></small>";
    }

    /**
     * Selecciona una ficha del atril para colocación:
     * - Resalta visualmente la ficha seleccionada
     * - Deselecciona otras fichas
     *
     * @param ficha Ficha seleccionada por el usuario
     */
    private void seleccionarFicha(Ficha ficha) {
        fichaSeleccionada = ficha;
        Arrays.stream(panelAtril.getComponents())
                .forEach(c -> ((JButton) c).setBackground(Color.LIGHT_GRAY));

        Arrays.stream(panelAtril.getComponents())
                .filter(c -> ((JButton) c).getText().equals(ficha.getLetra()))
                .findFirst()
                .ifPresent(c -> ((JButton) c).setBackground(Color.YELLOW));
    }

    /**
     * Maneja la colocación temporal de una ficha en el tablero.
     * Actualiza las estructuras de estado y la interfaz gráfica.
     *
     * @param x Coordenada X en el tablero
     * @param y Coordenada Y en el tablero
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
    private void colocarFichaTemporal(int x, int y) throws CoordenadaFueraDeRangoException {
        if (partida.getTablero().getFicha(x, y) == null && fichaSeleccionada != null) {
            int disponibles = atrilActual.get(fichaSeleccionada) - fichasEnUso.getOrDefault(fichaSeleccionada, 0);
            if (disponibles > 0) {
                // Guardar el comodín original si es necesario
                Ficha fichaParaUsar = fichaSeleccionada;
                if (fichaSeleccionada.getLetra().equals("#")) {
                    // Crear una copia del comodín para usar
                    fichaParaUsar = new Ficha("#", fichaSeleccionada.getPuntuacion());
                    comodinesOriginales.put(new Point(x, y), fichaSeleccionada);
                } else {
                    comodinesOriginales.remove(new Point(x, y));
                }

                fichasEnUso.put(fichaSeleccionada, fichasEnUso.getOrDefault(fichaSeleccionada, 0) + 1);
                colocacionesTemporales.put(new Point(x, y), fichaParaUsar);
                cargarAtril();

                // Actualizar celda
                int index = x * Tablero.COLUMNAS + y;
                JPanel celda = (JPanel) panelTablero.getComponent(index);
                celda.removeAll();
                JLabel lbl = new JLabel("<html>" + formatearFicha(fichaParaUsar) + "</html>", SwingConstants.CENTER);
                lbl.setFont(LABEL_FONT);
                celda.add(lbl);
                celda.revalidate();
                celda.repaint();
                fichaSeleccionada = null;
            }
        }
    }

    /**
     * Valida y confirma la colocación de fichas en el tablero:
     * 1. Verifica alineación horizontal/vertical
     * 2. Valida primera palabra en centro
     * 3. Comprueba conexión con palabras existentes
     * 4. Valida palabras con el diccionario
     * 5. Actualiza estado del juego y puntuaciones
     * 6. Gestiona turnos siguientes o finalización
     */
    private void confirmarColocacion() {
        if (colocacionesTemporales.isEmpty()) return;

        try {
            List<Point> puntos = new ArrayList<>(colocacionesTemporales.keySet());
            boolean horizontal;
            boolean vertical = false;

            if (puntos.size() == 1) {
                // Caso especial: solo una ficha colocada
                Point p = puntos.get(0);
                int x = p.x;
                int y = p.y;

                // Verificar fichas adyacentes en el tablero (no temporales)
                boolean tieneIzquierda = (y > 0 && partida.getTablero().getFicha(x, y-1) != null);
                boolean tieneDerecha = (y < Tablero.COLUMNAS-1 && partida.getTablero().getFicha(x, y+1) != null);
                boolean tieneArriba = (x > 0 && partida.getTablero().getFicha(x-1, y) != null);
                boolean tieneAbajo = (x < Tablero.FILAS-1 && partida.getTablero().getFicha(x+1, y) != null);

                // Determinar orientación basada en adyacentes
                if ((tieneIzquierda || tieneDerecha) && !(tieneArriba || tieneAbajo)) {
                    horizontal = true;
                }
                else if ((tieneArriba || tieneAbajo) && !(tieneIzquierda || tieneDerecha)) {
                    horizontal = false;
                    vertical = true;
                }
                else {
                    // Caso ambiguo (adyacentes en ambas direcciones o ninguno)
                    horizontal = tieneIzquierda || tieneDerecha;
                    vertical = tieneArriba || tieneAbajo;
                }
            }
            else {
                // Lógica original para múltiples fichas
                horizontal = puntos.stream().allMatch(point -> point.x == puntos.get(0).x);
                vertical = puntos.stream().allMatch(point -> point.y == puntos.get(0).y);
            }

            // Validar orientación
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
                for (int y = start.y; y < Tablero.COLUMNAS; y++) {
                    Point p = new Point(start.x, y);
                    Ficha f = getFichaAtPosition(p.x, p.y);
                    if (f == null) break;
                    fullWord.append(f.getLetra());
                    todasLasLetras.add(p);
                }
            } else {
                for (int x = start.x; x < Tablero.FILAS; x++) {
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
                comodinesOriginales.clear();
                colocacionesTemporales.clear();
                fichasEnUso.clear();
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

    /**
     * Muestra un diálogo para seleccionar letra cuando se usa un comodín.
     *
     * @return Letra elegida por el usuario, o null si cancela
     */
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

    /**
     * Verifica si una posición del tablero es adyacente a fichas existentes.
     *
     * @param x Coordenada X a verificar
     * @param y Coordenada Y a verificar
     * @return true si la posición tiene fichas vecinas, false en caso contrario
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
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

    /**
     * Construye una palabra vertical a partir de una posición dada en el tablero.
     *
     * @param p Punto de inicio para construir la palabra
     * @return Palabra formada en dirección vertical
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
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

    /**
     * Construye una palabra horizontal a partir de una posición dada en el tablero.
     *
     * @param p Punto de inicio para construir la palabra
     * @return Palabra formada en dirección horizontal
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
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

    /**
     * Obtiene la ficha en una posición específica del tablero, priorizando las colocaciones temporales.
     *
     * @param x Coordenada X en el tablero
     * @param y Coordenada Y en el tablero
     * @return Ficha en la posición especificada, o null si está vacía
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
    private Ficha getFichaAtPosition(int x, int y) throws CoordenadaFueraDeRangoException {
        Point p = new Point(x, y);
        // Priorizar comodines ya sustituidos
        return colocacionesTemporales.containsKey(p) ?
                colocacionesTemporales.get(p) :
                partida.getTablero().getFicha(x, y);
    }

    /**
     * Encuentra el punto de inicio real de una palabra basado en fichas adyacentes.
     *
     * @param points Lista de posiciones de fichas colocadas
     * @param isHorizontal Indica si la búsqueda es en dirección horizontal
     * @return Punto de inicio de la palabra
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
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

    /**
     * Actualiza todos los componentes visuales después de un cambio de estado:
     * - Tablero de juego
     * - Atril de fichas
     * - Información del jugador
     */
    private void actualizarEstadoJuego() {
        cargarTablero();
        cargarAtril();
        actualizarPanelInformacion();
    }

    /**
     * Ejecuta el turno de la IA cuando corresponde:
     * - Utiliza el algoritmo según dificultad
     * - Realiza movimiento óptimo o cambia fichas
     * - Actualiza la interfaz automáticamente
     */
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

    /**
     * Restaura las fichas temporales al atril y limpia las estructuras de estado.
     */
    private void revertirColocacionesTemporales() {
        // Devolver todas las fichas temporales al atril
        for (Map.Entry<Point, Ficha> entry : colocacionesTemporales.entrySet()) {
            Point pos = entry.getKey();
            Ficha ficha = entry.getValue();

            // Si es un comodín transformado, usar el original
            if (comodinesOriginales.containsKey(pos)) {
                Ficha comodinOriginal = comodinesOriginales.get(pos);
                fichasEnUso.put(comodinOriginal, fichasEnUso.getOrDefault(comodinOriginal, 0) - 1);
                if (fichasEnUso.get(comodinOriginal) <= 0) {
                    fichasEnUso.remove(comodinOriginal);
                }
            }
            // Ficha normal
            else {
                fichasEnUso.put(ficha, fichasEnUso.getOrDefault(ficha, 0) - 1);
                if (fichasEnUso.get(ficha) <= 0) {
                    fichasEnUso.remove(ficha);
                }
            }
        }

        colocacionesTemporales.clear();
        comodinesOriginales.clear();
        cargarAtril();
        cargarTablero();
    }

    /**
     * Muestra la interfaz para cambiar fichas del atril:
     * - Checkboxes para selección de fichas a cambiar
     * - Botones de confirmación/cancelación
     * - Lógica de reposición desde la bolsa
     */
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
            JCheckBox check = new JCheckBox(ficha.getLetra());  // Solo la letra, sin formato
            check.putClientProperty("ficha", ficha);  // Almacenar objeto Ficha
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

    /**
     * Confirma el cambio de fichas seleccionadas:
     * - Procesa las fichas marcadas para cambio
     * - Comunica con el gestor de partida para realizar el cambio
     * - Actualiza el atril con nuevas fichas
     * - Ejecuta turno de IA si corresponde
     */
    private void confirmarCambioFichas() {
        revertirColocacionesTemporales();
        List<String> fichasCambio = new ArrayList<>();
        for (JCheckBox check : checkBoxes) {
            if (check.isSelected()) {
                Ficha f = (Ficha) check.getClientProperty("ficha");
                fichasCambio.add(f.getLetra());  // Obtener letra del objeto
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

    /**
     * Cancela el proceso de cambio de fichas:
     * - Restaura la interfaz a su estado normal
     * - Limpia las selecciones temporales
     * - Oculta el panel de cambio de fichas
     */
    private void cancelarCambioFichas() {
        revertirColocacionesTemporales();
        botonesPanel.setVisible(true);
        cambiarFichasPanel.setVisible(false);
        checkBoxes.clear();
        comodinesOriginales.clear();
    }

    /**
     * Vuelve al menú de gestión de partidas cerrando la vista actual.
     */
    private void salirPartida() {
        gestorDeView.volverMenuGestionPartida(this);
    }

    /**
     * Configura el estado inicial de la partida, incluyendo el primer turno de IA si corresponde.
     */
    private void cargarEstadoInicial() {
        if (partida.getModoPartida() == Partida.Modo.PvIA && jugadorActual == null) {
            ejecutarTurnoIA();
        }
    }

}