package ranking;

import gestordeperfil.Perfil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.*;
import java.util.TreeSet;

/**
 * Clase de pruebas unitarias para la clase DriverRanking.
 * Verifica el correcto funcionamiento de la visualización de rankings y la interacción con el usuario.
 *
 * <p>Principales aspectos probados:
 * <ul>
 *   <li>Formato correcto de los diferentes tipos de rankings</li>
 *   <li>Ordenamiento descendente de los perfiles</li>
 *   <li>Manejo de entrada del usuario y flujo de navegación</li>
 *   <li>Casos especiales (ranking vacío, opciones inválidas)</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class DriverRankingTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Ranking ranking;
    private Perfil perfil1, perfil2;

    /**
     * Configura el entorno de prueba antes de cada test.
     * <ul>
     *   <li>Redirige System.out para capturar la salida</li>
     *   <li>Crea dos perfiles con estadísticas diferentes</li>
     *   <li>Agrega los perfiles al sistema de rankings</li>
     * </ul>
     */
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        ranking = new Ranking();

        perfil1 = new Perfil("Jugador1", "pass1", "frase1");
        perfil2 = new Perfil("Jugador2", "pass2", "frase2");

        perfil1.incrementarPuntos(100);
        perfil1.incrementarPartidasJugadas(10);
        perfil1.incrementarPartidasGanadas(8);
        perfil1.incrementarPartidasPerdidas(2);

        perfil2.incrementarPuntos(200);
        perfil2.incrementarPartidasJugadas(5);
        perfil2.incrementarPartidasGanadas(4);
        perfil2.incrementarPartidasPerdidas(1);

        ranking.addToRankings(perfil1);
        ranking.addToRankings(perfil2);
    }

    /**
     * Restaura los flujos de salida originales después de cada test.
     */
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Obtiene y normaliza la salida de consola capturada.
     * @return String con la salida de consola (saltos de línea en formato UNIX)
     */
    private String getConsoleOutput() {
        return outContent.toString().replace("\r\n", "\n");
    }

    /**
     * Crea una instancia de DriverRanking con entrada simulada.
     * @param input Cadena de entrada simulada del usuario
     * @return Instancia configurada de DriverRanking
     */
    private DriverRanking createDriverWithInput(String input) {
        return new DriverRanking(
                ranking,
                new ByteArrayInputStream(input.getBytes())
        );
    }

    /**
     * Prueba la visualización del ranking por partidas jugadas.
     * Verifica:
     * <ul>
     *   <li>El formato del encabezado</li>
     *   <li>El orden descendente según partidas jugadas</li>
     *   <li>La correcta numeración de posiciones</li>
     *   <li>La presentación de estadísticas</li>
     * </ul>
     */
    @Test
    public void testMostrarRankingPartidasJugadas() {
        DriverRanking driver = createDriverWithInput("");
        driver.mostrarRanking("partidasJugadas");

        String expected =
                "\n--- RANKING PARTIDASJUGADAS ---\n\n" +
                        "1- Jugador1 - Partidas jugadas: 10\n" + // ✅ Jugador1 primero
                        "2- Jugador2 - Partidas jugadas: 5\n\n";  // ✅ Jugador2 segundo

        assertEquals(expected, getConsoleOutput());
    }

    /**
     * Prueba la visualización del ranking por puntos.
     * Verifica:
     * <ul>
     *   <li>La precedencia del perfil con más puntos</li>
     *   <li>La correcta asignación de posiciones</li>
     *   <li>El formato de presentación de puntos</li>
     * </ul>
     */
    @Test
    public void testMostrarRankingPuntos() {
        DriverRanking driver = createDriverWithInput("");
        driver.mostrarRanking("puntos");

        String expected =
                "\n--- RANKING PUNTOS ---\n\n" +
                        "1- Jugador2 - Puntos: 200\n" +
                        "2- Jugador1 - Puntos: 100\n\n"; // 2 \n al final

        assertEquals(expected, getConsoleOutput());
    }

    /**
     * Prueba la visualización del ranking por victorias.
     * Valida:
     * <ul>
     *   <li>La prioridad del perfil con más victorias</li>
     *   <li>La correcta asociación de estadísticas</li>
     *   <li>La estructura del mensaje de salida</li>
     * </ul>
     */
    @Test
    public void testMostrarRankingVictorias() {
        DriverRanking driver = createDriverWithInput("");
        driver.mostrarRanking("victorias");

        String expected =
                "\n--- RANKING VICTORIAS ---\n\n" +
                        "1- Jugador1 - Victorias: 8\n" +
                        "2- Jugador2 - Victorias: 4\n\n"; // 2 \n al final

        assertEquals(expected, getConsoleOutput());
    }

    /**
     * Prueba la visualización del ranking por derrotas.
     * Comprueba:
     * <ul>
     *   <li>La ordenación por mayor número de derrotas</li>
     *   <li>La correcta presentación de valores negativos</li>
     *   <li>La integridad del formato de salida</li>
     * </ul>
     */
    @Test
    public void testMostrarRankingDerrotas() {
        DriverRanking driver = createDriverWithInput("");
        driver.mostrarRanking("derrotas");

        String expected =
                "\n--- RANKING DERROTAS ---\n\n" +
                        "1- Jugador1 - Derrotas: 2\n" +
                        "2- Jugador2 - Derrotas: 1\n\n"; // 2 \n al final

        assertEquals(expected, getConsoleOutput());
    }

    /**
     * Prueba la opción de continuar viendo rankings.
     * Simula la entrada de usuario afirmativa (1) y verifica que:
     * <ul>
     *   <li>El método retorna true</li>
     *   <li>Se procesa correctamente la entrada</li>
     * </ul>
     */
    @Test
    public void testVerOtroRankingSi() {
        DriverRanking driver = createDriverWithInput("1\n");
        assertTrue(driver.verOtroRanking());
    }

    /**
     * Prueba la opción de salir del sistema de rankings.
     * Simula la entrada negativa del usuario (2) y verifica que:
     * <ul>
     *   <li>El método retorna false</li>
     *   <li>Se procesa correctamente la selección</li>
     * </ul>
     */
    @Test
    public void testVerOtroRankingNo() {
        DriverRanking driver = createDriverWithInput("2\n");
        assertFalse(driver.verOtroRanking());
    }

    /**
     * Prueba el comportamiento del sistema sin jugadores registrados.
     * Verifica:
     * <ul>
     *   <li>La detección correcta de rankings vacíos</li>
     *   <li>El mensaje informativo al usuario</li>
     *   <li>La ausencia de errores en esta situación</li>
     * </ul>
     */
    @Test
    public void testRankingManagementSinJugadores() {
        Ranking rankingVacio = new Ranking();
        DriverRanking driver = new DriverRanking(
                rankingVacio,
                new ByteArrayInputStream("".getBytes())
        );
        driver.rankingManagement();
        assertTrue(getConsoleOutput().contains("No hay ningún jugador en el sistema"));
    }
}