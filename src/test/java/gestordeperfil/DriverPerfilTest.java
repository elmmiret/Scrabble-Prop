package gestordeperfil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;

public class DriverPerfilTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private GestorDePerfil gestor;
    private DriverPerfil driver;

    @Before
    public void setUp() {
        gestor = new GestorDePerfil();
        driver = new DriverPerfil(gestor);
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }

    @Test
    public void testNuevoPerfilSuccess() {
        String input = "newUser\nSecurePass123\nSecurePass123\nblue\n";
        provideInput(input);

        boolean result = driver.nuevoPerfil();

        assertTrue(result);
        assertTrue(gestor.existeJugador("newUser"));
        assertTrue(outContent.toString().contains("Profile created successfully"));
    }

    @Test
    public void testNuevoPerfilExistingUsername() {
        gestor.crearPerfil("existingUser", "Pass123", "red");
        String input = "existingUser\nanypass\nanypass\nanyphrase\n";
        provideInput(input);

        boolean result = driver.nuevoPerfil();

        assertFalse(result);
        assertTrue(outContent.toString().contains("Este username ya está en uso"));
    }

    @Test
    public void testEliminarPerfilSuccess() {
        gestor.crearPerfil("deleteUser", "Pass123", "phrase");
        String input = "deleteUser\nPass123\n1\n";
        provideInput(input);

        boolean result = driver.eliminarPerfil();

        assertTrue(result);
        assertFalse(gestor.existeJugador("deleteUser"));
        assertTrue(outContent.toString().contains("Perfil eliminado correctamente"));
    }

    @Test
    public void testCambiarPasswordSuccess() {
        gestor.crearPerfil("user1", "oldPass", "phrase");
        String input = "user1\noldPass\nNewPass123\n";
        provideInput(input);

        boolean result = driver.cambiarPassword();

        assertTrue(result);
        assertTrue(gestor.esPasswordCorrecta("user1", "NewPass123"));
    }

    @Test
    public void testReestablecerPasswordSuccess() {
        gestor.crearPerfil("user1", "oldPass", "blue");
        String input = "user1\nblue\nNewPass123\nNewPass123\n";
        provideInput(input);

        boolean result = driver.reestablecerPassword();

        assertTrue(result);
        assertTrue(gestor.esPasswordCorrecta("user1", "NewPass123"));
    }

    @Test
    public void testCambiarUsernameSuccess() {
        gestor.crearPerfil("oldUser", "pass123", "phrase");
        String input = "oldUser\npass123\nnewUser\n";
        provideInput(input);

        boolean result = driver.cambiarUsername();

        assertTrue(result);
        assertTrue(gestor.existeJugador("newUser"));
        assertFalse(gestor.existeJugador("oldUser"));
    }

    @Test
    public void testProfileManagementCreateOption() {
        String input = "1\nnewUser\nValidPass1\nValidPass1\nblue\n2\n";
        provideInput(input);

        driver.profileManagement();

        assertTrue(gestor.existeJugador("newUser"));
        assertTrue(outContent.toString().contains("Profile created successfully"));
    }

    @Test
    public void testProfileManagementDeleteOption() {
        gestor.crearPerfil("deleteMe", "pass123", "phrase");
        String input = "2\ndeleteMe\npass123\n1\n2\n";
        provideInput(input);

        driver.profileManagement();

        assertFalse(gestor.existeJugador("deleteMe"));
    }

    @Test
    public void testPasswordSecurityValidation() {
        String input = "weakuser\nweak\nweak\nred\n";
        provideInput(input);

        driver.nuevoPerfil();

        assertTrue(outContent.toString().contains("no cumple los requisitos mínimos"));
        assertFalse(gestor.existeJugador("weakuser"));
    }

    @Test
    public void testPasswordMismatchHandling() {
        String input = "mismatchUser\nPass123\nDifferentPass\n";
        provideInput(input);

        driver.nuevoPerfil();

        assertTrue(outContent.toString().contains("Las passwords no coinciden"));
        assertFalse(gestor.existeJugador("mismatchUser"));
    }

    @Test
    public void testRecoveryFlowAfterFailedAuth() {
        gestor.crearPerfil("recoveryUser", "pass123", "blue");
        String input = "recoveryUser\nwrongpass\n1\nblue\nNewPass123\nNewPass123\n";
        provideInput(input);

        driver.eliminarPerfil();

        assertTrue(outContent.toString().contains("Password reestablecida correctamente"));
        assertTrue(gestor.esPasswordCorrecta("recoveryUser", "NewPass123"));
    }

    @Test
    public void testMenuNavigationWithInvalidOption() {
        String input = "99\n6\n";
        provideInput(input);

        driver.profileManagement();

        assertTrue(outContent.toString().contains("Opción incorrecta"));
    }

    @Test
    public void testRetryMechanismInProfileCreation() {
        String input = "1\ninvalidUser\nWeak1\nWeak1\nred\n2\n" +  // First attempt fails
                "1\nvalidUser\nValidPass1\nValidPass1\ngreen\n2\n";  // Second attempt succeeds
        provideInput(input);

        driver.profileManagement();

        assertTrue(gestor.existeJugador("validUser"));
        assertFalse(gestor.existeJugador("invalidUser"));
    }
}