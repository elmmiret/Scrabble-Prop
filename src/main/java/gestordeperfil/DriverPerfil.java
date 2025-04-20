package gestordeperfil;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Clase principal para la gestión interactiva de perfiles mediante consola.
 * Proporciona un menú para crear, eliminar y modificar perfiles de usuario.
 *
 * @author Marc Ribas Acon
 */
public class DriverPerfil {

    private Scanner lector;
    private GestorDePerfil gestorDePerfil;

    /**
     * Construye un DriverPerfil asociado a un GestorDePerfil específico.
     * Inicializa el scanner para lectura de entrada del usuario.
     *
     * @param gdp el gestor de perfiles a utilizar
     * @param scanner fuente de entrada para interactuar con el usuario
     */
    // Modified constructor
    public DriverPerfil(GestorDePerfil gdp, Scanner scanner) {
        gestorDePerfil = gdp;
        lector = scanner;
    }

    /**
     * Maneja el proceso de creación de un nuevo perfil.
     * Solicita username, password y frase de recuperación, verificando requisitos.
     *
     * @return true si el perfil fue creado exitosamente, false en caso contrario
     */
    public boolean nuevoPerfil() {
        System.out.print("\n");
        System.out.print("Username: ");
        String username = lector.nextLine();
        if (!gestorDePerfil.existeJugador(username)) {
            System.out.print("Password (mínimo 8 carácteres, 1 mayúscula y 1 número): ");
            String password = lector.nextLine();
            if (gestorDePerfil.esPasswordSegura(password)) {
                System.out.print("Password otra vez: ");
                String password2 = lector.nextLine();
                if (password.equals(password2)) {
                    System.out.print("Cuál es tu color favorito? (Frase de recuperación): ");
                    String recoveryPhrase = lector.nextLine();
                    gestorDePerfil.crearPerfil(username, password, recoveryPhrase);
                    System.out.println("\nPerfil creado correctamente\n");
                    return true;
                } else System.out.println("\nLas passwords no coinciden\n");
            } else System.out.println("\nLa password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)\n");
        } else System.out.println("\nEste username ya está en uso\n");
        return false;
    }

    /**
     * Maneja el proceso de eliminación de un perfil existente.
     * Verifica credenciales antes de proceder con la eliminación.
     *
     * @return true si el perfil fue eliminado o la operación cancelada, false en caso contrario
     */
    public boolean eliminarPerfil() {
        System.out.print("\n");
        System.out.print("Username: ");
        String username = lector.nextLine();
        if (gestorDePerfil.existeJugador(username)) {
            System.out.print("Password: ");
            String password = lector.nextLine();
            if (gestorDePerfil.esPasswordCorrecta(username, password)) {
                System.out.print("\n");
                System.out.println("Borrar permanentemente tu perfil?");
                System.out.println("1- Sí\n2- No\n");
                int chosenOption = lector.nextInt();
                lector.nextLine(); // Limpiar buffer
                if (chosenOption == 1) {
                    gestorDePerfil.eliminarPerfil(username);
                    System.out.println("\nPerfil eliminado correctamente\n");
                }
                else System.out.println("\nEliminación abortada\n");
                return true;
            }
            else {
                System.out.println("\nPassword incorrecta\n");
                return ofrecerRestablecer();
            }
        } else System.out.println("\nNo existe ningún perfil con este username\n");
        return false;
    }

    /**
     * Maneja el cambio de password de un perfil existente.
     * Verifica la password actual y los requisitos de la nueva password.
     *
     * @return true si la password fue cambiada exitosamente, false en caso contrario
     */
    public boolean cambiarPassword() {
        System.out.print("\n");
        System.out.print("Username: ");
        String username = lector.nextLine();
        if (gestorDePerfil.existeJugador(username)) {
            System.out.print("Password: ");
            String password = lector.nextLine();
            if (gestorDePerfil.esPasswordCorrecta(username, password)) {
                System.out.print("Nueva password (mínimo 8 carácteres, 1 mayúscula y 1 número): ");
                String newPassword = lector.nextLine();
                if (gestorDePerfil.esPasswordSegura(newPassword)) {
                    if (!password.equals(newPassword)) {
                        System.out.print("Nueva password otra vez: ");
                        String newPassword2 = lector.nextLine();
                        if (newPassword.equals(newPassword2))
                        {
                            gestorDePerfil.cambiarPassword(username, newPassword);
                            System.out.println("\nPassword cambiada correctamente\n");
                            return true;
                        } else System.out.println("\nLas passwords no coinciden\n");
                    } else System.out.println("\nLa password antigua y nueva son iguales\n");
                } else System.out.println("\nLa password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)\n");
            } else {
                System.out.println("\nPassword incorrecta\n");
                return ofrecerRestablecer();
            }
        } else System.out.println("\nNo existe ningún perfil con este username\n");
        return false;
    }

    /**
     * Maneja el restablecimiento de password mediante frase de recuperación.
     *
     * @return true si la password fue restablecida exitosamente, false en caso contrario
     */
    public boolean restablecerPassword() {
        System.out.print("\n");
        System.out.print("Username: ");
        String username = lector.nextLine();
        if (gestorDePerfil.existeJugador(username)) {
            System.out.print("Cuál es tu color favorito? (Frase de recuperación): ");
            String recoveryPhrase = lector.nextLine();
            if (gestorDePerfil.esFraseRecuperacionCorrecta(username, recoveryPhrase)) {
                System.out.print("Nueva password (mínimo 8 carácteres, 1 mayúscula y 1 número): ");
                String newPassword = lector.nextLine();
                if (gestorDePerfil.esPasswordSegura(newPassword)) {
                    System.out.print("Nueva password otra vez: ");
                    String newPassword2 = lector.nextLine();
                    if (newPassword.equals(newPassword2)) {
                        gestorDePerfil.cambiarPassword(username, newPassword);
                        System.out.println("\nPassword restablecida correctamente\n");
                        return true;
                    } else System.out.println("\nLas passwords no coinciden\n");
                } else System.out.println("\nLa password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)\n");
            } else System.out.println("\nFrase de recuperación incorrecta\n");
        } else System.out.println("\nNo existe ningún perfil con este username\n");
        return false;
    }

