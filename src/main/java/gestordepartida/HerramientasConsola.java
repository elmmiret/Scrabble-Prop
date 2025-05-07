package gestordepartida;

import java.util.Scanner;

public class HerramientasConsola {
    private static Scanner scanner;


    public HerramientasConsola(Scanner scanner) {
        this.scanner = scanner;
    }


    /**
     * Utilidad para lectura de cadenas con mensaje personalizado.
     *
     * @param mensaje Texto a mostrar antes de la entrada
     * @return Cadena introducida por el usuario (sin trim)
     */
    public String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine();
    }

    /**
     * Utilidad para lectura de enteros con validación.
     *
     * @param mensaje Texto a mostrar antes de la entrada
     * @return Entero válido introducido por el usuario
     */
    public int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Introduce un número.");
            }
        }
    }
}
