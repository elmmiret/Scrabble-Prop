package gestordepartida;

import java.awt.datatransfer.SystemFlavorMap;
import java.util.*;

import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import gestordeperfil.*;

/**
 * Clase controladora para la gestión interactiva de partidas de Scrabble.
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Menú de gestión de partidas (crear/cargar/eliminar/consultar)</li>
 *   <li>Autenticación de jugadores y recuperación de contraseñas</li>
 *   <li>Flujo completo de juego para modos PvP y PvIA</li>
 *   <li>Interacción con gestores de partidas y perfiles</li>
 * </ul>
 *
 * @author Albert Aulet Niubó
 * @author Paula Pérez Chia
 */
public class DriverPartida {

    /** Gestor principal de partidas */
    private static GestorDePartida gestor;

    /** Gestor de perfiles de jugadores */
    private static GestorDePerfil gestorPerfiles;

    /** Lector de entrada de consola */
    private static Scanner scanner;

    /** Contador de turnos pasados consecutivamente para finalizar partida */
    private int pasarPartidaSeguidos;

    /**
     * Construye un controlador de partidas con dependencias inyectadas.
     *
     * @param gdp Gestor de partidas principal
     * @param gestorp Gestor de perfiles de jugadores
     * @param scanner Entrada de datos configurada
     */
    public DriverPartida(GestorDePartida gdp, GestorDePerfil gestorp, Scanner scanner) {
        gestor = gdp;
        gestorPerfiles = gestorp;
        this.scanner = scanner;
        pasarPartidaSeguidos = 0;
    }

    /**
     * Gestiona el menú principal de operaciones con partidas.
     *
     * @throws CasillaOcupadaException Si se intenta colocar en casilla ocupada
     * @throws CoordenadaFueraDeRangoException Si se usan coordenadas inválidas
     */
    public void partidaManagement() throws CasillaOcupadaException, CoordenadaFueraDeRangoException{
        if (gestorPerfiles.hayJugadores()){
            Scanner scanner = new Scanner(System.in);
            boolean salir = false;
            while (!salir) {
                int opcion = leerEntero("Selección:\n1- Crear una nueva partida\n2- Cargar partida existente\n3- Ver repitición de partida\n4- Eliminar partida\n5- Consultar partidas\n6- Atrás\n\n");
                switch (opcion) {
                    case 1 -> crearNuevaPartida();
                    case 2 -> cargarPartidaExistente();
                    case 3 -> verRepeticionPartida();
                    case 4 -> eliminarPartida();
                    case 5 -> consultarPartidas();
                    case 6 -> salir = true;
                    default -> System.out.println("Opción inválida");
                }
            }
        }
        else System.out.println("\nNo hay ningún perfil en el sistema para jugar!");
    }

    /**
     * Crea una nueva partida con configuración interactiva.
     *
     * @throws CasillaOcupadaException Error en colocación inicial
     * @throws CoordenadaFueraDeRangoException Coordenadas fuera de rango
     */
    private void crearNuevaPartida() throws CasillaOcupadaException, CoordenadaFueraDeRangoException{
        System.out.println("\n=== CREAR PARTIDA ===");

        int id = leerEntero("ID de partida: ");
        if (gestor.obtenerPartida(id) != null) {
            System.out.println("¡Ya existe una partida con este ID!\n");
            return;
        }

        String nombre = leerCadena("Nombre de la partida: ");
        int idioma = leerEntero("Idioma (1-CAT 2-CAST 3-ENG): ");
        Partida.Idioma idiom = switch (idioma) {
            case 1 -> Partida.Idioma.CAT;
            case 2 -> Partida.Idioma.CAST;
            case 3 -> Partida.Idioma.ENG;
            default -> throw new IllegalArgumentException("Idioma inválido");
        };

        System.out.println("\n=== AUTENTIFICACIÓN JUGADOR 1 ===");
        Perfil jugador = autenticarUsuario();
        if (jugador == null) {
            return;
        }

        int modo = leerEntero("Modo (1-PvP 2-PvIA): ");
        Partida partida;
        if (modo == 1) {
            System.out.println("\n=== AUTENTIFICACIÓN JUGADOR 2 ===");
            Perfil oponente = autenticarUsuario();
            if (oponente == null) {
                return;
            }
            else if (oponente == jugador) {
                System.out.println("\nNo puedes jugar contra ti mismo!\n");
                return;
            }
            partida = gestor.crearPartida(id, nombre, idiom, jugador, Partida.Modo.PvP, oponente, 0);
            jugar(partida);
        } else {
            int dificultad = leerEntero("Dificultad IA (1-3): ");
            partida = gestor.crearPartida(id, nombre, idiom, jugador, Partida.Modo.PvIA, null, dificultad);
            System.out.println("Partida creada correctamente!");
            jugarIA(partida);
        }

        System.out.print("\n");
    }

