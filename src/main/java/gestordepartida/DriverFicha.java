package gestordepartida;

import java.util.Scanner;

/**
 * Driver para probar las funcionalidades de la clase Ficha NO SE USA EN LA IMPLEMENTACIÓN
 *
 * @author Paula Pérez
 */
public class DriverFicha {

    private static void mostrarInstrucciones() {
        System.out.println("*** DRIVER DE LA CLASE FICHA ***");
        System.out.println("1. Crear ficha");
        System.out.println("2. Consultar letra");
        System.out.println("3. Consultar puntuación");
        System.out.println("0. Salir");
    }


    public static void main(String[] args) {
        Scanner seleccion = new Scanner(System.in);
        boolean salir = false;
        Ficha ficha = null;

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

                case 2:
                    if (ficha != null) System.out.println("Letra de la ficha: " + ficha.getLetra());
                    else System.out.println("No hay ninguna ficha creada, pulsa 1 y vuelve a repetir esta opción posteriormente");
                    break;

                case 3:
                    if (ficha != null) System.out.println("Puntuación de la ficha: " + ficha.getPuntuacion());
                    else System.out.println("No hay ninguna ficha creada, pulsa 1 y vuelve a repetir esta opción posteriormente");
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
