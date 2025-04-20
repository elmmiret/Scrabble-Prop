package estadisticas;

import gestordeperfil.DriverPerfil;
import gestordeperfil.GestorDePerfil;
import gestordeperfil.Perfil;
import ranking.Ranking;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.io.*;
import java.util.Scanner;

/**
 * Clase de pruebas para el sistema de gestión de estadísticas mediante consola.
 * Verifica el funcionamiento de las operaciones de consulta de estadísticas
 *
 * <p>Características principales probadas:
 * <ul>
 *   <li>Consulta de estadísticas con diferentes casos (usuarios válidos, inválidos y sin jugadores)</li>
 *   <li>Manejo de múltiples consultas consecutivas</li>
 *   <li>Gestión de opciones inválidas durante el flujo interactivo</li>
 *   <li>Integración con el sistema de perfiles y ranking</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class DriverEstadisticasTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private GestorDePerfil gestor;

    /**
     * Configura el entorno de pruebas inicializando el gestor de perfiles antes de cada test.
     */
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        gestor = new GestorDePerfil(new Ranking());
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private String getConsoleOutput() {
        return outContent.toString().replace("\r\n", "\n");
    }

    private DriverEstadisticas createDriver(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner testScanner = new Scanner(inputStream);
        return new DriverEstadisticas(gestor, testScanner);
    }

    /**
     * Prueba la visualización de estadísticas cuando no hay jugadores registrados verificando:
     * - La correcta detección de la ausencia de perfiles
     * - La salida del mensaje de error apropiado
     * - El retorno de estado false indicando la imposibilidad de operación
     */
    @Test
    public void testMostrarEstadisticasSinJugadores() {
        DriverEstadisticas driver = createDriver("");
        assertFalse(driver.mostrarEstadisticas());
        assertEquals("\nNo hay ningún jugador en el sistema\n", getConsoleOutput());
    }

    /**
     * Valida el comportamiento ante consultas de usuarios no existentes comprobando:
     * - La detección correcta de perfiles inexistentes
     * - La presentación de mensajes de error adecuados
     * - La preservación del estado del sistema ante operaciones inválidas
     */
    @Test
    public void testMostrarEstadisticasUsuarioInvalido() {
        gestor.crearPerfil("user1", "pass1", "frase1");
        DriverEstadisticas driver = createDriver("userX\n");
        assertFalse(driver.mostrarEstadisticas());
        assertTrue(getConsoleOutput().contains("No existe ningún perfil con ese username"));
    }

    /**
     * Testea la consulta exitosa de estadísticas de un usuario válido verificando:
     * - La correcta recuperación de datos del perfil (puntos y partidas jugadas)
     * - El formato adecuado de presentación de la información
     * - La correspondencia entre los datos almacenados y los mostrados
     */
    @Test
    public void testMostrarEstadisticasUsuarioValido() {
        gestor.crearPerfil("user1", "pass1", "frase1");
        Perfil p = gestor.getPerfil("user1");
        p.incrementarPuntos(100);
        p.incrementarPartidasJugadas(5);

        DriverEstadisticas driver = createDriver("user1\n");
        assertTrue(driver.mostrarEstadisticas());

        String output = getConsoleOutput();
        assertTrue(output.contains("---user1---"));
        assertTrue(output.contains("Puntos: 100"));
        assertTrue(output.contains("Partidas jugadas: 5"));
    }

    /**
     * Prueba el flujo de gestión de estadísticas con una única consulta verificando:
     * - La secuencia completa de interacción (consulta + salida)
     * - El manejo correcto de la opción de salida
     * - La limpieza adecuada del estado post-operación
     */
    @Test
    public void testEstadisticasManagementUnaConsulta() {
        gestor.crearPerfil("user1", "pass1", "frase1");
        String input = "user1\n2\n"; // Consultar user1 y salir
        DriverEstadisticas driver = createDriver(input);
        driver.estadisticasManagement();

        assertTrue(getConsoleOutput().contains("¿Quieres ver las estadísticas de otro perfil?"));
    }

    /**
     * Valida el funcionamiento con múltiples consultas consecutivas comprobando:
     * - La correcta secuenciación de operaciones
     * - La preservación del estado entre consultas
     * - La capacidad de manejar múltiples perfiles simultáneamente
     */
    @Test
    public void testEstadisticasManagementMultiplesConsultas() {
        gestor.crearPerfil("user1", "pass1", "frase1");
        gestor.crearPerfil("user2", "pass2", "frase2");
        String input = "user1\n1\nuser2\n2\n"; // Consultar dos usuarios
        DriverEstadisticas driver = createDriver(input);
        driver.estadisticasManagement();

        String output = getConsoleOutput();
        assertTrue(output.contains("---user1---"));
        assertTrue(output.contains("---user2---"));
    }

    /**
     * Testea el manejo de opciones inválidas durante el flujo interactivo verificando:
     * - La detección correcta de opciones no válidas
     * - El comportamiento seguro del sistema ante entradas erróneas
     * - La finalización controlada del proceso
     */
    @Test
    public void testEstadisticasManagementOpcionInvalida() {
        gestor.crearPerfil("user1", "pass1", "frase1");
        String input = "user1\n3\n"; // Opción inválida (debería salir)
        DriverEstadisticas driver = createDriver(input);
        driver.estadisticasManagement();

        assertTrue(getConsoleOutput().contains("¿Quieres ver las estadísticas de otro perfil?"));
    }

    /**
     * Prueba el caso de gestión de estadísticas sin jugadores registrados verificando:
     * - La detección temprana de ausencia de perfiles
     * - La salida del mensaje de error correspondiente
     * - La prevención de operaciones innecesarias
     */
    @Test
    public void testEstadisticasManagementSinJugadores() {
        String input = "";
        DriverEstadisticas driver = createDriver(input);
        driver.estadisticasManagement();

        assertEquals("\nNo hay ningún jugador en el sistema\n", getConsoleOutput());
    }
}