    /**
     * Autentica un usuario mediante credenciales.
     *
     * @return Perfil autenticado o null si se cancela
     */
    private Perfil autenticarUsuario() {
        while (true) {
            String username = leerCadena("Username: ");
            if (!gestorPerfiles.existeJugador(username)) {
                System.out.println("No existe ningun usuario con este username: " + username);
                continue;
            }
            String contraseña = leerCadena("Password del usuario: ");
            if (gestorPerfiles.esPasswordCorrecta(username, contraseña)) {
                return gestorPerfiles.getPerfil(username);
            }

            int opcion = leerEntero("Password incorrecta\n1- Reintentar\n2- Restablecer Password\n3- Salir\n");
            switch (opcion) {
                case 1 -> {
                    continue;
                }
                case 2 -> {
                    if (manejarRecuperacionContraseña(username)) continue;
                    else return null;
                }
                case 3 -> {
                    return null;
                }
            }
        }
    }

    /**
     * Gestiona el proceso de recuperación de contraseña de un usuario.
     *
     * @param username Nombre de usuario que solicita la recuperación
     * @return true si la recuperación fue exitosa, false si se superaron los intentos
     */
    private boolean manejarRecuperacionContraseña (String username) {
        String frase = leerCadena("\nIntroduce la frase de recuperación: ");
        int intentos = 3;
        while (!gestor.verificarFraseRecuperacion(username, frase)) {
            System.out.println("Frase incorrecta. Intentos restantes: " + --intentos);
            if (intentos == 0) return false;

            frase = leerCadena("\nIntroduce la frase de recuperación: ");
        }

        String nuevaContraseña = leerCadena("Introduce la nueva password: ");
        while (!gestorPerfiles.esPasswordSegura(nuevaContraseña)) {
            System.out.println("La password no cumple los requisitos de seguridad. Los requisitos son: \n- Mínimo 8 caracteres\n- Al menos 1 mayúscula\n- Al menos 1 número\n");
            nuevaContraseña = leerCadena("Introduce la nueva password: ");
        }
        gestorPerfiles.cambiarPassword(username, nuevaContraseña);
        System.out.println("¡Password actualizada correctamente!");
        return true;
    }

    /**
     * Carga una partida existente desde el sistema.
     *
     * @throws CasillaOcupadaException Si se detectan colocaciones inválidas al cargar
     * @throws CoordenadaFueraDeRangoException Si hay coordenadas corruptas en el estado guardado
     */
    private void cargarPartidaExistente() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        int id = leerEntero("ID de partida: ");
        Partida partida = gestor.obtenerPartida(id);

        if (partida == null || !gestor.existePartidaJugador(jugador, id)) {
            System.out.println("Partida no encontrada");
            return;
        }

