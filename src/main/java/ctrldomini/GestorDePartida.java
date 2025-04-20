package ctrldomini;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import gestordeperfil.GestorDePerfil;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;

/**
 * Gestiona la creación, eliminación, consulta y ejecución de partidas de Scrabble.
 * @author Albert Aulet Niubó
 */
public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    private GestorDePerfil gestorDePerfil;
    private Scanner scanner;

    public GestorDePartida(GestorDePerfil gDP) {
        scanner = new Scanner(System.in);
        partidas = new HashMap<>();
        gestorDePerfil = gDP;
    }

    private boolean manejarRecuperacionContraseña(String username) {
        System.out.println("\nIntroduce la frase de recuperación:");
        String frase = scanner.nextLine();

        int intentos = 3;
        while (!gestorDePerfil.esFraseRecuperacionCorrecta(username, frase)) {
            System.out.println("Frase incorrecta. Intentos restantes: " + --intentos);
            if (intentos == 0) return false;

            System.out.println("Introduce la frase de recuperación:");
            frase = scanner.nextLine();
        }

        System.out.println("Introduce la nueva contraseña:");
        String nuevaContraseña = scanner.nextLine();

        while (!gestorDePerfil.esPasswordSegura(nuevaContraseña)) {
            System.out.println("Requisitos contraseña:\n- Mínimo 8 carácteres\n- Al menos 1 mayúscula\n- Al menos 1 número");
            System.out.println("Introduce nueva contraseña:");
            nuevaContraseña = scanner.nextLine();
        }

        gestorDePerfil.cambiarPassword(username, nuevaContraseña);
        System.out.println("¡Contraseña actualizada correctamente!");
        return true;
    }

    private void imprimirTableroSeguro(Tablero tablero) {
        try {
            tablero.imprimirTablero();
        } catch (CoordenadaFueraDeRangoException e) {
            System.out.println("Error visualizando tablero: " + e.getMessage());
        }
    }

    private boolean manejarColocacionPalabra(Turno turno) {
        System.out.println("\nIntroduce la palabra:");
        String palabra = scanner.nextLine().toUpperCase();

        System.out.println("Coordenadas iniciales:");
        String[] coords = scanner.nextLine().split(" ");
        int x = coords[0].charAt(0) - 'A';
        int y = Integer.parseInt(coords[1]) - 1;

        System.out.println("Orientación (V/H):");
        String orientacion = scanner.nextLine().equalsIgnoreCase("V") ? "vertical" : "horizontal";

        try {
            if (!turno.colocarPalabra(palabra, x, y, orientacion)) {
                System.out.println("¡Movimiento inválido! Verifica:");
                System.out.println("- La palabra existe en el diccionario");
                System.out.println("- Tienes las fichas necesarias");
                System.out.println("- La posición es válida");
                return false;
            }
        } catch (CoordenadaFueraDeRangoException | CasillaOcupadaException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private boolean manejarCambioFichas(Turno turno, Map<Ficha, Integer> atril) {
        System.out.println("\nFichas disponibles:");
        atril.forEach((f, c) -> System.out.print(f.getLetra() + "(" + c + ") "));

        System.out.println("\nIntroduce letras a cambiar (ej: A B C):");
        String[] letras = scanner.nextLine().toUpperCase().split(" ");

        Map<Ficha, Integer> cambio = new HashMap<>();
        for (String letra : letras) {
            if (!letra.isEmpty()) {
                Ficha f = new Ficha(letra, 0);
                cambio.put(f, cambio.getOrDefault(f, 0) + 1);
            }
        }
        
        boolean valido = true;
        for (Ficha f : cambio.keySet()) {
            if (!atril.containsKey(f) || atril.get(f) < cambio.get(f)) {
                System.out.println("No tienes suficientes fichas de " + f.getLetra());
                valido = false;

            }
        }

        if (valido) {
            turno.cambiarFichas(atril, cambio);
            System.out.println("¡Fichas cambiadas con éxito!");
        }
        return valido;
    }

    private void mostrarAtril(Map<Ficha, Integer> atril) {
        System.out.println("\n=== TUS FICHAS ===");
        atril.forEach((ficha, cant) ->
                System.out.printf("%s(%d) ", ficha.getLetra(), cant));
        System.out.println("\n==================");
    }

    public Partida crearPartida() {
        System.out.println("\n=== NUEVA PARTIDA ===");
        int id;
        do {
            System.out.println("ID de partida:");
            id = scanner.nextInt();
            scanner.nextLine();
        } while (partidas.containsKey(id));

        System.out.println("Nombre de la partida:");
        String nombre = scanner.nextLine();

        System.out.println("Idioma (1-CAT 2-CAST 3-ENG):");
        Partida.Idioma idioma = switch (scanner.nextInt()) {
            case 1 -> Partida.Idioma.CAT;
            case 2 -> Partida.Idioma.CAST;
            case 3 -> Partida.Idioma.ENG;
            default -> throw new IllegalArgumentException("Opción inválida");
        };
        scanner.nextLine();

        System.out.println("\n=== JUGADOR PRINCIPAL ===");
        Perfil jugadorPrincipal = autenticarUsuario();
        if (jugadorPrincipal == null) {
            return null;
        }
        System.out.println("\nModo de juego (1-PvP 2-PvIA):");
        Partida.Modo modo = scanner.nextInt() == 1 ? Partida.Modo.PvP : Partida.Modo.PvIA;
        scanner.nextLine();

        Partida nuevaPartida;
        if (modo == Partida.Modo.PvP) {
            System.out.println("\n=== JUGADOR SECUNDARIO ===");
            Perfil oponente = autenticarUsuario();
            if (oponente == null) {
                return null;
            }
            nuevaPartida = new Partida(jugadorPrincipal, oponente, id, nombre, modo, idioma);
        } else {
            System.out.println("Dificultad IA (1-3):");
            int dificultad = scanner.nextInt();
            nuevaPartida = new Partida(jugadorPrincipal, id, nombre, modo, idioma, dificultad);
        }

        partidas.put(id, nuevaPartida);
        System.out.println("\n¡Partida creada con éxito!");
        return nuevaPartida;
    }

    private Perfil autenticarUsuario() {
        while (true) {
            System.out.println("Nombre de usuario:");
            String username = scanner.nextLine();

            if (!gestorDePerfil.existeJugador(username)) {
                System.out.println("Usuario no encontrado");
                continue;
            }

            System.out.println("Contraseña:");
            String password = scanner.nextLine();

            if (gestorDePerfil.esPasswordCorrecta(username, password)) {
                return gestorDePerfil.getPerfil(username);
            }

            System.out.println("Contraseña incorrecta\n1- Reintentar\n2- Recuperar contraseña\n3- Cancelar\n");
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> { continue; }
                case 2 -> {
                    if (manejarRecuperacionContraseña(username)) continue;
                    else return null;
                }
                default -> { return null; }
            }
        }
    }

    public void jugar(int idPartida) {
        Partida partida = partidas.get(idPartida);
        if (partida == null) {
            System.out.println("Partida no encontrada");
            return;
        }

        boolean enJuego = true;
        while (enJuego) {
            Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
            Perfil jugador = turnoActual.getJugador();

            System.out.println("\n=== TURNO DE " + jugador.getUsername() + " ===");
            imprimirTableroSeguro(partida.getTablero());

            Map<Ficha, Integer> atril = jugador == partida.getCreador() ?
                    turnoActual.getAtrilJ1() : turnoActual.getAtrilJ2();

            mostrarAtril(atril);

            System.out.println("Acciones:\n 1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida\n");
            int num = scanner.nextInt();
            scanner.nextLine();
            boolean accionValida = false;
            while (!accionValida) {
                switch (num) {
                    case 1:
                        accionValida = manejarColocacionPalabra(turnoActual);
                        break;
                    case 2:
                        accionValida = manejarCambioFichas(turnoActual, atril);
                        break;
                    case 3:
                        turnoActual.pasarTurno();
                        accionValida = true;
                        break;
                    case 4:
                        System.out.println("¡Has salido de la partida!");
                        enJuego = false;
                        accionValida = true;
                        break;
                    default:
                        System.out.println("Opción inválida");
                        num = scanner.nextInt();
                        scanner.nextLine();
                        break;
                }
                if (!accionValida && (num == 1 || num == 2)) {
                    System.out.println("Acciones:\n1- Colocar palabra\n2- Cambiar fichas\n3- Pasar turno\n4- Salir de la partida");
                    num = scanner.nextInt();
                    scanner.nextLine();
                }
            }

            if (partida.getRondas().size() > 1) {
                Turno turnoAnterior = partida.getRondas().get(partida.getRondas().size() - 2);
                if (turnoAnterior.getTipoJugada() == turnoActual.getTipoJugada() && turnoActual.getTipoJugada() == Turno.TipoJugada.pasar) {
                    turnoActual.setTipoJugada(Turno.TipoJugada.finalizar);
                }
            }
            if (turnoActual.getTipoJugada() == Turno.TipoJugada.finalizar) {
                System.out.println("\n=== PARTIDA FINALIZADA ===");
                System.out.println("Puntuación final:");
                System.out.println("- " + partida.getCreador().getUsername() + ": " + turnoActual.getPuntuacionJ1());
                System.out.println("- " + (partida.getModoPartida() == Partida.Modo.PvP ? partida.getOponente().getUsername() : "IA") + ": " + turnoActual.getPuntuacionJ2());
                enJuego = false;
            }
        }
    }

    public boolean existePartidaJugador(Perfil jugador, int idpartida) {
        Partida p = partidas.get(idpartida);
        return p != null && (p.getCreador().equals(jugador) || (p.getOponente() != null && p.getOponente().equals(jugador)));
    }


    public void consultarPartidasJugador(Perfil jugador) {
        if (jugador == null) {
            System.out.println("\nEl jugador no es válido.\n");
            return;
        }

        List<Partida> partidasJugador = new ArrayList<>();

        for (Map.Entry<Integer, Partida> entry : partidas.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.getCreador().equals(jugador) || (partida.getOponente() != null && partida.getOponente().equals(jugador))) {
                partidasJugador.add(partida);
            }
        }

        if (partidasJugador.isEmpty()) {
            System.out.println("\nEl jugador " + jugador.getUsername() + " no participa en ninguna partida.\n");
        } else {
            System.out.println("\nPartidas de " + jugador.getUsername() + ":");
            System.out.println("-------------------------------------------------");
            for (Partida p : partidasJugador) {


                System.out.println("ID: " + p.getId());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Data de creación: " + p.getFechaHoraCreacion());
                System.out.println("Modo: " + p.getModoPartida());
                if (p.getModoPartida() == Partida.Modo.PvP) {
                    System.out.println("Oponente: " + p.getOponente());
                } else {
                    System.out.println("Dificultad: " + p.getDificultad());
                }
                System.out.println("-------------------------------------------------");
            }
            System.out.println();
        }
    }


    public void borrar(int idPartida) {
        Partida eliminada = partidas.remove(idPartida);
        if (eliminada != null) {
            System.out.println("Partida '" + eliminada.getNombre() + "' eliminada");
        } else {
            System.out.println("No se encontró la partida");
        }
    }
}