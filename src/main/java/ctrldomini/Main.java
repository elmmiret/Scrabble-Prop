package ctrldomini;

import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Main {

  public static void main(String[] args) {

    // INIT
    Scanner scanner = new Scanner(System.in);
    GestorDePerfil gestorDePerfil = new GestorDePerfil();
    Map<String, Perfil> players = new HashMap<>();

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
            String username;
            String password;
            String passphrase;
            String oldusername;
            String oldpassword;
            String newusername;
            String newpassword;
            switch (chosenOption)
            {
              case 1: //Create new profile
                scanner.nextLine(); //consume console buffer
                System.out.print("Username: ");
                username = scanner.nextLine();
                System.out.print("Password: ");
                password = scanner.nextLine();
                System.out.print("Passphrase: ");
                passphrase = scanner.nextLine();

                if (players.containsKey(username)) System.out.println("This username is already in use");
                else
                {
                  players.put(username, gestorDePerfil.createPerfil(username, password, passphrase));
                  System.out.println(players.get(username).getUsername());
                  System.out.println(players.get(username).getPassword());
                  System.out.println(players.get(username).getRecoveryPhrase());
                }
                break;

              case 2: //Erase a profile
                break;
              case 3: //Change password
                scanner.nextLine(); //consume console buffer
                System.out.print("Username: ");
                username = scanner.nextLine();
                System.out.print("Old password: ");
                oldpassword = scanner.nextLine();
                System.out.print("New password: ");
                newpassword = scanner.nextLine();

                if (players.containsKey(username))
                {
                  if (!gestorDePerfil.changePassword(players.get(username), username, oldpassword, newpassword))
                    System.out.println("Wrong credentials");
                  else
                  {
                    System.out.println(players.get(username).getUsername());
                    System.out.println(players.get(username).getPassword());
                    System.out.println(players.get(username).getRecoveryPhrase());
                  }
                }
                else System.out.println("This user doesn't exist");
                break;

              case 4: //Reestablish password
                break;
              case 5: //Change username
                //careful, the map key of the entry has to change as well or create another and delete last
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