package estadisticas;

import gestordeperfil.*;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Clase encargada de la gestión de la interfaz de usuario para mostrar estadísticas de jugadores.
 * Proporciona funcionalidades para consultar y visualizar datos de perfiles existentes.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Visualización de estadísticas individuales de jugadores</li>
 *   <li>Búsqueda de perfiles por nombre de usuario</li>
 *   <li>Gestión interactiva de múltiples consultas</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class DriverEstadisticas {

    /**
     * Scanner para capturar la entrada del usuario durante el proceso de consulta
     * de estadísticas. Utilizado para leer nombres de usuario y opciones del menú.
     */
    private Scanner lector;

    /**
     * Gestor principal de perfiles que proporciona acceso a los datos almacenados.
     * Se utiliza para verificar la existencia de jugadores y recuperar sus estadísticas.
     */
    private GestorDePerfil gestorDePerfil;

    /**
     * Construye un controlador de estadísticas vinculado a un gestor de perfiles.
     *
     * @param gdp Gestor de perfiles que provee acceso a los datos
     * @param scanner Fuente de entrada para la interacción del usuario
     */
    public DriverEstadisticas(GestorDePerfil gdp, Scanner scanner) {
        gestorDePerfil = gdp;
        lector = scanner;
    }

    /**
     * Muestra las estadísticas de un perfil específico.
     *
     * <p>Flujo de operación:
     * <ol>
     *   <li>Verifica existencia de jugadores registrados</li>
     *   <li>Solicita nombre de usuario</li>
     *   <li>Muestra estadísticas si el perfil existe</li>
     *   <li>Informa errores en caso contrario</li>
     * </ol>
     *
     * @return true si se mostraron estadísticas válidas, false en caso contrario
     */
    public boolean mostrarEstadisticas() {
        System.out.print("\n");
        if (gestorDePerfil.hayJugadores()) {
            System.out.print("Por favor, introduzca el username del perfil: ");
            String username = lector.nextLine();
            if (gestorDePerfil.existeJugador(username)) {
                Perfil user = gestorDePerfil.getPerfil(username);
                System.out.print("\n");
                System.out.printf("---%s---\n", user.getUsername());
                System.out.printf("Puntos: %d\n", user.getPuntos());
                System.out.printf("Partidas jugadas: %d\n", user.getPartidasJugadas());
                System.out.printf("Victorias: %d\n", user.getPartidasGanadas());
                System.out.printf("Derrotas: %d\n", user.getPartidasPerdidas());
                System.out.print("\n");
                return true;
            } else {
                System.out.println("\nNo existe ningún perfil con ese username");
            }
        } else {
            System.out.println("No hay ningún jugador en el sistema");
        }
        return false;
    }

    /**
     * Gestiona el ciclo completo de consulta de estadísticas.
     *
     * <p>Permite realizar múltiples consultas hasta que el usuario decida salir.
     * El flujo incluye:
     * <ul>
     *   <li>Visualización inicial de estadísticas</li>
     *   <li>Opciones para continuar consultando o salir</li>
     *   <li>Manejo de entrada numérica del usuario</li>
     * </ul>
     */
    public void estadisticasManagement() {
        boolean operationDone = false;
        while (!operationDone) {
            operationDone = !mostrarEstadisticas();
            if (!operationDone) {
                System.out.println("¿Quieres ver las estadísticas de otro perfil?");
                System.out.println("1- Sí");
                System.out.println("2- No");
                System.out.print("\n");
                int chosenOption = lector.nextInt();
                lector.nextLine(); // Limpiar buffer
                operationDone = (chosenOption != 1);
            }
        }
    }
}