        System.out.println("\n=== PARTIDA CARGADA ===");
        if (partida.getModoPartida() == Partida.Modo.PvP) {
            jugar(partida);
        } else {
            jugarIA(partida);
        }
    }

    private void verRepeticionPartida() throws CoordenadaFueraDeRangoException {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        System.out.println("¿De qué partida quieres ver la repetición?");
        consultarPartidas(jugador);

        int id = leerEntero("ID de la partida: ");
        Partida partida = gestor.obtenerPartida(id);

        if (partida == null || !gestor.existePartidaJugador(jugador, id)) {
            System.out.println("Partida no encontrada");
            return;
        }

        System.out.println("\n=== REPETICIÓN PARTIDA " + id + " ===");
        boolean salir = false;
        int i = 0;
        int maxTurnos = gestor.getMaxTurnos(partida);
        if (maxTurnos < 0) {
            System.out.println("No hay turnos para mostrar.");
            return;
        }

        String usernameCreador = partida.getCreador().getUsername();
        Partida.Modo modo = partida.getModoPartida();

        while (!salir) {
            Turno turnoRepe = gestor.getTurno(partida, i);
            Perfil jugadorActivo = turnoRepe.getJugador();
            String username;
            if (modo == Partida.Modo.PvP) {
                username = jugadorActivo != null ? jugadorActivo.getUsername() : "?";
            } else {
                username = (jugadorActivo == null) ? "IA" : usernameCreador;
            }
            System.out.println("=== TURNO " + (i + 1) + ", le toca jugar a " + username);
            gestor.obtenerRepresentacionTablero(turnoRepe.getTableroTurno());

            Map<Ficha, Integer>[] atriles = gestor.getAtrilesTurno(turnoRepe);
            Map<Ficha, Integer> atrilJugadorSiguiente = atriles[1];

            if (username.equals(usernameCreador)) {
                mostrarAtril(atrilJugadorSiguiente, 1);
            } else {
                mostrarAtril(atrilJugadorSiguiente, 1);
            }



            boolean accioValida = false;
            while (!accioValida) {
                int opcio = leerEntero("Acciones:\n1- Siguiente turno\n2- Turno anterior\n3- Ver turno específico\n4- Salir\n");
                switch (opcio) {
                    case 1 -> {
                        if (i >= maxTurnos-1) {
                            System.out.println("Estás en el último turno.");
                        } else {
                            i++;
                            accioValida = true;
                        }
                    }
                    case 2 -> {
                        if (i <= 0) {
                            System.out.println("Estás en el primer turno.");
                        } else {
                            i--;
                            accioValida = true;
                        }
                    }
                    case 3 -> {
                        int numTorn = leerEntero("Introduce el número de turno: ");
                        if (numTorn <= 0) {
                            System.out.println("Tiene que ser mayor de 0");
                        } else if (!gestor.isTurnoValido(partida, numTorn)) {
                            System.out.println("Turno fuera de rango (1-" + maxTurnos + ")");
                        } else {
                            i = numTorn - 1;
                            accioValida = true;
                        }
                    }
                    case 4 -> {
                        salir = true;
                        accioValida = true;
                    }
                    default -> System.out.println("Opción inválida");
                }
            }
        }

        System.out.println("Repetició finalitzada");
    }


    /**
     * Maneja el flujo de juego contra otro jugador humano.
     *
     * @param partida Partida en curso
     */
    private void jugar(Partida partida) {
        boolean enJuego = true;
        while (enJuego) {
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
            Perfil jugador = turnoActual.getJugador();
            System.out.println("\n=== TURNO DE " + jugador.getUsername() + " ===");
            try {
                //gestor.obtenerRepresentacionTablero(partida.getTablero());
                gestor.obtenerRepresentacionTablero(turnoActual.getTablero());
            } catch (CoordenadaFueraDeRangoException e) {
                throw new RuntimeException(e);
            }
            Map <Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
            if (!atril.isEmpty()) {
                mostrarAtril(atril);
                int num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                boolean accionValida = false;
                while (!accionValida) {
                    switch(num) {
                        case 1:
                            accionValida = colocarPalabra(partida, jugador);
                            pasarPartidaSeguidos = 0;
                            break;
                        case 2:
                            accionValida = cambiarFichas(partida, jugador);
                            pasarPartidaSeguidos = 0;
                            break;
                        case 3:
                            turnoActual.pasarTurno();
                            ++pasarPartidaSeguidos;
                            accionValida = true;
                            break;
                        case 4:
                            System.out.println("¡Has salido de la partida!");
                            accionValida = true;
                            enJuego = false;
                            break;
                        default:
                            num = leerEntero("Opción inválida");
                            break;

                    }
                    if (!accionValida && (num == 1 || num == 2)) {
                        mostrarAtril(atril);
                        num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                    }
                    if (pasarPartidaSeguidos >= 2)
                    {
                        enJuego = false;
                        System.out.println("Habéis pasado dos veces seguidas de turno. Fin de la partida!\n");
                        mostrarResultadosFinales(partida);
                        if (turnoActual.getPuntuacionJ1() > turnoActual.getPuntuacionJ2())
                        {
                            System.out.printf("\nGANADOR: %s\n", partida.getCreador().getUsername());
                            gestorPerfiles.incrementarPartidasGanadas(partida.getCreador().getUsername());
                            gestorPerfiles.incrementarPartidasPerdidas(partida.getOponente().getUsername());
                        }
                        else if (turnoActual.getPuntuacionJ2() > turnoActual.getPuntuacionJ1())
                        {
                            System.out.printf("\nGANADOR: %s\n", partida.getOponente().getUsername());
                            gestorPerfiles.incrementarPartidasGanadas(partida.getOponente().getUsername());
                            gestorPerfiles.incrementarPartidasPerdidas(partida.getCreador().getUsername());
                        }

                        gestorPerfiles.incrementarPuntosJugador(partida.getCreador().getUsername(), turnoActual.getPuntuacionJ1());
                        gestorPerfiles.incrementarPuntosJugador(partida.getOponente().getUsername(), turnoActual.getPuntuacionJ2());

                        gestorPerfiles.incrementarPartidasJugadas(partida.getCreador().getUsername());
                        gestorPerfiles.incrementarPartidasJugadas(partida.getOponente().getUsername());

                        return;
                    }
                }

            }
            else {
                turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                enJuego = false;
                System.out.println("No tienes fichas en el atril. Fin de la partida!\n");
                mostrarResultadosFinales(partida);
                if (turnoActual.getPuntuacionJ1() > turnoActual.getPuntuacionJ2())
                {
                    System.out.printf("\nGANADOR: %s\n", partida.getCreador().getUsername());
                    gestorPerfiles.incrementarPartidasGanadas(partida.getCreador().getUsername());
                    gestorPerfiles.incrementarPartidasPerdidas(partida.getOponente().getUsername());
                }
                else if (turnoActual.getPuntuacionJ2() > turnoActual.getPuntuacionJ1())
                {
                    System.out.printf("\nGANADOR: %s\n", partida.getOponente().getUsername());
                    gestorPerfiles.incrementarPartidasGanadas(partida.getOponente().getUsername());
                    gestorPerfiles.incrementarPartidasPerdidas(partida.getCreador().getUsername());
                }

                gestorPerfiles.incrementarPuntosJugador(partida.getCreador().getUsername(), turnoActual.getPuntuacionJ1());
                gestorPerfiles.incrementarPuntosJugador(partida.getOponente().getUsername(), turnoActual.getPuntuacionJ2());

                gestorPerfiles.incrementarPartidasJugadas(partida.getCreador().getUsername());
                gestorPerfiles.incrementarPartidasJugadas(partida.getOponente().getUsername());


                return;
            }

            if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                mostrarResultadosFinales(partida);
            }
            mostrarResultadosFinales(partida);
        }
    }

    /**
     * Gestiona el flujo de juego contra la IA.
     *
     * @param partida Partida en curso
     * @throws CasillaOcupadaException Si la IA intenta colocar en casilla ocupada
     * @throws CoordenadaFueraDeRangoException Si la IA usa coordenadas inválidas
     */
    private void jugarIA(Partida partida) throws CasillaOcupadaException, CoordenadaFueraDeRangoException {
        boolean enJuego = true;
        while (enJuego) {
            Turno turnoActual = partida.getRondas().getLast();
            Perfil jugador = turnoActual.getJugador();
            if (jugador != partida.getCreador()) { // es la IA
                System.out.println("\n=== TURNO DE LA IA ===");
                try {
                    gestor.obtenerRepresentacionTablero(turnoActual.getTablero());

                } catch (CoordenadaFueraDeRangoException e) {
                    throw new RuntimeException(e);
                }
                Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
                if (!atril.isEmpty()) {
                    mostrarAtril(atril);
                    turnoActual.jugarIA(partida.getDificultad());
                } else {
                    turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                }

                if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                    mostrarResultadosFinales(partida);
                }
            } else { // es la persona
                System.out.println("\n=== TURNO DE " + jugador.getUsername() + " ===");
                try {
                    gestor.obtenerRepresentacionTablero(partida.getTablero());
                } catch (CoordenadaFueraDeRangoException e) {
                    throw new RuntimeException(e);
                }
                Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
                if (!atril.isEmpty()) {
                    mostrarAtril(atril);
                    int num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                    boolean accionValida = false;
                    while (!accionValida) {
                        switch (num) {
                            case 1:
                                accionValida = colocarPalabra(partida, jugador);
                                pasarPartidaSeguidos = 0;
                                break;
                            case 2:
                                accionValida = cambiarFichas(partida, jugador);
                                pasarPartidaSeguidos = 0;
                                break;
                            case 3:
                                turnoActual.pasarTurno();
                                accionValida = true;
                                ++pasarPartidaSeguidos;
                                break;
                            case 4:
                                System.out.println("¡Has salido de la partida!");
                                accionValida = true;
                                enJuego = false;
                                break;
                            default:
                                num = leerEntero("Opción inválida");
                                break;

                        }
                        if (!accionValida && (num == 1 || num == 2)) {
                            mostrarAtril(atril);
                            num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                        }
                        else if (pasarPartidaSeguidos >= 2)
                        {
                            enJuego = false;
                            System.out.println("Habéis pasado dos veces seguidas de turno. Fin de la partida!\n");
                            mostrarResultadosFinales(partida);
                            if (turnoActual.getPuntuacionJ1() > turnoActual.getPuntuacionJ2())
                            {
                                System.out.printf("\nGANADOR: %s\n", partida.getCreador().getUsername());
                                gestorPerfiles.incrementarPartidasGanadas(partida.getCreador().getUsername());
                            }
                            else if (turnoActual.getPuntuacionJ2() > turnoActual.getPuntuacionJ1())
                            {
                                System.out.printf("\nGANADOR: IA");
                                gestorPerfiles.incrementarPartidasPerdidas(partida.getCreador().getUsername());
                            }

                            gestorPerfiles.incrementarPuntosJugador(partida.getCreador().getUsername(), turnoActual.getPuntuacionJ1());

                            gestorPerfiles.incrementarPartidasJugadas(partida.getCreador().getUsername());

                            return;
                        }
                    }

                    // printeo

                    for (Turno turno : partida.getRondas()) {
                        System.out.print(turno.getTipoJugada() + "   |    ");
                    }
                }
                else {
                    turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                    enJuego = false;
                    System.out.println("No tienes fichas en el atril. Fin de la partida!\n");
                    mostrarResultadosFinales(partida);
                    if (turnoActual.getPuntuacionJ1() > turnoActual.getPuntuacionJ2())
                    {
                        System.out.printf("\nGANADOR: %s\n", partida.getCreador().getUsername());
                        gestorPerfiles.incrementarPartidasGanadas(partida.getCreador().getUsername());
                    }
                    else if (turnoActual.getPuntuacionJ2() > turnoActual.getPuntuacionJ1())
                    {
                        System.out.printf("\nGANADOR: IA");
                        gestorPerfiles.incrementarPartidasPerdidas(partida.getCreador().getUsername());
                    }

                    gestorPerfiles.incrementarPuntosJugador(partida.getCreador().getUsername(), turnoActual.getPuntuacionJ1());

                    gestorPerfiles.incrementarPartidasJugadas(partida.getCreador().getUsername());

                    return;
                }
                if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                    mostrarResultadosFinales(partida);
                }
                mostrarResultadosFinales(partida);
            }
        }
    }

    /**
     * Gestiona la colocación de una palabra en el tablero durante un turno.
     *
     * @param partida Partida en curso
     * @param jugador Perfil del jugador activo
     * @return true si la colocación fue exitosa, false en caso contrario
     */
    private boolean colocarPalabra(Partida partida, Perfil jugador) {
        String palabra = leerCadena("Palabra: ").toUpperCase();
        String x_string = leerCadena("Fila (A, B...): ").toUpperCase();
        int x = x_string.charAt(0) - 'A';
        int y = leerEntero("Columna: ");
        String orientacion = leerCadena("Orientación (V/H): ").toUpperCase();
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);
        try {
            boolean exito = gestor.colocarPalabra(turnoActual, palabra, x, y-1, orientacion.equals("V") ? "vertical" : "horizontal");
            System.out.println(exito ? "¡Palabra colocada!" : "Movimiento inválido");
            return exito;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gestiona el intercambio de fichas del jugador durante su turno.
     *
     * @param partida Partida en curso
     * @param jugador Perfil del jugador activo
     * @return true si el intercambio se realizó correctamente
     */
    private boolean cambiarFichas(Partida partida, Perfil jugador) {
        Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
        System.out.print("Letras a cambiar (separadas por espacio): ");
        String line = scanner.nextLine().trim();
        List<String> letras = line.isEmpty()
                ? Collections.emptyList()
                : List.of(line.split("\\s+"));

        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);

        boolean exito = gestor.cambiarFichas(turnoActual, atril, letras);

        System.out.println(exito ? "Fichas cambiadas" : "No se pudo cambiar las fichas seleccionadas");

        return exito;
    }

    /**
     * Muestra el listado completo de partidas asociadas a un jugador.
     */
    private void consultarPartidas() {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador);
        System.out.println("\n=== TUS PARTIDAS ===");
        partidas.forEach(p -> {
            System.out.println("ID: " + p.getId() + "  Nombre: " + p.getNombre() + "  Modo: " + p.getModoPartida() + (p.getModoPartida().equals(Partida.Modo.PvP) ? "  Oponente: " + p.getOponente().getUsername() : "  Dificultad: " + p.getDificultad()));

        });
    }

    private void consultarPartidas(Perfil jugador) {
        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador);
        System.out.println("\n=== TUS PARTIDAS ===");
        partidas.forEach(p -> {
            System.out.println("ID: " + p.getId() + "  Nombre: " + p.getNombre() + "  Modo: " + p.getModoPartida() + (p.getModoPartida().equals(Partida.Modo.PvP) ? "  Oponente: " + p.getOponente().getUsername() : "  Dificultad: " + p.getDificultad()));

        });
    }

    /**
     * Elimina permanentemente una partida del sistema.
     *
     * @return void Muestra mensajes de éxito/error por consola
     */
    private void eliminarPartida() {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;
        int id = leerEntero("ID de partida a eliminar: ");
        if (gestor.eliminarPartida(id)) {
            System.out.println("Partida eliminada");
        } else {
            System.out.println("No se encontró la partida");
        }
    }

    /**
     * Utilidad para lectura de cadenas con mensaje personalizado.
     *
     * @param mensaje Texto a mostrar antes de la entrada
     * @return Cadena introducida por el usuario (sin trim)
     */
    private String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Utilidad para lectura de enteros con validación.
     *
     * @param mensaje Texto a mostrar antes de la entrada
     * @return Entero válido introducido por el usuario
     */
    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Introduce un número.");
            }
        }
    }


    private void mostrarAtril(Map<Ficha, Integer> atril) {
        mostrarAtril(atril, 0);
    }

    /**
     * Muestra visualmente las fichas disponibles en el atril.
     *
     * @param atril Mapa de fichas con sus cantidades
     */
    private void mostrarAtril(Map<Ficha, Integer> atril, int num) {
        if (num == 0) {
            System.out.println("\n=== TUS FICHAS ===");
        } else if (num == 1){
            System.out.println("\n=== FICHAS PRÓXIMO JUGADOR ===");

        }
        atril.forEach((f, c) -> {
            for (int i = 0; i < c; i++) {
                System.out.print(f.getLetra() + " ");
            }
        });
        System.out.println("\n==================");
    }

    /**
     * Presenta los resultados finales de una partida concluida.
     *
     * @param partida Partida finalizada
     */
    private void mostrarResultadosFinales(Partida partida) {
        System.out.println("\n=== PUNTOS ===");
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        System.out.println(partida.getCreador().getUsername() + ": " +
                turnoActual.getPuntuacionJ1());
        System.out.println((partida.getModoPartida() == Partida.Modo.PvP ?
                partida.getOponente().getUsername() : "IA") + ": " +
                turnoActual.getPuntuacionJ2());
    }
}