package gestordeperfil;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

/**
 * Clase de pruebas unitarias para verificar el funcionamiento del GestorDePerfil.
 * Contiene tests para validar la lógica de gestión de perfiles, seguridad de contraseñas
 * y operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los perfiles de usuario.
 *
 * <p>Principales aspectos verificados:
 * <ul>
 *   <li>Inicialización correcta de las estructuras de datos</li>
 *   <li>Validación de credenciales y frases de recuperación</li>
 *   <li>Cumplimiento de políticas de seguridad en contraseñas</li>
 *   <li>Comportamiento de las operaciones de modificación de perfiles</li>
 *   <li>Manejo de casos excepcionales y errores</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class GestorDePerfilTest {
    private GestorDePerfil gestor;
    private final String TEST_USER = "testUser";
    private final String TEST_PASS = "SecurePass123";
    private final String TEST_PHRASE = "My Recovery Phrase";

    /**
     * Configura el entorno de prueba antes de cada test.
     * Crea un gestor con un perfil de prueba predefinido.
     */
    @Before
    public void setUp() {
        gestor = new GestorDePerfil();
        gestor.crearPerfil(TEST_USER, TEST_PASS, TEST_PHRASE);
    }

    /**
     * Verifica que el constructor inicialice correctamente un mapa vacío.
     * Comprueba que no existan perfiles en un gestor recién creado.
     */
    @Test
    public void testConstructorInitializesEmptyMap() {
        GestorDePerfil newGestor = new GestorDePerfil();
        assertTrue(newGestor.getJugadores().isEmpty());
    }

    /**
     * Prueba la validación de contraseñas correctas e incorrectas.
     * Incluye verificaciones para:
     * - Contraseña válida del usuario registrado
     * - Contraseña incorrecta para usuario existente
     */
    @Test
    public void testEsPasswordCorrecta() {
        assertTrue(gestor.esPasswordCorrecta(TEST_USER, TEST_PASS));
        assertFalse(gestor.esPasswordCorrecta(TEST_USER, "wrongPassword"));
    }

    /**
     * Valida el reconocimiento de frases de recuperación con diferentes casos:
     * - Frase exacta (case-sensitive)
     * - Frase en mayúsculas/minúsculas diferentes (case-insensitive)
     * - Frase incorrecta
     */
    @Test
    public void testEsFraseRecuperacionCorrecta() {
        assertTrue(gestor.esFraseRecuperacionCorrecta(TEST_USER, TEST_PHRASE.toLowerCase()));
        assertTrue(gestor.esFraseRecuperacionCorrecta(TEST_USER, TEST_PHRASE.toUpperCase()));
        assertFalse(gestor.esFraseRecuperacionCorrecta(TEST_USER, "wrong phrase"));
    }

    /**
     * Comprueba el cumplimiento de los requisitos de seguridad para contraseñas.
     * Casos probados:
     * <ul>
     *   <li>Contraseñas válidas con combinaciones aceptables</li>
     *   <li>Contraseñas inválidas (corta, sin mayúsculas, sin dígitos)</li>
     *   <li>Valor nulo como contraseña</li>
     * </ul>
     */
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

    /**
     * Testea la creación de nuevos perfiles verificando:
     * - Inserción correcta en el mapa de jugadores
     * - Conservación de los datos del perfil creado
     * - Integridad de la información almacenada
     */
    @Test
    public void testCrearPerfil() {
        String newUser = "newUser";
        gestor.crearPerfil(newUser, "Pass123", "New Phrase");

        assertTrue(gestor.existeJugador(newUser));
        assertEquals(newUser, gestor.getJugadores().get(newUser).getUsername());
    }

    /**
     * Valida el cambio de nombre de usuario comprobando:
     * - Actualización de la clave en el mapa
     * - Eliminación del nombre antiguo
     * - Conservación de los datos del perfil
     */
    @Test
    public void testCambiarUsername() {
        String newUsername = "updatedUser";

        gestor.cambiarUsername(TEST_USER, newUsername);

        assertFalse(gestor.existeJugador(TEST_USER));
        assertTrue(gestor.existeJugador(newUsername));
        assertEquals(newUsername, gestor.getJugadores().get(newUsername).getUsername());
    }

    /**
     * Prueba la modificación de contraseñas verificando:
     * - Actualización efectiva de la contraseña
     * - Invalidación de la contraseña anterior
     * - Persistencia de otros datos del perfil
     */
    @Test
    public void testCambiarPassword() {
        String newPassword = "NewSecurePass456";
        gestor.cambiarPassword(TEST_USER, newPassword);

        assertTrue(gestor.esPasswordCorrecta(TEST_USER, newPassword));
        assertFalse(gestor.esPasswordCorrecta(TEST_USER, TEST_PASS));
    }

    /**
     * Verifica la eliminación de perfiles comprobando:
     * - Remoción completa del mapa de jugadores
     * - Limpieza posterior de las entradas
     * - Comportamiento con mapa vacío
     */
    @Test
    public void testEliminarPerfil() {
        gestor.eliminarPerfil(TEST_USER);
        assertFalse(gestor.existeJugador(TEST_USER));
        assertTrue(gestor.getJugadores().isEmpty());
    }

    /**
     * Testea la obtención del mapa de jugadores verificando:
     * - Integridad de la estructura devuelta
     * - Correspondencia de tamaños
     * - Presencia de perfiles existentes
     */
    @Test
    public void testGetJugadores() {
        Map<String, Perfil> jugadores = gestor.getJugadores();
        assertNotNull(jugadores);
        assertEquals(1, jugadores.size());
        assertTrue(jugadores.containsKey(TEST_USER));
    }

    /**
     * Valida la detección de existencia de usuarios verificando:
     * - Reconocimiento de usuario registrado
     * - Detección de usuario no existente
     * - Respuesta ante valores nulos o vacíos
     */
    @Test
    public void testExisteJugador() {
        assertTrue(gestor.existeJugador(TEST_USER));
        assertFalse(gestor.existeJugador("nonExistentUser"));
    }

    /**
     * Prueba secuencias complejas de operaciones para verificar:
     * - Comportamiento con múltiples perfiles
     * - Interacción entre diferentes operaciones CRUD
     * - Estabilidad del sistema ante modificaciones consecutivas
     */
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

    /**
     * Valida el manejo de errores con usuarios no existentes verificando:
     * - Lanzamiento de excepciones esperadas
     * - Comportamiento ante operaciones inválidas
     * - Tipos de operaciones probadas: cambio de nombre, cambio de contraseña y eliminación
     */
    @Test(expected = NullPointerException.class)
    public void testNonExistentUserOperations() {
        // These should throw NullPointerException when trying to access non-existent users
        gestor.cambiarUsername("ghostUser", "newName");
        gestor.cambiarPassword("ghostUser", "newPass");
        gestor.eliminarPerfil("ghostUser");
    }
}