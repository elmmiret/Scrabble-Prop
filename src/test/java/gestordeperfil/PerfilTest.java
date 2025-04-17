package gestordeperfil;
        
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas unitarias para verificar el correcto funcionamiento de la clase Perfil.
 * Contiene tests para todos los métodos y casos de uso principales del sistema de gestión de perfiles.
 *
 * <p>Las pruebas cubren:
 * <ul>
 *   <li>Inicialización correcta de campos en el constructor</li>
 *   <li>Modificación de credenciales de usuario</li>
 *   <li>Actualización de estadísticas de juego</li>
 *   <li>Gestión de puntos acumulados</li>
 *   <li>Comportamiento de campos inmutables</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class PerfilTest {

    private Perfil perfil;

    /**
     * Configura el entorno de pruebas creando un perfil de ejemplo antes de cada test.
     */
    @Before
    public void setUp() {
        perfil = new Perfil("user1", "Testeando12", "my recovery phrase");
    }

    /**
     * Verifica que el constructor inicialice correctamente todos los campos del perfil.
     * Comprueba que los valores de usuario, password, frase de recuperación y estadísticas
     * coincidan con los valores proporcionados en la creación.
     */
    @Test
    public void testConstructorInitialization() {
        assertEquals("user1", perfil.getUsername());
        assertEquals("Testeando12", perfil.getPassword());
        assertEquals("my recovery phrase", perfil.getFraseRecuperacion());
        assertEquals(0, perfil.getPartidasJugadas());
        assertEquals(0, perfil.getPartidasGanadas());
        assertEquals(0, perfil.getPartidasPerdidas());
        assertEquals(0, perfil.getPuntos());
    }

    /**
     * Comprueba el funcionamiento del cambio de nombre de usuario.
     * Verifica que el nuevo username se almacene correctamente y sea accesible mediante el getter.
     */
    @Test
    public void testCambiarUsername() {
        perfil.cambiarUsername("newUser");
        assertEquals("newUser", perfil.getUsername());
    }

    /**
     * Testea la modificación de la contraseña del perfil.
     * Confirma que la nueva contraseña sea actualizada y persistida correctamente.
     */
    @Test
    public void testCambiarPassword() {
        perfil.cambiarPassword("newPassword");
        assertEquals("newPassword", perfil.getPassword());
    }

    /**
     * Valida el incremento del contador de partidas jugadas.
     * Verifica que el contador aumente correctamente tras múltiples incrementos.
     */
    @Test
    public void testIncrementarPartidasJugadas() {
        perfil.incrementarPartidasJugadas(1);
        assertEquals(1, perfil.getPartidasJugadas());
        perfil.incrementarPartidasJugadas(1);
        assertEquals(2, perfil.getPartidasJugadas());
    }

    /**
     * Confirma el correcto funcionamiento del contador de partidas ganadas.
     * Comprueba que los incrementos sucesivos se reflejen en el valor del contador.
     */
    @Test
    public void testIncrementarPartidasGanadas() {
        perfil.incrementarPartidasGanadas(1);
        assertEquals(1, perfil.getPartidasGanadas());
        perfil.incrementarPartidasGanadas(1);
        assertEquals(2, perfil.getPartidasGanadas());
    }

    /**
     * Prueba el incremento del contador de partidas perdidas.
     * Verifica que el contador actualice su valor tras múltiples operaciones de incremento.
     */
    @Test
    public void testIncrementarPartidasPerdidas() {
        perfil.incrementarPartidasPerdidas(1);
        assertEquals(1, perfil.getPartidasPerdidas());
        perfil.incrementarPartidasPerdidas(1);
        assertEquals(2, perfil.getPartidasPerdidas());
    }

    /**
     * Testea la acumulación de puntos con valores positivos y negativos.
     * Verifica:
     * <ol>
     *   <li>Suma correcta de puntos positivos</li>
     *   <li>Resta adecuada con valores negativos</li>
     *   <li>Acumulación correcta en operaciones sucesivas</li>
     * </ol>
     */
    @Test
    public void testIncrementarPuntos() {
        perfil.incrementarPuntos(10);
        assertEquals(10, perfil.getPuntos());
        perfil.incrementarPuntos(-5);
        assertEquals(5, perfil.getPuntos());
        perfil.incrementarPuntos(3);
        assertEquals(8, perfil.getPuntos());
    }

    /**
     * Valida la interacción entre múltiples operaciones de incremento.
     * Comprueba que los diferentes contadores y puntos se actualicen correctamente
     * cuando se modifican simultáneamente.
     */
    @Test
    public void testMultipleIncrementsAndGetters() {
        perfil.incrementarPartidasJugadas(1);
        perfil.incrementarPartidasGanadas(1);
        perfil.incrementarPartidasPerdidas(1);
        perfil.incrementarPuntos(15);

        assertEquals(1, perfil.getPartidasJugadas());
        assertEquals(1, perfil.getPartidasGanadas());
        assertEquals(1, perfil.getPartidasPerdidas());
        assertEquals(15, perfil.getPuntos());
    }

    /**
     * Confirma la inmutabilidad de la frase de recuperación.
     * Verifica que la frase de recuperación no se modifique al cambiar otros campos del perfil.
     */
    @Test
    public void testFraseRecuperacionRemainsUnchanged() {
        perfil.cambiarUsername("anotherUser");
        perfil.cambiarPassword("anotherPass");
        assertEquals("my recovery phrase", perfil.getFraseRecuperacion());
    }
}