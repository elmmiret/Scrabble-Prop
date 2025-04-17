package ctrldomini;

import gestordeperfil.DriverPerfil;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;
import ranking.DriverRanking;
import ranking.Ranking;

import java.util.Scanner;

public class Main {

  public static void main(String[] args)
  {

    // INIT
    Scanner scanner = new Scanner(System.in);
    Ranking ranking = new Ranking();
    GestorDePerfil gestorDePerfil = new GestorDePerfil(ranking);
    DriverPerfil driverPerfil = new DriverPerfil(gestorDePerfil, System.in);
    DriverRanking driverRanking = new DriverRanking(ranking, System.in);


    System.out.println("\n| S | | C | | R | | A | | B | | B | | L | | E |\n");

    while (true)
    {
      System.out.print("\n");
      System.out.println("Por favor, escoja una de las siguientes opciones:");
      System.out.println("1- Gestión de perfil");
      System.out.println("2- Crear nueva partida");
      System.out.println("3- Jugar una partida empezada");
      System.out.println("4- Ver rankings");
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
            System.out.println("Creating new game");
            break;
          case 3: //Play a started game
            System.out.println("Please, choose a started game:");
            break;
          case 4: //See ranking
            driverRanking.rankingManagement();
            break;
          case 5: //Exit
            System.out.println("Goodbye!");
            System.exit(0);
            break;
          default: //Error option
            System.out.println("Incorrect option, please choose any of the options offered");
            break;
        }
    }
  }
}