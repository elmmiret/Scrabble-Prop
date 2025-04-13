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
        return players.get(username).getPassword().equals(password);
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
                    System.out.print("Passphrase: ");
                    String passphrase = scanner.nextLine();
                    players.put(username, new Perfil(username, password, passphrase));
                    System.out.println(players.get(username).getUsername());
                    System.out.println(players.get(username).getPassword());
                    System.out.println(players.get(username).getRecoveryPhrase());
                    return true;
                }
                else System.out.println("ERROR: The passwords don't match"); //Extension 1b: two passwords don't match
            }
            else System.out.println("ERROR: The password doesn't meet the minimum safety requirements: 8 characters and at least 1 upper case letter and 1 number"); //Extension 1c: password doesn't meet minimum safety requirements
        }
        else System.out.println("ERROR: This username is already in use"); //Extension 1a: profile already exists

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
                        System.out.println(players.get(newUsername).getUsername());
                        System.out.println(players.get(newUsername).getPassword());
                        System.out.println(players.get(newUsername).getRecoveryPhrase());
                        return true;
                    }
                    else System.out.println("ERROR: This username is already in use"); //Extension 1c: the new password is already in use
                }
                else System.out.println("ERROR: The old and the new username are the same"); //Extension 1b: the old and new username are the same
            }
            else //Extension 1d: incorrect password, the player can recover the password with the passphrase
            {
                System.out.println("ERROR: Incorrect password");
                System.out.println("Do you want to reestablish your password?");
                //LLEVAR A REESTABLISH PASSWORD CUANDO ESTE HECHO !!!!!!!!!!!!!!!!!!!!
            }
        }
        else System.out.println("ERROR: There is no profile with this username"); //Extension 1a: No profile with this username exists

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
                        System.out.println(players.get(username).getUsername());
                        System.out.println(players.get(username).getPassword());
                        System.out.println(players.get(username).getRecoveryPhrase());
                        return true;
                    }
                    else System.out.println("ERROR: The old and the new password are the same"); //Extension 1c: old and new password are the same
                }
                else System.out.println("ERROR: The password doesn't meet the minimum safety requirements: 8 characters and at least 1 upper case letter and 1 number"); //Extension 1b: password doesn't meet minimum safety requirements
            }
            else //Extension 1d: incorrect password, the player can recover the password with the passphrase
            {
                System.out.println("ERROR: Incorrect password");
                System.out.println("Do you want to reestablish your password?");
                //LLEVAR A REESTABLISH PASSWORD CUANDO ESTE HECHO !!!!!!!!!!!!!!!!!!!!
            }
        }
        else System.out.println("ERROR: There is no profile with this username"); //Extension 1a: No profile with this username exists
        return false;
    }

    public boolean reestablishPassword(Perfil perfil, String recovery, String password) {
        String perfilRecovery = perfil.getRecoveryPhrase();
        if (!perfilRecovery.equalsIgnoreCase(recovery)) return false;
        perfil.changePassword(password);
        return true;
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
                    return true;
                }
                else
                {
                    System.out.println("\nYour profile won't be deleted\n"); //Extension 2: The player chooses to not delete the profile
                    return false;
                }
            }
            else //Extension 1b: incorrect password, the player can recover the password with the passphrase
            {
                System.out.println("ERROR: Incorrect password");
                System.out.println("Do you want to reestablish your password?");
                //LLEVAR A REESTABLISH PASSWORD CUANDO ESTE HECHO !!!!!!!!!!!!!!!!!!!!
            }
        }
        else System.out.println("ERROR: There is no profile with this username"); //Extension 1a: No profile with this username exists
        return false;
    }
}