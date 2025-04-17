package gestordeperfil;

import org.junit.Before;
import org.junit.Test;
import ranking.Ranking;

import static org.junit.Assert.*;
import java.io.*;

/**
 * Clase de pruebas para el sistema de gestión interactiva de perfiles mediante consola.
 * Verifica el funcionamiento de las operaciones principales a través de simulaciones de entrada de usuario.
 *
 * <p>Características principales probadas:
 * <ul>
 *   <li>Flujos completos de interacción con el usuario</li>
 *   <li>Manejo correcto de entradas válidas e inválidas</li>
 *   <li>Integración entre la interfaz de usuario y el gestor de perfiles</li>
 *   <li>Secuencias complejas de operaciones</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class DriverPerfilTest
{
    private GestorDePerfil gestor;
    private DriverPerfil driver;

    /**
     * Configura el entorno de pruebas inicializando el gestor de perfiles antes de cada test.
     */
    @Before
    public void setUp()
    {
        Ranking ranking = new Ranking();
        gestor = new GestorDePerfil(ranking);
    }

    /**
     * Crea una instancia del controlador con entrada de datos simulada.
     * @param input Cadena que simula la entrada del usuario (separados por saltos de línea)
     * @return Instancia del driver configurada con la entrada simulada
     */
    private DriverPerfil createDriverWithInput(String input)
    {
        return new DriverPerfil(
                gestor,
                new ByteArrayInputStream(input.getBytes())
        );
    }

    /**
     * Prueba el proceso completo de creación de un nuevo perfil verificando:
     * - Registro exitoso con credenciales válidas
     * - Verificación de requisitos de seguridad en contraseña
     * - Confirmación de password mediante doble entrada
     * - Almacenamiento correcto en el sistema
     */
    @Test
    public void testNuevoPerfil()
    {
        String input = "newUser\nSecurePass123\nSecurePass123\nblue\n";
        driver = createDriverWithInput(input);

        assertTrue(driver.nuevoPerfil());
        assertTrue(gestor.existeJugador("newUser"));
    }

    /**
     * Valida el proceso de eliminación de perfiles comprobando:
     * - Autenticación requerida previa a la eliminación
     * - Confirmación explícita del usuario
     * - Eliminación completa de los registros
     * - Comportamiento post-eliminación
     */
    @Test
    public void testEliminarPerfil()
    {
        gestor.crearPerfil("deleteUser", "Password123", "blue");
        String input = "deleteUser\nPassword123\n1\n";
        driver = createDriverWithInput(input);

        assertTrue(driver.eliminarPerfil());
        assertFalse(gestor.existeJugador("deleteUser"));
    }

    /**
     * Testea el cambio de contraseña verificando:
     * - Autenticación del usuario antes de permitir cambios
     * - Cumplimiento de políticas de seguridad en nueva contraseña
     * - Actualización efectiva en el sistema
     * - Invalidación de la contraseña anterior
     */
    @Test
    public void testCambiarPassword()
    {
        gestor.crearPerfil("user1", "Password123", "blue");
        String input = "user1\nPassword123\nPassword1234\nPassword1234";
        driver = createDriverWithInput(input);

        assertTrue(driver.cambiarPassword());
        assertEquals("Password", "Password1234", gestor.getPerfil("user1").getPassword());
    }

    /**
     * Prueba el restablecimiento de contraseña mediante frase de recuperación verificando:
     * - Validación correcta de la frase de seguridad
     * - Proceso completo de restablecimiento
     * - Requerimiento de doble confirmación de nueva password
     * - Actualización segura de credenciales
     */
    @Test
    public void testReestablecerPassword()
    {
        gestor.crearPerfil("user1", "Password123", "blue");
        String input = "user1\nblue\nPassword12345\nPassword12345";
        driver = createDriverWithInput(input);

        assertTrue(driver.reestablecerPassword());
        assertEquals("Password", "Password12345", gestor.getPerfil("user1").getPassword());
    }

    /**
     * Valida el proceso de cambio de nombre de usuario comprobando:
     * - Verificación de disponibilidad del nuevo username
     * - Actualización de referencias en el sistema
     * - Conservación de datos del perfil
     * - Integridad de las estadísticas asociadas
     */
    @Test
    public void testCambiarUsername()
    {
        gestor.crearPerfil("user1", "Password123", "blue");
        String input = "user1\nPassword123\nuser2\n";
        driver = createDriverWithInput(input);

        assertTrue(driver.cambiarUsername());
        assertEquals("Username", "user2", gestor.getPerfil("user2").getUsername());
    }
}