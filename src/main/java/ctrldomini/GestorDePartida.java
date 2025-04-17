package ctrldomini;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

/**
 * Esta clase representa el Gestor de Partida
 *
 * @author Arnau Miret Barrull
 */
public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    Scanner scanner;

    /**
     * @author Arnau Miret Barrull
     */
    public GestorDePartida() {
        scanner = new Scanner(System.in);
        partidas = new HashMap<>();
    }

    /**
     * @author Arnau Miret Barrull
     */
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
    public void consultarPartidasJugador(Perfil jugador) {

    }

    public boolean existePartidaJugador(Perfil jugador) {

    }

    public void jugar(int idpartida) {

    }

    public void borrar(int idpartida) {

    }


}
