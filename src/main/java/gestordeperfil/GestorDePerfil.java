package gestordeperfil;

import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages user profiles including creation, deletion, authentication, and password recovery.
 * This class handles profile operations through console interactions and maintains a database
 * of profiles using a HashMap. Implements password security checks and recovery mechanisms.
 *
 * @author Marc Ribas Acon
 */
public class GestorDePerfil {

    private Map<String, Perfil> players;
    Scanner scanner;

    /**
     * Constructs a new profile manager with an empty database.
     * Initializes the scanner for user input and the profile storage map.
     */
    public GestorDePerfil() {
        scanner = new Scanner(System.in);
        players = new HashMap<>();
    }

    /**
     * Validates if the provided password matches the stored password for a username.
     * Case-sensitive comparison.
     *
     * @param username the username to verify
     * @param password the password to check
     * @return true if the provided password matches the user's password, false otherwise
     */
    private boolean correctPassword(String username, String password) {
        return players.get(username).getPassword().equals(password);
    }

    /**
     * Validates if the provided recovery phrase matches the stored phrase for a username.
     * Case-insensitive comparison.
     *
     * @param username the username to verify
     * @param recoveryPhrase the recovery phrase
     * @return true if the provided recovery phrase matches the user's recovery phrase, false otherwise
     */
    private boolean correctRecoveryPhrase(String username, String recoveryPhrase) {
        return players.get(username).getRecoveryPhrase().equalsIgnoreCase(recoveryPhrase);
    }

    /**
     * Checks if a password meets security requirements:
     * - Minimum 8 characters
     * - At least 1 uppercase letter
     * - At least 1 digit
     *
     * @param password the password to validate
     * @return true if the password meets requirements, false otherwise
     */
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

    /**
     * Guides user through profile creation process via console.
     * Validates username uniqueness and password safety requirements.
     *
     * @return true if the profile was successfully created, false if any validation failed
     */
    public boolean createPerfil() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (!players.containsKey(username)) {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (passwordIsSafe(password)) {
                System.out.print("Password again: ");
                String password2 = scanner.nextLine();
                if (password.equals(password2)) {
                    System.out.print("What's your favourite color? (Recovery phrase): ");
                    String recoveryPhrase = scanner.nextLine();
                    players.put(username, new Perfil(username, password, recoveryPhrase));
                    System.out.println("\nProfile created successfully\n");
                    return true;
                } else System.out.println("\nThe passwords don't match\n");
            } else System.out.println("\nThe password doesn't meet the minimum safety requirements\n");
        } else System.out.println("\nThis username is already in use\n");
        return false;
    }

    /**
     * Handles a profile's username change process with authentication checks.
     * Includes password recovery option for failed attempts.
     *
     * @return true if the username was changed or the password was recovered, false otherwise
     */
    public boolean changeUsername() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username)) {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password)) {
                System.out.print("New username: ");
                String newUsername = scanner.nextLine();
                if (!username.equals(newUsername)) {
                    if (!players.containsKey(newUsername)) {
                        Perfil player = players.get(username);
                        player.changeUsername(newUsername);
                        players.remove(username);
                        players.put(newUsername, player);
                        System.out.println("\nUsername changed successfully\n");
                        return true;
                    } else System.out.println("\nThis username is already in use\n");
                } else System.out.println("\nThe old and new username are the same\n");
            } else {
                System.out.println("\nIncorrect password\n");
                return handlePasswordRecovery();
            }
        } else System.out.println("\nNo profile with this username\n");
        return false;
    }

    /**
     * Manages a profile's password change process with security validations.
     * Includes recovery flow for failed authentication attempts.
     *
     * @return true if the password was changed or recovered, false otherwise
     */
    public boolean changePassword() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username)) {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password)) {
                System.out.print("New password: ");
                String newPassword = scanner.nextLine();
                if (passwordIsSafe(newPassword)) {
                    if (!password.equals(newPassword)) {
                        players.get(username).changePassword(newPassword);
                        System.out.println("\nPassword changed successfully\n");
                        return true;
                    } else System.out.println("\nOld and new password are the same\n");
                } else System.out.println("\nPassword doesn't meet requirements\n");
            } else {
                System.out.println("\nIncorrect password\n");
                return handlePasswordRecovery();
            }
        } else System.out.println("\nNo profile with this username\n");
        return false;
    }

    /**
     * Resets password using recovery phrase authentication.
     * Validates new password safety and confirmation.
     *
     * @return true if password was successfully reset, false otherwise
     */
    public boolean reestablishPassword() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username)) {
            System.out.print("What's your favourite color? (Recovery phrase): ");
            String recoveryPhrase = scanner.nextLine();
            if (correctRecoveryPhrase(username, recoveryPhrase)) {
                System.out.print("New password: ");
                String newPassword = scanner.nextLine();
                if (passwordIsSafe(newPassword)) {
                    System.out.print("New password again: ");
                    String newPassword2 = scanner.nextLine();
                    if (newPassword.equals(newPassword2)) {
                        players.get(username).changePassword(newPassword);
                        System.out.println("\nPassword reestablished successfully\n");
                        return true;
                    } else System.out.println("\nPasswords don't match\n");
                } else System.out.println("\nPassword doesn't meet requirements\n");
            } else System.out.println("\nIncorrect recovery phrase\n");
        } else System.out.println("\nNo profile with this username\n");
        return false;
    }

    /**
     * Deletes a profile after confirmation and authentication.
     * Provides recovery option for failed password attempts.
     *
     * @return true if the profile was deleted or process completed, false on critical errors
     */
    public boolean eraseProfile() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        if (players.containsKey(username)) {
            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (correctPassword(username, password)) {
                System.out.println("Permanently delete your profile?");
                System.out.println("1- Yes\n2- No");
                int chosenOption = scanner.nextInt();
                scanner.nextLine(); // Clear buffer
                if (chosenOption == 1) {
                    players.remove(username);
                    System.out.println("\nProfile deleted successfully\n");
                } else System.out.println("\nDeletion canceled\n");
                return true;
            } else {
                System.out.println("\nIncorrect password\n");
                return handlePasswordRecovery();
            }
        } else System.out.println("\nNo profile with this username\n");
        return false;
    }

    /**
     * Handles password recovery workflow for failed authentication attempts and calls the function that handles it
     * @return true if password was recovered, false if user declined
     */
    private boolean handlePasswordRecovery() {
        System.out.println("Recover password?");
        System.out.println("1- Yes\n2- No");
        int chosenOption = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        return (chosenOption == 1) ? reestablishPassword() : false;
    }
}