    /**
     * Ofrece la opción de restablecer la password tras un fallo de autenticación.
     *
     * @return true si se inicia el proceso de restablecimiento, false en caso contrario
     */
    public boolean ofrecerRestablecer() {
        System.out.println("Restablecer password?");
        System.out.println("1- Sí\n2- No");
        System.out.print("\n");
        int chosenOption = lector.nextInt();
        lector.nextLine(); // Limpiar buffer
        return (chosenOption == 1) ? restablecerPassword() : false;
    }

    /**
     * Maneja el cambio de username de un perfil existente.
     * Verifica credenciales y disponibilidad del nuevo username.
     *
     * @return true si el username fue cambiado exitosamente, false en caso contrario
     */
    public boolean cambiarUsername() {
        System.out.print("\n");
        System.out.print("Username: ");
        String username = lector.nextLine();
        if (gestorDePerfil.existeJugador(username)) {
            System.out.print("Password: ");
            String password = lector.nextLine();
            if (gestorDePerfil.esPasswordCorrecta(username, password)) {
                System.out.print("Nuevo username: ");
                String newUsername = lector.nextLine();
                if (!username.equals(newUsername)) {
                    if (!gestorDePerfil.existeJugador(newUsername)) {
                        gestorDePerfil.cambiarUsername(username, newUsername);
                        System.out.println("\nUsername cambiado correctamente\n");
                        return true;
                    } else System.out.println("\nEste username ya está en uso\n");
                } else System.out.println("\nEl username antiguo y el nuevo son el mismo\n");
            } else {
                System.out.println("\nPassword incorrecta\n");
                return ofrecerRestablecer();
            }
        } else System.out.println("\nNo existe ningún perfil con este username\n");
        return false;
    }

    /**
     * Muestra el menú de gestión de perfiles y maneja las opciones seleccionadas.
     * Permite navegar por las diferentes operaciones disponibles.
     */
    public void profileManagement() {
        System.out.print("\n");
        System.out.println("Por favor, escoja una de las siguientes opciones:");
        System.out.println("1- Crear un nuevo perfil");
        System.out.println("2- Eliminar un perfil");
        System.out.println("3- Cambiar password");
        System.out.println("4- Restablecer password");
        System.out.println("5- Cambiar username");
        System.out.println("6- Atrás");
        System.out.print("\n");
        int chosenOption = lector.nextInt();
        lector.nextLine(); // Limpiar buffer
        boolean operationDone = false;
        switch (chosenOption) {
            case 1: // Crear nuevo perfil
                while (!operationDone) {
                    operationDone = nuevoPerfil();
                    if (!operationDone) {
                        System.out.println("Quieres intentarlo otra vez?");
                        System.out.println("1- Sí");
                        System.out.println("2- No");
                        System.out.print("\n");
                        chosenOption = lector.nextInt();
                        if (chosenOption == 2) operationDone = true;
                        lector.nextLine(); // Limpiar buffer
                    }
                }
                break;

            case 2: // Eliminar perfil
                while (!operationDone) {
                    operationDone = eliminarPerfil();
                    if (!operationDone) {
                        System.out.println("Quieres intentarlo otra vez?");
                        System.out.println("1- Sí");
                        System.out.println("2- No");
                        System.out.print("\n");
                        chosenOption = lector.nextInt();
                        if (chosenOption == 2) operationDone = true;
                        lector.nextLine(); // Limpiar buffer
                    }
                }
                break;

            case 3: // Cambiar password
                while (!operationDone) {
                    operationDone = cambiarPassword();
                    if (!operationDone) {
                        System.out.println("Quieres intentarlo otra vez?");
                        System.out.println("1- Sí");
                        System.out.println("2- No");
                        System.out.print("\n");
                        chosenOption = lector.nextInt();
                        if (chosenOption == 2) operationDone = true;
                        lector.nextLine(); // Limpiar buffer
                    }
                }
                break;

            case 4: // Restablecer password
                while (!operationDone) {
                    operationDone = restablecerPassword();
                    if (!operationDone) {
                        System.out.println("Quieres intentarlo otra vez?");
                        System.out.println("1- Sí");
                        System.out.println("2- No");
                        System.out.print("\n");
                        chosenOption = lector.nextInt();
                        if (chosenOption == 2) operationDone = true;
                        lector.nextLine(); // Limpiar buffer
                    }
                }
                break;

            case 5: // Cambiar username
                while (!operationDone) {
                    operationDone = cambiarUsername();
                    if (!operationDone) {
                        System.out.println("Quieres intentarlo otra vez?");
                        System.out.println("1- Sí");
                        System.out.println("2- No");
                        System.out.print("\n");
                        chosenOption = lector.nextInt();
                        if (chosenOption == 2) operationDone = true;
                        lector.nextLine(); // Limpiar buffer
                    }
                }
                break;

            case 6: // Volver
                break;
            default:
                System.out.println("Opción incorrecta, por favor selecciona una de las opciones ofrecidas");
                break;
        }
    }
}