package ctrldomini;

import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    // INIT
    Scanner scanner = new Scanner(System.in);
    GestorDePerfil gestorDePerfil = new GestorDePerfil();

    System.out.println("\n| S | | C | | R | | A | | B | | B | | L | | E |\n");

    while (true)
    {
      System.out.println("Hello! Welcome to Scrabble!");
      System.out.println("Please, pick any of the following options:");
      System.out.println("1- Profile management");
      System.out.println("2- Create a new game");
      System.out.println("3- Play a started game");
      System.out.println("4- See ranking");
      System.out.println("5- Exit");

      int chosenOption = scanner.nextInt();
        switch (chosenOption)
        {
          case 1: //Profile management
            System.out.println("Please, pick any of the following options:");
            System.out.println("1- Create a new profile");
            System.out.println("2- Erase a profile");
            System.out.println("3- Change a password");
            System.out.println("4- Reestablish password");
            System.out.println("5- Change a username");
            System.out.println("6- Go back");
            chosenOption = scanner.nextInt();
            boolean operationDone = false;
            switch (chosenOption)
            {
              case 1: //Create new profile
                while (!operationDone)
                {
                  if (gestorDePerfil.createPerfil()) operationDone = true;
                  else
                  {
                    System.out.println("Do you want to try again?");
                    System.out.println("1- Yes");
                    System.out.println("2- No");
                    chosenOption = scanner.nextInt();
                    if (chosenOption == 2) operationDone = true;
                    scanner.nextLine(); //consume console buffer
                  }
                }
                break;

              case 2: //Erase a profile
                while (!operationDone)
                {
                  if (gestorDePerfil.eraseProfile()) operationDone = true;
                  else
                  {
                    System.out.println("Do you want to try again?");
                    System.out.println("1- Yes");
                    System.out.println("2- No");
                    chosenOption = scanner.nextInt();
                    if (chosenOption == 2) operationDone = true;
                    scanner.nextLine(); //consume console buffer
                  }
                }
                break;

              case 3: //Change password
                while (!operationDone)
                {
                  if (gestorDePerfil.changePassword()) operationDone = true;
                  else
                  {
                    System.out.println("Do you want to try again?");
                    System.out.println("1- Yes");
                    System.out.println("2- No");
                    chosenOption = scanner.nextInt();
                    if (chosenOption == 2) operationDone = true;
                    scanner.nextLine(); //consume console buffer
                  }
                }
                break;

              case 4: //Reestablish password
                while (!operationDone)
                {
                  if (gestorDePerfil.reestablishPassword()) operationDone = true;
                  else
                  {
                    System.out.println("Do you want to try again?");
                    System.out.println("1- Yes");
                    System.out.println("2- No");
                    chosenOption = scanner.nextInt();
                    if (chosenOption == 2) operationDone = true;
                    scanner.nextLine(); //consume console buffer
                  }
                }
                break;

              case 5: //Change username
                while (!operationDone)
                {
                  if (gestorDePerfil.changeUsername()) operationDone = true;
                  else
                  {
                    System.out.println("Do you want to try again?");
                    System.out.println("1- Yes");
                    System.out.println("2- No");
                    chosenOption = scanner.nextInt();
                    if (chosenOption == 2) operationDone = true;
                    scanner.nextLine(); //consume console buffer
                  }
                }
                break;

              case 6: //Go back
                break;
              default:
                System.out.println("Incorrect option, please choose any of the options offered");
                break;
            }
            break;
          case 2: //Create new game
            System.out.println("Creating new game");
            break;
          case 3: //Play a started game
            System.out.println("Please, choose a started game:");
            break;
          case 4: //See ranking
            System.out.println("Displaying ranking...");
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