package ctrldomini;

import exceptions.*;
import java.util.Scanner;

/**
 * Driver para probar las funcionalidades de la clase Tablero
 *
 * @author: Paula Pérez
 */
public class DriverTablero {

    private static void mostrarInstrucciones() {
        System.out.println("*** DRIVER DE LA CLASE TABLERO ***");
        System.out.println("1. Crear tablero");
        System.out.println("2. Colocar ficha en el tablero");
        System.out.println("3. Ver ficha en una casilla");
        System.out.println("4. Ver modificador de una casilla");
        System.out.println("5. Imprimir tablero");
        System.out.println("0. Salir");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;
        Tablero tablero = null;

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
                    // poner en un futuro seleccion de idioma para que cambie la leyenda?
                    tablero = new Tablero();
                    System.out.print("Tablero creado");
                    break;

                case 2:
                    System.out.print("Introduce la letra de la ficha: ");
                    String letra = seleccion.nextLine();
                    System.out.print("Introduce la puntuación de la ficha: ");
                    String puntuacion = seleccion.nextLine();
                    int puntuacion_valida;
                    try {
                        puntuacion_valida = Integer.parseInt(puntuacion);
                    } catch (NumberFormatException e) {
                        System.out.println("Input inválido");
                        break;
                    }
                    ficha = new Ficha(letra, puntuacion_valida);
                    System.out.println("Ficha creada.");
                    break;

                    System.out.print("Introduce la fila (A - O): ");
                    char fila = scanner.nextLine().charAt(0);
                    System.out.print("Introduce la columna (1 - 15): ");
                    int columna = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        tablero.setFicha(ficha, fila, columna);
                        System.out.println("Ficha colocada correctamente.");
                    } catch (CoordenadaFueraDeRangoException | CasillaOcupadaException e) {
                        System.out.println(e.getMessage());
                    }
                    break;


                case 4:
                    System.out.print("Introduce la fila (A - O): ");
                    char filaMod = scanner.nextLine().charAt(0);

                    System.out.print("Introduce la columna (1 - 15): ");
                    int columnaMod = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        Ficha ficha = tablero.getFicha(filaMod - 'A', columnaMod - 1);
                        if (ficha != null) {
                            System.out.println("Letra de la ficha: " + ficha.getLetra());
                            System.out.println("Puntuación de la ficha: " + ficha.getPuntuacion());
                        } else {
                            System.out.println("No hay modificador en esta casilla.");
                        }
                    } catch (CoordenadaFueraDeRangoException e) {
                        System.out.println("Coordenada fuera de rango: " + e.getMessage());
                    }
                    break;

                case 5:
                    System.out.print("Introduce la fila (A - O): ");
                    char filaMod = scanner.nextLine().charAt(0);

                    System.out.print("Introduce la columna (1 - 15): ");
                    int columnaMod = scanner.nextInt();
                    scanner.nextLine();

                    try {
                        TipoModificador modificador = tablero.getTipoModificador(filaMod - 'A', columnaMod - 1);
                        if (modificador != null) {
                            System.out.println("Modificador de la casilla: " + modificador);
                        } else {
                            System.out.println("No hay modificador en esta casilla.");
                        }
                    } catch (CoordenadaFueraDeRangoException e) {
                        System.out.println("Coordenada fuera de rango: " + e.getMessage());
                    }
                    break;

                case 6:
                    if (tablero != null) tablero.imprimirTablero();
                    else System.out.println("No hay ningun tablero creado, pulsa 1 y vuelve a repetir esta opción posteriormente");
                    break;

                case 0:
                    salir = true;
                    break;

                default:
                    System.out.println("Acción no válida");
            }
        }
        System.out.println("*** FIN DEL TESTEO ***");
        seleccion.close();
    }
}
