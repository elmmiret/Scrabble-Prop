package ctrldomini;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;

/**
 *
 *
 * @author Arnau Miret Barrull
 */

public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    Scanner scanner;

    /**
     * Construye un nuevo controlador de partidas con una base de datos vacía.
     * Inicializa el scanner, para que el usuario pueda comunicarse, y el mapa que almacena las partidas
     */
    public GestorDePartida() {
        scanner = new Scanner(System.in);
        partidas = new HashMap<>();
    }

    public boolean crearPartida() {
        System.out.print("Identificador de la partida: ");
        int idpartida = scanner.nextInt();
        if(!partidas.containsKey(idpartida)) {
            System.out.println("Escoge un diccionario: \n1- Català\n2- Castellano\n3- English");
            int diccionario = scanner.nextInt();
            if(diccionario == 1 || diccionario == 2 || diccionario == 3) {
                System.out.println("\nEl diccionario seleccionado no existe\n");
            }
            else System.out.println("\nEl diccionario seleccionado no existe\n");

            "Modo de juego: \n1- Otro usuario\n2- IA"

        }
        else System.out.println("\nEste identificador ya está en uso\n");
        return false;
    }


}
