package gestordeperfil;

import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;

import java.util.Scanner;

/**
 * Provides a console-based interface for profile management operations.
 * This driver class handles user interaction for executing profile-related actions
 * through a {@link GestorDePerfil} instance, including retry logic for failed operations.
 *
 * @author Marc Ribas Acon
 */
public class DriverPerfil {

    /**
     * Displays and manages the profile operations menu.
     * Handles user input/output flow for various profile management tasks through console interactions.
     * Implements retry mechanisms for failed operations until success or user cancellation.
     *
     * @param gestorDePerfil the profile manager instance containing profile data and business logic
     */
    public void profileManagement(GestorDePerfil gestorDePerfil)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, pick any of the following options:");
        System.out.println("1- Create a new profile");
        System.out.println("2- Erase a profile");
        System.out.println("3- Change a password");
        System.out.println("4- Reestablish password");
        System.out.println("5- Change a username");
        System.out.println("6- Go back");
        int chosenOption = scanner.nextInt();
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
    }
}
