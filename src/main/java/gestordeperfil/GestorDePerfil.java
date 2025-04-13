package gestordeperfil;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

//this class manages the creation, elimination and logs into the database of the profiles
public class GestorDePerfil {

    private Map<String, Perfil> players;
    Scanner scanner;

    public GestorDePerfil()
    {
        scanner = new Scanner(System.in);
        players = new HashMap<>();
    }

    private boolean correctPassword(String username, String password)
    {
        return players.get(username).getPassword().equalsIgnoreCase(password);
    }

    private boolean correctRecoveryPhrase(String username, String recoveryPhrase)
    {
        return players.get(username).getRecoveryPhrase().equals(recoveryPhrase);
    }

    public static boolean passwordIsSafe(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasUpperCase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            if (hasUpperCase && hasDigit) return true;
        }
        return false;
    }

    public boolean createPerfil()
    {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (!players.containsKey(username))
        {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (passwordIsSafe(password)) {
                System.out.print("Password again: ");
                String password2 = scanner.nextLine();
                if (password.equals(password2))
                {
                    System.out.print("What's your favourite color? (Recovery phrase): ");
                    String recoveryPhrase = scanner.nextLine();
                    players.put(username, new Perfil(username, password, recoveryPhrase));
                    System.out.println("\nProfile created successfully\n");
                    System.out.println(players.get(username).getUsername());
                    System.out.println(players.get(username).getPassword());
                    System.out.println(players.get(username).getRecoveryPhrase());
                    return true;
                }
                else System.out.println("\nERROR: The passwords don't match\n"); //Extension 1b: two passwords don't match
            }
            else System.out.println("\nERROR: The password doesn't meet the minimum safety requirements: 8 characters and at least 1 upper case letter and 1 number\n"); //Extension 1c: password doesn't meet minimum safety requirements
        }
        else System.out.println("\nERROR: This username is already in use\n"); //Extension 1a: profile already exists

        return false;
    }

    //return true if oldName and the actual name of the profile are the same and the password is correct, meaning that
    // the process turned ok
    //return false if oldName and the actual name of the profile dont match, if the password is incorrect or the old and
    //new usernames are the same
    public boolean changeUsername() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username))
        {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password))
            {
                System.out.print("New username: ");
                String newUsername = scanner.nextLine();
                if (!username.equals(newUsername))
                {
                    if (!players.containsKey(newUsername))
                    {
                        Perfil player = players.get(username);
                        player.changeUsername(newUsername);
                        players.remove(username);
                        players.put(newUsername, player);
                        System.out.println("\nUsername changed successfully\n");
                        System.out.println(players.get(newUsername).getUsername());
                        System.out.println(players.get(newUsername).getPassword());
                        System.out.println(players.get(newUsername).getRecoveryPhrase());
                        return true;
                    }
                    else System.out.println("\nERROR: This username is already in use\n"); //Extension 1c: the new password is already in use
                }
                else System.out.println("\nERROR: The old and the new username are the same\n"); //Extension 1b: the old and new username are the same
            }
            else //Extension 1d: incorrect password, the player can recover the password with the recoveryPhrase
            {
                System.out.println("\nERROR: Incorrect password\n");
                System.out.println("Do you want to reestablish your password?");
                System.out.println("1- Yes");
                System.out.println("2- No");
                int chosenOption = scanner.nextInt();
                scanner.nextLine(); //consume console buffer
                if (chosenOption == 1)
                {
                    reestablishPassword();
                    return true;
                }
                else return false;
            }
        }
        else System.out.println("\nERROR: There is no profile with this username\n"); //Extension 1a: No profile with this username exists

        return false;
    }

    //return true if the password was changed without any problems
    //return false if the username and the name don't match, if the oldPassword does not match wit the actual password
    //or if the new and old passwords are the same
    public boolean changePassword() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username))
        {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password))
            {
                System.out.print("New password: ");
                String newPassword = scanner.nextLine();
                if (passwordIsSafe(newPassword))
                {
                    if (!password.equals(newPassword))
                    {
                        players.get(username).changePassword(newPassword);
                        System.out.println("\nPassword changed successfully\n");
                        System.out.println(players.get(username).getUsername());
                        System.out.println(players.get(username).getPassword());
                        System.out.println(players.get(username).getRecoveryPhrase());
                        return true;
                    }
                    else System.out.println("\nERROR: The old and the new password are the same\n"); //Extension 1c: old and new password are the same
                }
                else System.out.println("\nERROR: The password doesn't meet the minimum safety requirements: 8 characters and at least 1 upper case letter and 1 number\n"); //Extension 1b: password doesn't meet minimum safety requirements
            }
            else //Extension 1d: incorrect password, the player can recover the password with the recoveryPhrase
            {
                System.out.println("\nERROR: Incorrect password\n");
                System.out.println("Do you want to reestablish your password?");
                System.out.println("1- Yes");
                System.out.println("2- No");
                int chosenOption = scanner.nextInt();
                scanner.nextLine(); //consume console buffer
                if (chosenOption == 1)
                {
                    reestablishPassword();
                    return true;
                }
                else return false;
            }
        }
        else System.out.println("\nERROR: There is no profile with this username\n"); //Extension 1a: No profile with this username exists
        return false;
    }

    public boolean reestablishPassword() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username))
        {
            System.out.print("What's your favourite color? (Recovery phrase): ");
            String recoveryPhrase = scanner.nextLine();
            if (correctRecoveryPhrase(username, recoveryPhrase))
            {
                System.out.print("New password: ");
                String newPassword = scanner.nextLine();
                if (passwordIsSafe(newPassword))
                {
                    System.out.print("New password again: ");
                    String newPassword2 = scanner.nextLine();
                    if (newPassword.equals(newPassword2))
                    {
                        players.get(username).changePassword(newPassword);
                        System.out.println("\nPassword reestablished successfully\n");
                        System.out.println(players.get(username).getUsername());
                        System.out.println(players.get(username).getPassword());
                        System.out.println(players.get(username).getRecoveryPhrase());
                        return true;
                    }
                    else System.out.println("\nERROR: The passwords don't match\n"); //Extension 3a: the passwords don't match
                }
                else System.out.println("\nERROR: The password doesn't meet the minimum safety requirements: 8 characters and at least 1 upper case letter and 1 number\n"); //Extension 3b: password doesn't meet minimum safety requirements
            }
            else System.out.println("\nERROR: The recovery phrase is not correct\n"); //Extension 1b: incorrect recovery phrase
        }
        else System.out.println("\nERROR: There is no profile with this username\n"); //Extension 1a: No profile with this username exists
        return false;
    }

    public boolean eraseProfile()
    {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username))
        {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password))
            {
                System.out.println("Do you really want to permanently delete your profile?");
                System.out.println("1- Yes");
                System.out.println("2- No");
                int chosenOption = scanner.nextInt();
                scanner.nextLine(); //consume console buffer
                if (chosenOption == 1)
                {
                    players.remove(username);
                    System.out.println("\nProfile deleted successfully\n");
                }
                else System.out.println("\nYour profile won't be deleted\n"); //Extension 2: The player chooses to not delete the profile
                return true;
            }
            else //Extension 1b: incorrect password, the player can recover the password with the recoveryPhrase
            {
                System.out.println("\nERROR: Incorrect password\n");
                System.out.println("Do you want to reestablish your password?");
                System.out.println("1- Yes");
                System.out.println("2- No");
                int chosenOption = scanner.nextInt();
                scanner.nextLine(); //consume console buffer
                if (chosenOption == 1)
                {
                    reestablishPassword();
                    return true;
                }
                else return false;
            }
        }
        else System.out.println("\nERROR: There is no profile with this username\n"); //Extension 1a: No profile with this username exists
        return false;
    }
}