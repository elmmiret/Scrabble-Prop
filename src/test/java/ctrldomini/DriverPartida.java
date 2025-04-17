package ctrldomini;

import algorisme;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

/**
 * Driver para probar las funcionalidades de la clase Partida.
 *
 * @author Paula Pérez
 */
public class DriverPartida {

    private static void mostrarInstrucciones() {
        System.out.println("*** DRIVER DE LA CLASE PARTIDA ***");
        System.out.println("1. Crear partida");
        System.out.println("2. Cargar partida");
        System.out.println("3. Eliminar partida");
        System.out.println("4. Consultar partidas");
        System.out.println("0. Salir");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean salir = false;
        Partida partida = null;
        GestorDePartida gestor;
        GestorDePerfil gestorPerfil;
        mostrarInstrucciones();

        while (!salir) {
            System.out.print("Elige una opción: ");
            String input = seleccion.nextLine(); // para leer hasta salto de linea
            int opcion;
            try {
                opcion = Integer.parseInt(input);  // intenta convertir a número
            } catch (NumberFormatException e) {
                opcion = -1;  // si no habia un numero, ponemos un valor imposible para forzar el "default"
            }

            switch (opcion) {
                case 1:
                    System.out.println("Sigue las instrucciones para crear la partida: ");
                    partida = gestor.CrearPartida();
                    if (partida == null) System.out.println("Error al crear la partida, vuelve a intentarlo pulsando 1.");
                    else System.out.println("Partida creada con éxito, si deseas jugarla selecciona la opción 2 e inserta su id.");
                    break;

                case 2:
                    System.out.println("Inicia sesión con tu cuenta");
                    Perfil jugador = gestorPerfil.login();
                    if (jugador == null) {
                        System.out.println("Error al iniciar sesión.");
                        break;
                    }

                    System.out.println("Inserta el id de la partida que quieras cargar:");
                    String input = seleccion.nextLine(); // para leer hasta salto de linea
                    int num;
                    try {
                        num = Integer.parseInt(input);  // intenta convertir a número
                    } catch (NumberFormatException e) {
                        num = -1;  // si no habia un numero, ponemos un valor imposible para forzar el "default"
                    }
                    if (num == -1) {
                        System.out.println("formato de id erroneo, pulsa 3 y posteriormente vuelve a intentarlo");
                        break;
                    }
                    if (!gestor.existePartidaJugador(jugador)) System.out.println("No existe ninguna partida con este id para tu perfil.");
                    else gestor.jugar(input);
                    // que esa funcion llame al driver de turno y que de alguna manera
                    break;

                case 3:
                    System.out.println("Inicia sesión con tu cuenta");
                    Perfil jugador = gestorPerfil.login();
                    if (jugador == null) {
                        System.out.println("Error al iniciar sesión.");
                        break;
                    }

                    System.out.println("Inserta el id de la partida que quieras eliminar:");
                    String input = seleccion.nextLine(); // para leer hasta salto de linea
                    int num;
                    try {
                        num = Integer.parseInt(input);  // intenta convertir a número
                    } catch (NumberFormatException e) {
                        num = -1;  // si no habia un numero, ponemos un valor imposible para forzar el "default"
                    }
                    if (num == -1) {
                        System.out.println("formato de id erroneo, pulsa 3 y posteriormente vuelve a intentarlo");
                        break;
                    }
                    if (!gestor.existePartidaJugador(jugador)) System.out.println("No existe ninguna partida con este id para tu perfil.");
                    else gestor.borrar(input);
                    break;

                case 4:
                    // el jugador tiene que logguearse para verificar que es el y entonces poder consultar sus partidas
                    // tiene que imprimir los ids de sus partidas, el nombre, el tipo y si es PvP su oponente
                    Perfil jugador = gestorPerfil.login();
                    gestor.consultarPartidasJugador(perfil.getUsername());
                    break;

                default:
                    break;
            }
        }
        System.out.println("*** FIN DEL TESTEO ***");
    }
}
