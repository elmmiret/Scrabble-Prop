package ranking;

import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase encargada de la gestión y visualización de rankings de jugadores.
 * Proporciona funcionalidades para generar y mostrar diferentes tipos de rankings
 * basados en las estadísticas de los perfiles de los jugadores.
 *
 * <p>Los tipos de ranking disponibles son:
 * <ul>
 *   <li>Por puntos totales</li>
 *   <li>Por número de partidas jugadas</li>
 *   <li>Por número de victorias</li>
 *   <li>Por número de derrotas</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */

public class DriverRanking {
    Scanner lector;
    GestorDePerfil gestorDePerfil;

    /**
     * Construye un DriverRanking asociado a un GestorDePerfil específico.
     *
     * @param gdp Gestor de perfiles que contiene los datos de los jugadores
     * @param inputStream Fuente de entrada para la interacción del usuario
     */
    public DriverRanking(GestorDePerfil gdp, InputStream inputStream)
    {
        gestorDePerfil = gdp;
        lector = new Scanner(inputStream);
    }

    /**
     * Genera un ranking ordenado de perfiles según el criterio especificado.
     *
     * @param modo Criterio de ordenación:
     *             "puntos" - Por puntos totales
     *             "partidasJugadas" - Por partidas jugadas
     *             "victorias" - Por partidas ganadas
     *             "derrotas" - Por partidas perdidas
     * @return ArrayList de Perfil ordenado de mayor a menor según el criterio
     * @throws IllegalArgumentException Si el modo especificado no es válido
     */
    public ArrayList<Perfil> generarRanking(String modo) {
        Map<String, Perfil> jugadores = gestorDePerfil.getJugadores();
        ArrayList<Perfil> ranking = new ArrayList<>(jugadores.values());
        Comparator<Perfil> comparador;
        switch (modo) {
            case "puntos":
                comparador = Comparator.comparingInt(Perfil::getPuntos).reversed();
                break;
            case "partidasJugadas":
                comparador = Comparator.comparingInt(Perfil::getPartidasJugadas).reversed();
                break;
            case "victorias":
                comparador = Comparator.comparingInt(Perfil::getPartidasGanadas).reversed();
                break;
            case "derrotas":
                comparador = Comparator.comparingInt(Perfil::getPartidasPerdidas).reversed();
                break;
            default:
                throw new IllegalArgumentException("Modo de ordenación no válido: " + modo);
        }
        ranking.sort(comparador);
        return ranking;
    }

    /**
     * Muestra el ranking formateado en la consola según el modo especificado.
     *
     * @param modo Tipo de ranking a mostrar:
     *             "puntos", "partidasJugadas", "victorias" o "derrotas"
     */
    public void mostrarRanking(String modo)
    {
        ArrayList<Perfil> ranking;
        ranking = generarRanking(modo);
        System.out.println("\n--- RANKING " + modo.toUpperCase() + " ---\n");
        for (int i = 0; i < ranking.size(); ++i)
        {
            System.out.printf("%d- %s", i + 1, ranking.get(i).getUsername());
            switch(modo) {
                case "puntos":
                    System.out.printf(" - Puntos: %d\n", ranking.get(i).getPuntos());
                    break;
                case "partidasJugadas":
                    System.out.printf(" - Partidas jugadas: %d\n", ranking.get(i).getPartidasJugadas());
                    break;
                case "victorias":
                    System.out.printf(" - Victorias: %d\n", ranking.get(i).getPartidasGanadas());
                    break;
                case "derrotas":
                    System.out.printf(" - Derrotas: %d\n", ranking.get(i).getPartidasPerdidas());
                    break;
            }
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
        if (gestorDePerfil.hayJugadores()) {
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
        } else System.out.println("\nNo hay nigún jugador en el sistema\n");
    }
}
