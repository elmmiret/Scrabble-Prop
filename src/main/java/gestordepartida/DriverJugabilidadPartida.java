package gestordepartida;

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
public class DriverJugabilidadPartida {

    /** Gestor principal de partidas */
    private static GestorDePartida gestor;

    /** Gestor de perfiles de jugadores */
    private static GestorDePerfil gestorPerfiles;

    /** Lector de entrada de consola */
    private static HerramientasConsola consola;


    /** Contador de turnos pasados consecutivamente para finalizar partida */
    private int pasarPartidaSeguidos;

    /**
     * Construye un controlador de partidas con dependencias inyectadas.
     *
     * @param gdp Gestor de partidas principal
     * @param gestorp Gestor de perfiles de jugadores
     * @param scanner Entrada de datos configurada
     */
    public DriverJugabilidadPartida(GestorDePartida gdp, GestorDePerfil gestorp, Scanner scanner) {
        gestor = gdp;
        gestorPerfiles = gestorp;
        consola = new HerramientasConsola(scanner);
        pasarPartidaSeguidos = 0;
    }

    /**
     * Autentica un usuario mediante credenciales.
     *
     * @return Perfil autenticado o null si se cancela
     */
    private Perfil autenticarUsuario() {
        while (true) {
            String username = consola.leerCadena("Username: ");
            if (!gestorPerfiles.existeJugador(username)) {
                System.out.println("No existe ningun usuario con este username: " + username);
                continue;
            }
            String contraseña = consola.leerCadena("Password del usuario: ");
            if (gestorPerfiles.esPasswordCorrecta(username, contraseña)) {
                return gestorPerfiles.getPerfil(username);
            }

            int opcion = consola.leerEntero("Password incorrecta\n1- Reintentar\n2- Restablecer Password\n3- Salir\n");
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
        String frase = consola.leerCadena("\nIntroduce la frase de recuperación: ");
        int intentos = 3;
        while (!gestor.verificarFraseRecuperacion(username, frase)) {
            System.out.println("Frase incorrecta. Intentos restantes: " + --intentos);
            if (intentos == 0) return false;

            frase = consola.leerCadena("\nIntroduce la frase de recuperación: ");
        }

        String nuevaContraseña = consola.leerCadena("Introduce la nueva password: ");
        while (!gestorPerfiles.esPasswordSegura(nuevaContraseña)) {
            System.out.println("La password no cumple los requisitos de seguridad. Los requisitos son: \n- Mínimo 8 caracteres\n- Al menos 1 mayúscula\n- Al menos 1 número\n");
            nuevaContraseña = consola.leerCadena("Introduce la nueva password: ");
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
    public void cargarPartida() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        int id = consola.leerEntero("ID de partida: ");
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
            if (atril.size() > 0) {
                mostrarAtril(atril);
                int num = consola.leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
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
                            num = consola.leerEntero("Opción inválida");
                            break;

                    }
                    if (!accionValida && (num == 1 || num == 2)) {
                        mostrarAtril(atril);
                        num = consola.leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
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
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
            Perfil jugador = turnoActual.getJugador();
            if (jugador != partida.getCreador()) { // es la IA
                System.out.println("\n=== TURNO DE LA IA ===");
                try {
//                    gestor.obtenerRepresentacionTablero(partida.getTablero());
                    gestor.obtenerRepresentacionTablero(turnoActual.getTablero());

                } catch (CoordenadaFueraDeRangoException e) {
                    throw new RuntimeException(e);
                }
                Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
                if (atril.size() > 0) {
                    mostrarAtril(atril);
                    turnoActual.jugarIA();
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
                if (atril.size() > 0) {
                    mostrarAtril(atril);
                    int num = consola.leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
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
                                num = consola.leerEntero("Opción inválida");
                                break;

                        }
                        if (!accionValida && (num == 1 || num == 2)) {
                            mostrarAtril(atril);
                            num = consola.leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
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
    }

    /**
     * Gestiona la colocación de una palabra en el tablero durante un turno.
     *
     * @param partida Partida en curso
     * @param jugador Perfil del jugador activo
     * @return true si la colocación fue exitosa, false en caso contrario
     */
    private boolean colocarPalabra(Partida partida, Perfil jugador) {
        String palabra = consola.leerCadena("Palabra: ").toUpperCase();
        String x_string = consola.leerCadena("Fila (A, B...): ").toUpperCase();
        int x = x_string.charAt(0) - 'A';
        int y = consola.leerEntero("Columna: ");
        String orientacion = consola.leerCadena("Orientación (V/H): ").toUpperCase();
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);
        try {
            boolean exito = gestor.colocarPalabra(turnoActual, palabra, x, y - 1, orientacion.equals("V") ? "vertical" : "horizontal");
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
        String line = consola.leerCadena("Letras a cambiar (separadas por espacio): ").trim();
        List<String> letras = line.isEmpty()
                ? Collections.emptyList()
                : List.of(line.split("\\s+"));

        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);

        boolean exito = gestor.cambiarFichas(turnoActual, atril, letras);

        System.out.println(exito ? "Fichas cambiadas" : "No se pudo cambiar las fichas seleccionadas");

        return exito;
    }

    /**
     * Muestra visualmente las fichas disponibles en el atril.
     *
     * @param atril Mapa de fichas con sus cantidades
     */
    private void mostrarAtril(Map<Ficha, Integer> atril) {
        System.out.println("\n=== TUS FICHAS ===");
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