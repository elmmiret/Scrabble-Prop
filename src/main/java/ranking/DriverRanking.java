package ranking;

import gestordeperfil.Perfil;
import java.io.InputStream;
import java.util.*;

/**
 * Clase encargada de la interacción con el usuario para la visualización de rankings.
 * Gestiona la presentación de diferentes clasificaciones y el menú de navegación.
 *
 * <p>Funcionalidades principales:
 * <ul>
 *   <li>Visualización formateada de rankings por diferentes criterios</li>
 *   <li>Menú interactivo para selección de tipo de ranking</li>
 *   <li>Gestón de flujo de navegación entre rankings</li>
 *   <li>Interacción con el usuario mediante consola</li>
 * </ul>
 *
 * <p>Tipos de ranking soportados:
 * <ol>
 *   <li>Puntos totales</li>
 *   <li>Partidas jugadas</li>
 *   <li>Victorias obtenidas</li>
 *   <li>Derrotas acumuladas</li>
 * </ol>
 *
 * @author Marc Ribas Acon
 */
public class DriverRanking {
    Scanner lector;
    Ranking ranking;

    /**
     * Construye un controlador de rankings vinculado a un sistema de rankings existente.
     *
     * @param rkg Sistema de rankings que contiene los datos a mostrar
     * @param scanner Fuente de entrada para interactuar con el usuario
     */
    public DriverRanking(Ranking rkg, Scanner scanner)
    {
        ranking = rkg;
        lector = scanner;
    }

    /**
     * Muestra en consola un ranking formateado según el criterio especificado.
     *
     * @param modo Tipo de ranking a visualizar (case-insensitive):
     */
    public void mostrarRanking(String modo)
    {
        System.out.println("\n--- RANKING " + modo.toUpperCase() + " ---\n");
        TreeSet<Perfil> outputRanking;
        int position = 1;
        switch(modo) {
            case "puntos":
                outputRanking = ranking.getRankingPuntos();
                for (Perfil perfil : outputRanking)
                {
                    System.out.printf("%d- %s", position++, perfil.getUsername());
                    System.out.printf(" - Puntos: %d\n", perfil.getPuntos());
                }
                break;
            case "partidasJugadas":
                outputRanking = ranking.getRankingPartidasJugadas();
                for (Perfil perfil : outputRanking)
                {
                    System.out.printf("%d- %s", position++, perfil.getUsername());
                    System.out.printf(" - Partidas jugadas: %d\n", perfil.getPartidasJugadas());
                }
                break;
            case "victorias":
                outputRanking = ranking.getRankingVictorias();
                for (Perfil perfil : outputRanking)
                {
                    System.out.printf("%d- %s", position++, perfil.getUsername());
                    System.out.printf(" - Victorias: %d\n", perfil.getPartidasGanadas());
                }
                break;
            case "derrotas":
                outputRanking = ranking.getRankingDerrotas();
                for (Perfil perfil : outputRanking)
                {
                    System.out.printf("%d- %s", position++, perfil.getUsername());
                    System.out.printf(" - Derrotas: %d\n", perfil.getPartidasPerdidas());
                }
                break;
        }
        System.out.print("\n");
    }

    /**
     * Pregunta al usuario si desea visualizar otro ranking.
     *
     * @return true si el usuario quiere ver otro ranking, false en caso contrario
     */
    public boolean verOtroRanking()
    {
        System.out.println("Quieres ver otro ranking?");
        System.out.println("1- Sí");
        System.out.println("2- No");
        System.out.print("\n");
        int chosenOption = lector.nextInt();
        lector.nextLine(); //limpiar buffer
        if (chosenOption == 2) return false;
        return true;
    }

    /**
     * Gestiona el menú interactivo de selección de rankings.
     * Permite navegar por las diferentes opciones de visualización hasta que
     * el usuario seleccione la opción de salir o no existan jugadores registrados.
     */
    public void rankingManagement()
    {
        if (!ranking.rankingsVacios()) {
            boolean operationDone = false;
            while (!operationDone)
            {
                System.out.print("\n");
                System.out.println("Por favor, escoja cómo quiere ordenar el ranking:");
                System.out.println("1- Por número de puntos");
                System.out.println("2- Por número de partidas jugadas");
                System.out.println("3- Por número de victorias");
                System.out.println("4- Por número de derrotas");
                System.out.println("5- Atrás");
                System.out.print("\n");
                int chosenOption = lector.nextInt();
                lector.nextLine(); // Limpiar buffer
                switch (chosenOption)
                {
                    case 1:
                        mostrarRanking("puntos");
                        operationDone = !verOtroRanking();
                        break;

                    case 2:
                        mostrarRanking("partidasJugadas");
                        operationDone = !verOtroRanking();
                        break;

                    case 3:
                        mostrarRanking("victorias");
                        operationDone = !verOtroRanking();
                        break;

                    case 4:
                        mostrarRanking("derrotas");
                        operationDone = !verOtroRanking();
                        break;

                    case 5:
                        operationDone = true;
                        break;

                    default:
                        System.out.println("Opción incorrecta, por favor selecciona una de las opciones ofrecidas");
                        break;
                }
            }
        } else System.out.println("\nNo hay ningún jugador en el sistema\n");
    }
}
