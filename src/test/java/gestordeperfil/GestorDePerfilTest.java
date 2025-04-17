package gestordeperfil;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;


public class GestorDePerfilTest {
    private GestorDePerfil gestor;
    private final String TEST_USER = "testUser";
    private final String TEST_PASS = "SecurePass123";
    private final String TEST_PHRASE = "My Recovery Phrase";

    @Before
    public void setUp() {
        gestor = new GestorDePerfil();
        gestor.crearPerfil(TEST_USER, TEST_PASS, TEST_PHRASE);
    }

    @Test
    public void testConstructorInitializesEmptyMap() {
        GestorDePerfil newGestor = new GestorDePerfil();
        assertTrue(newGestor.getJugadores().isEmpty());
    }

    @Test
    public void testEsPasswordCorrecta() {
        assertTrue(gestor.esPasswordCorrecta(TEST_USER, TEST_PASS));
        assertFalse(gestor.esPasswordCorrecta(TEST_USER, "wrongPassword"));
    }

    @Test
    public void testEsFraseRecuperacionCorrecta() {
        assertTrue(gestor.esFraseRecuperacionCorrecta(TEST_USER, TEST_PHRASE.toLowerCase()));
        assertTrue(gestor.esFraseRecuperacionCorrecta(TEST_USER, TEST_PHRASE.toUpperCase()));
        assertFalse(gestor.esFraseRecuperacionCorrecta(TEST_USER, "wrong phrase"));
    }

    @Test
    public void testEsPasswordSegura() {
        // Valid passwords
        assertTrue(gestor.esPasswordSegura("Secure123"));
        assertTrue(gestor.esPasswordSegura("LONGpasswordWith123"));

        // Invalid passwords
        assertFalse(gestor.esPasswordSegura("weak"));
        assertFalse(gestor.esPasswordSegura("nouppercase123"));
        assertFalse(gestor.esPasswordSegura("NODIGITS"));
        assertFalse(gestor.esPasswordSegura("Sh0rt"));
        assertFalse(gestor.esPasswordSegura(null));
    }

    @Test
    public void testCrearPerfil() {
        String newUser = "newUser";
        gestor.crearPerfil(newUser, "Pass123", "New Phrase");

        assertTrue(gestor.existeJugador(newUser));
        assertEquals(newUser, gestor.getJugadores().get(newUser).getUsername());
    }

    @Test
    public void testCambiarUsername() {
        String newUsername = "updatedUser";

        gestor.cambiarUsername(TEST_USER, newUsername);

        assertFalse(gestor.existeJugador(TEST_USER));
        assertTrue(gestor.existeJugador(newUsername));
        assertEquals(newUsername, gestor.getJugadores().get(newUsername).getUsername());
    }

    @Test
    public void testCambiarPassword() {
        String newPassword = "NewSecurePass456";
        gestor.cambiarPassword(TEST_USER, newPassword);

        assertTrue(gestor.esPasswordCorrecta(TEST_USER, newPassword));
        assertFalse(gestor.esPasswordCorrecta(TEST_USER, TEST_PASS));
    }

    @Test
    public void testEliminarPerfil() {
        gestor.eliminarPerfil(TEST_USER);
        assertFalse(gestor.existeJugador(TEST_USER));
        assertTrue(gestor.getJugadores().isEmpty());
    }

    @Test
    public void testGetJugadores() {
        Map<String, Perfil> jugadores = gestor.getJugadores();
        assertNotNull(jugadores);
        assertEquals(1, jugadores.size());
        assertTrue(jugadores.containsKey(TEST_USER));
    }

    @Test
    public void testExisteJugador() {
        assertTrue(gestor.existeJugador(TEST_USER));
        assertFalse(gestor.existeJugador("nonExistentUser"));
    }

    @Test
    public void testMultipleOperations() {
        // Create second profile
        gestor.crearPerfil("user2", "Pass123!", "Second Phrase");

        // Test existence
        assertTrue(gestor.existeJugador("user2"));
        assertEquals(2, gestor.getJugadores().size());

        // Change username of second profile
        gestor.cambiarUsername("user2", "updatedUser2");
        assertFalse(gestor.existeJugador("user2"));
        assertTrue(gestor.existeJugador("updatedUser2"));

        // Delete first profile
        gestor.eliminarPerfil(TEST_USER);
        assertFalse(gestor.existeJugador(TEST_USER));
        assertEquals(1, gestor.getJugadores().size());
    }

    @Test(expected = NullPointerException.class)
    public void testNonExistentUserOperations() {
        // These should throw NullPointerException when trying to access non-existent users
        gestor.cambiarUsername("ghostUser", "newName");
        gestor.cambiarPassword("ghostUser", "newPass");
        gestor.eliminarPerfil("ghostUser");
    }
}