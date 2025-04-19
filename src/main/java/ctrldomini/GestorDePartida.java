package ctrldomini;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

/**
 * Gestiona la creación, eliminación, consulta y ejecución de partidas de Scrabble.
 * Proporciona funcionalidades para interactuar con múltiples partidas y usuarios, como:
 *
 * @author Albert Aulet Niubó
 * @author Arnau Miret Barrull
 */
public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    Scanner scanner;

    /**
     * Constructor que inicializa el gestor con un scanner para entrada de usuario
     * y un mapa vacío para almacenar partidas.
     *
     * @author Arnau Miret Barrull
     */
    public GestorDePartida() {
        scanner = new Scanner(System.in);
        partidas = new HashMap<>();
    }

    /**
     * Crea una nueva partida interactuando con el usuario para recoger los parámetros necesarios.
     * Valida identificadores únicos, credenciales de jugadores y configura el modo de juego.
     *
     * @return Partida creada o null si hubo errores en el proceso.
     * @author Arnau Miret Barrull
     */
    // TODO: acabar esta clase
    public Partida crearPartida() {
        Partida partida = null;
        System.out.print("Identificador de la partida: ");
        int idpartida = scanner.nextInt();
        if(!partidas.containsKey(idpartida)) {
            System.out.print("Nombre de la partida: ");
            String nombrepartida = scanner.nextLine();
            System.out.println("Escoge un diccionario: \n1- Català\n2- Castellano\n3- English");
            // funcion para cargar el diccionario en la partida (llenar la bolsa)
            int diccionario = scanner.nextInt();
            if(diccionario == 1 || diccionario == 2 || diccionario == 3) {
                System.out.println("\n\"Modo de juego: \\n1- PvP\\n2- PvIA\n");
                int mododejuego = scanner.nextInt();
                if(mododejuego == 1) {
                    // PvP
                    // logear jugador principal y secundario (2 funciones)
                    System.out.println("Logguear jugador principal:\n");
                    System.out.println("Introduce el nombre de usuario principal: ");
                    String nombreprincipal = scanner.nextLine();
                    if(players.containsKey(nombreprincipal)) {
                        System.out.println("Introduce la contraseña: ");
                        String contraseñaprincipal = scanner.nextLine();
                        if(players.get(nombreprincipal).getPassword() == contraseñaprincipal) {
                            System.out.println("Introduce el nombre de usuario secundario: ");
                            String nombresecundario = scanner.nextLine();
                            if(players.containsKey(nombresecundario)) {
                                System.out.println("Introduce la contraseña: ");
                                String contraseñasecundario = scanner.nextLine();
                                if(players.get(nombresecundario).getPassword() == contraseñasecundario) {
                                    // ya tenemos todos los parametros
                                    //funcion de crear la partida

                                }
                                else {
                                    System.out.println("\nLa contraseña no es correcta\n");
                                    System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n");
                                    int opcion = scanner.nextInt();
                                    if(opcion == 2) {
                                        //funcion de cambiar el password
                                    }

                                }
                            }
                            else System.out.println("\nEl nombre de perfil indicado no existe\n");
                        }
                        else {
                            System.out.println("\nLa contraseña no es correcta\n");
                            System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n");
                            int opcion = scanner.nextInt();
                            if(opcion == 2) {
                                //funcion de cambiar el password
                            }

                        }
                    }
                    else System.out.println("\nEl nombre de perfil indicado no existe\n");

                }
                else if(mododejuego == 2) {
                    //PvIA
                    System.out.println("Logguear jugador principal:\n");
                    System.out.println("Introduce el nombre de usuario principal: ");
                    String nombreprincipal = scanner.nextLine();
                    if(players.containsKey(nombreprincipal)) {
                        System.out.println("Introduce la contraseña: ");
                        String contraseñaprincipal = scanner.nextLine();
                        if(players.get(nombreprincipal).getPassword() == contraseñaprincipal) {

                        }
                        else {
                            System.out.println("\nLa contraseña no es correcta\n");
                            System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n");
                            int opcion = scanner.nextInt();
                            if(opcion == 2) {
                                //funcion de cambiar el password
                            }
                        }
                    }
                    else System.out.println("\nEl nombre de perfil indicado no existe\n");

                }
                else System.out.println("\nEl modo de juego no es correcto\n");

            }
            else System.out.println("\nEl diccionario seleccionado no existe\n");

        }
        else System.out.println("\nEste identificador ya está en uso\n");

        return partida;
    }

    // si no existe nada, que imprima un mensaje informativo

    /**
     * Muestra todas las partidas asociadas a un jugador (como creador u oponente).
     * Incluye detalles como ID, nombre, fecha de creación y modo de juego.
     *
     * @param jugador Perfil del jugador cuyas partidas se quieren consultar.
     * @author Albert Aulet Niubó
     */
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
        }

        else {
            System.out.println("\nPartidas de " + jugador.Username() + ":");
            System.out.println("-------------------------------------------------");
            for (Partida p : partidasJugador) {


                System.out.println("ID: " + p.getId());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Data de creación: " + p.getFechaHoraCreacion());
                System.out.println("Modo: " + p.getModoPartida());
                if (p.getModoPartida() == Partida.Modo.PvP) {
                    System.out.println("Oponente: " + p.getOponente());
                }
                else {
                    System.out.println("Dificultad: " + p.getDificultad());
                }
                System.out.println("-------------------------------------------------");
            }
            System.out.println();
        }
    }

    /**
     * Verifica si un jugador participa en una partida específica.
     *
     * @param jugador Perfil del jugador a verificar.
     * @param idpartida Identificador de la partida.
     * @return true si el jugador es creador u oponente de la partida, false en caso contrario.
     * @author Albert Aulet Niubó
     */
    public boolean existePartidaJugador(Perfil jugador, int idpartida) {
        return partidas.get(idpartida).getCreador().equals(jugador) || partidas.get(idpartida).getOponente().equals(jugador);
    }

    /**
     * Inicia la ejecución de una partida existente.
     *
     * @param idpartida Identificador de la partida a jugar.
     * @author Albert Aulet Niubó
     */
    public void jugar(int idpartida) {

    }

    /**
     * Elimina una partida del gestor.
     *
     * @param idpartida Identificador de la partida a borrar.
     * @author Albert Aulet Niubó
     */
    public void borrar(int idpartida) {
        if (partidas.containsKey(idpartida)) {
            partidas.remove(idpartida);
            System.out.println("La partida con identificador " + idpartida + " ha sido borrada correctamente.\n");
        }
        else {
            System.out.println("No existe ninguna partida con identificador " + idpartida + ".\n");
        }
    }


}
