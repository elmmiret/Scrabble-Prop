package ctrldomini;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import gestordeperfil.*;

/**
 * Driver para probar las funcionalidades de la clase Partida y GestorDePartida.
 * @author Albert Aulet Niubó
 * @author Paula Pérez Chia
 */
public class DriverPartida {
    private static GestorDePartida gestor;
    private static GestorDePerfil gestorPerfiles;
    private static Scanner scanner;


    public DriverPartida(GestorDePartida gdp, GestorDePerfil gestorp, Scanner scanner) {
        gestor = gdp;
        gestorPerfiles = gestorp;
        this.scanner = scanner;
    }

    public void partidaManagement() throws CasillaOcupadaException, CoordenadaFueraDeRangoException{
        if (gestorPerfiles.hayJugadores()){
            Scanner scanner = new Scanner(System.in);
            boolean salir = false;
            while (!salir) {
                int opcion = leerEntero("Selección:\n1- Crear una nueva partida\n2- Cargar partida existente\n3- Eliminar partida\n4- Consultar partidas\n5- Atrás\n\n");
                switch (opcion) {
                    case 1 -> crearNuevaPartida();
                    case 2 -> cargarPartidaExistente();
                    case 3 -> eliminarPartida();
                    case 4 -> consultarPartidas();
                    case 5 -> salir = true;
                    default -> System.out.println("Opción inválida");
                }
            }
        }
        else System.out.println("\nNo hay ningún perfil en el sistema para jugar!");
    }

    private void crearNuevaPartida() {
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
        } else {
            int dificultad = leerEntero("Dificultad IA (1-3): ");
            partida = gestor.crearPartida(id, nombre, idiom, jugador, Partida.Modo.PvIA, null, dificultad);
        }

        System.out.println("\nPartida creada correctamente! ID: " + partida.getId());
        System.out.print("\n");
    }

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

    private void jugar(Partida partida) {
        boolean enJuego = true;
        while (enJuego) {
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
            Perfil jugador = turnoActual.getJugador();
            System.out.println("\n=== TURNO DE " + jugador.getUsername() + " ===");
            try {
                gestor.obtenerRepresentacionTablero(partida.getTablero());
            } catch (CoordenadaFueraDeRangoException e) {
                throw new RuntimeException(e);
            }
            Map <Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
            if (atril.size() > 0) {
                mostrarAtril(atril);
                int num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                boolean accionValida = false;
                while (!accionValida) {
                    switch(num) {
                        case 1:
                            accionValida = colocarPalabra(partida, jugador);
                            break;
                        case 2:
                            accionValida = cambiarFichas(partida, jugador);
                            break;
                        case 3:
                            turnoActual.pasarTurno();
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
                        num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
                    } else if (partida.getRondas().size() > 1 && turnoActual.getTipoJugada() == Turno.TipoJugada.pasar){
                        Turno turnoAnterior = partida.getRondas().get(partida.getRondas().size() - 2);
                        if (turnoAnterior.getTipoJugada() == Turno.TipoJugada.pasar) {
                            turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                            enJuego = false;
                        }
                    }
                }
            } else {
                turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                enJuego = false;
            }

            if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                mostrarResultadosFinales(partida);
            }
        }
    }

    private void jugarIA(Partida partida) throws CasillaOcupadaException, CoordenadaFueraDeRangoException {
        boolean enJuego = true;
        while (enJuego) {
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
            Perfil jugador = turnoActual.getJugador();
            if (jugador != partida.getCreador()) {
                if (jugador != null) System.out.println("\n=== TURNO DE " + jugador.getUsername() + " ===");
                else System.out.println("\n=== TURNO DE LA IA ===");
                try {
                    gestor.obtenerRepresentacionTablero(partida.getTablero());
                } catch (CoordenadaFueraDeRangoException e) {
                    throw new RuntimeException(e);
                }
                Map <Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
                if (atril.size() > 0) {
                    mostrarAtril(atril);
                    int num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n" );
                    boolean accionValida = false;
                    while (!accionValida) {
                        switch (num) {
                            case 1:
                                accionValida = colocarPalabra(partida, jugador);
                                break;
                            case 2:
                                accionValida = cambiarFichas(partida, jugador);
                                break;
                            case 3:
                                turnoActual.pasarTurno();
                                accionValida = true;
                                break;
                            case 4:
                                System.out.println("¡Has salido de la partida!" );
                                accionValida = true;
                                enJuego = false;
                                break;
                            default:
                                num = leerEntero("Opción inválida" );
                                break;

                        }
                        if (!accionValida && (num == 1 || num == 2)) {
                            num = leerEntero("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n" );
                        } else if (partida.getRondas().size() > 1 && turnoActual.getTipoJugada() == Turno.TipoJugada.pasar) {
                            Turno turnoAnterior = partida.getRondas().get(partida.getRondas().size() - 2);
                            if (turnoAnterior.getTipoJugada() == Turno.TipoJugada.pasar) {
                                turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                                enJuego = false;
                            }
                        }
                    }
                }
                else {
                    turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                    enJuego = false;
                }

                if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                    mostrarResultadosFinales(partida);
                }
            } else {
                turnoActual.jugarIA();
            }
        }
    }


    private boolean colocarPalabra(Partida partida, Perfil jugador) {
        String palabra = leerCadena("Palabra: ").toUpperCase();
        String x_string = leerCadena("Fila (A, B...): ");
        int x = x_string.charAt(0) - 'A';
        int y = leerEntero("Columna: ");
        String orientacion = leerCadena("Orientación (V/H): ").toUpperCase();
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);
        try {
            boolean exito = gestor.colocarPalabra(turnoActual, palabra, x, y, orientacion.equals("V") ? "vertical" : "horizontal");
            System.out.println(exito ? "¡Palabra colocada!" : "Movimiento inválido");
            return exito;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    private boolean cambiarFichas(Partida partida, Perfil jugador) {
        Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(partida, jugador);
        System.out.print("Letras a cambiar (separadas por espacio): ");
        List<String> letras = List.of(scanner.nextLine().split(" "));
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() -1);

        boolean exito = gestor.cambiarFichas(turnoActual, atril, letras);

        System.out.println(exito ? "Fichas cambiadas" : "No se pudo cambiar las fichas seleccionadas");

        return exito;
    }

    private void consultarPartidas() {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador);
        System.out.println("\n=== TUS PARTIDAS ===");
        partidas.forEach(p -> {
            System.out.println("ID: " + p.getId() + "  Nombre: " + p.getNombre() + "  Modo: " + p.getModoPartida() + (p.getModoPartida().equals(Partida.Modo.PvP) ? "  Oponente:  " + p.getOponente() : "  Dificultad: " + p.getDificultad()));

        });
    }

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

    private String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

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
        System.out.println("\n=== TUS FICHAS ===");
        atril.forEach((f, c) -> {
            for (int i = 0; i < c; i++) {
                System.out.print(f.getLetra() + " ");
            }
        });
        System.out.println("\n==================");
    }

    private void mostrarResultadosFinales(Partida partida) {
        System.out.println("\n=== RESULTADOS FINALES ===");
        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
        System.out.println(partida.getCreador().getUsername() + ": " +
                turnoActual.getPuntuacionJ1());
        System.out.println((partida.getModoPartida() == Partida.Modo.PvP ?
                partida.getOponente().getUsername() : "IA") + ": " +
                turnoActual.getPuntuacionJ2());
    }
}