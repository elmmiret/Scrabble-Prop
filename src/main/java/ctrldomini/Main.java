package ctrldomini;

import gestordeperfil.DriverPerfil;
import gestordeperfil.GestorDePerfil;
import gestordepartida.DriverPartida;
import gestordepartida.GestorDePartida;
import ranking.DriverRanking;
import ranking.Ranking;
import estadisticas.DriverEstadisticas;
import exceptions.*;
import view.GestorDeView;

import javax.swing.*;
import java.util.Scanner;

/**
 * Clase principal que actúa como controlador del flujo del programa.
 * Gestiona el menú principal y coordina las diferentes funcionalidades del sistema,
 * delegando acciones a módulos específicos según la opción seleccionada por el usuario.
 *
 * <p>El programa permite gestionar perfiles, partidas, visualizar rankings y estadísticas,
 * así como salir de la aplicación. Utiliza varios gestores y drivers para interactuar
 * con las capas de dominio y presentación.</p>
 *
 * @author Marc Ribas Acon
 */
public class Main {

  /**
   * Método principal que inicia la ejecución del programa.
   * <p>
   * Inicializa los componentes necesarios (scanner, gestores, drivers) y muestra un menú interactivo.
   * Según la opción seleccionada, delega la ejecución a los módulos correspondientes:
   * <ul>
   *   <li>1: Gestión de perfiles (crear, modificar, eliminar).</li>
   *   <li>2: Gestión de partidas (iniciar, pausar, reanudar).</li>
   *   <li>3: Visualización de rankings.</li>
   *   <li>4: Visualización de estadísticas.</li>
   *   <li>5: Salir del programa.</li>
   * </ul>
   *
   * @param args Argumentos de línea de comandos (no utilizados en este programa).
   * @throws CasillaOcupadaException Si se intenta ocupar una casilla ya ocupada en el tablero.
   * @throws CoordenadaFueraDeRangoException Si se proporcionan coordenadas inválidas durante una partida.
   */
  public static void main(String[] args) throws CasillaOcupadaException, CoordenadaFueraDeRangoException
  {
    // INIT
    Scanner scanner = new Scanner(System.in);
    Ranking ranking = new Ranking();
    GestorDePerfil gestorDePerfil = new GestorDePerfil(ranking);
    GestorDePartida gestorDePartida = new GestorDePartida(gestorDePerfil);
    DriverPerfil driverPerfil = new DriverPerfil(gestorDePerfil, scanner);
    DriverRanking driverRanking = new DriverRanking(ranking, scanner);
    DriverEstadisticas driverEstadisticas = new DriverEstadisticas(gestorDePerfil, scanner);
    DriverPartida driverPartida = new DriverPartida(gestorDePartida, gestorDePerfil, scanner);

    SwingUtilities.invokeLater(
            new Runnable() {
              @Override
              public void run() {
                GestorDeView gestorDeView = new GestorDeView(gestorDePerfil, ranking, gestorDePartida);
              }
            }
    );

    System.out.println("\n| S | | C | | R | | A | | B | | B | | L | | E |\n");

    while (true)
    {
      System.out.print("\n");
      System.out.println("Por favor, escoja una de las siguientes opciones:");
      System.out.println("1- Gestión de perfil");
      System.out.println("2- Gestión de partida");
      System.out.println("3- Ver rankings");
      System.out.println("4- Ver estadísticas");
      System.out.println("5- Salir");
      System.out.print("\n");

      int chosenOption = scanner.nextInt();
      scanner.nextLine(); //consume console buffer
        switch (chosenOption)
        {
          case 1: //Profile management
            driverPerfil.profileManagement();
            break;
          case 2: //Create new game
            driverPartida.partidaManagement();
            break;
          case 3: //See ranking
            driverRanking.rankingManagement();
            break;
          case 4: //Exit
            driverEstadisticas.estadisticasManagement();
            break;
          case 5:
            System.out.println("¡Adiós!\n");
            System.exit(0);
            break;
          default: //Error option
            System.out.println("Opción incorrecta, por favor escoja una de las opciones ofrecidas");
            break;
        }
    }
  }
}