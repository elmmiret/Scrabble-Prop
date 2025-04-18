package ranking;

import gestordeperfil.Perfil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Clase de pruebas unitarias para validar el funcionamiento de la clase Ranking.
 * Verifica el correcto ordenamiento, inserción, eliminación y mecanismos de desempate
 * en los diferentes tipos de rankings disponibles.
 *
 * <p>Principales aspectos probados:
 * <ul>
 *   <li>Inicialización correcta de los rankings</li>
 *   <li>Inserción y eliminación de perfiles en todos los rankings</li>
 *   <li>Ordenamiento descendente por diferentes criterios estadísticos</li>
 *   <li>Mecanismo de desempate por nombre de usuario</li>
 *   <li>Gestión de estados vacíos/no vacíos</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class RankingTest {
    private Ranking ranking;
    private Perfil perfil1;
    private Perfil perfil2;
    private Perfil perfil3;

    /**
     * Configura el entorno de pruebas antes de cada test.
     * Crea tres perfiles con estadísticas diferenciadas para pruebas de ordenamiento:
     * <ul>
     *   <li>Perfil1: Puntos medios, mayor partidas jugadas</li>
     *   <li>Perfil2: Máximos puntos, menos partidas</li>
     *   <li>Perfil3: Puntos intermedios, balance victorias/derrotas</li>
     * </ul>
     */
    @Before
    public void setUp() {
        ranking = new Ranking();

        // Perfiles con diferentes estadísticas
        perfil1 = new Perfil("usuarioA", "pass1", "frase1");
        perfil2 = new Perfil("usuarioB", "pass2", "frase2");
        perfil3 = new Perfil("usuarioC", "pass3", "frase3");

        // Configurar estadísticas
        perfil1.incrementarPuntos(100);
        perfil1.incrementarPartidasJugadas(10);
        perfil1.incrementarPartidasGanadas(8);
        perfil1.incrementarPartidasPerdidas(2);

        perfil2.incrementarPuntos(200);
        perfil2.incrementarPartidasJugadas(5);
        perfil2.incrementarPartidasGanadas(4);
        perfil2.incrementarPartidasPerdidas(1);

        perfil3.incrementarPuntos(150);
        perfil3.incrementarPartidasJugadas(8);
        perfil3.incrementarPartidasGanadas(5);
        perfil3.incrementarPartidasPerdidas(3);
    }

    /**
     * Verifica que los rankings se inicialicen vacíos.
     * Comprueba el estado inicial del sistema sin perfiles registrados.
     */
    @Test
    public void testRankingsInicialmenteVacios() {
        assertTrue("Los rankings deberían estar vacíos al inicio", ranking.rankingsVacios());
    }

    /**
     * Prueba la inserción de un perfil en todos los rankings.
     * Valida:
     * <ul>
     *   <li>Los rankings dejan de estar vacíos</li>
     *   <li>El perfil existe en todas las clasificaciones</li>
     *   <li>Consistencia en el número de elementos</li>
     * </ul>
     */
    @Test
    public void testAddToRankings() {
        ranking.addToRankings(perfil1);

        assertFalse("Ranking de puntos no debería estar vacío", ranking.getRankingPuntos().isEmpty());
        assertEquals("Debería contener 1 elemento en todos los rankings", 1, ranking.getRankingPuntos().size());
        assertTrue("El perfil debería estar en todos los rankings",
                ranking.getRankingPuntos().contains(perfil1) &&
                        ranking.getRankingPartidasJugadas().contains(perfil1) &&
                        ranking.getRankingVictorias().contains(perfil1) &&
                        ranking.getRankingDerrotas().contains(perfil1));
    }

    /**
     * Prueba la eliminación completa de un perfil.
     * Verifica que al remover un perfil:
     * <ul>
     *   <li>Se elimina de todos los rankings</li>
     *   <li>Los rankings vuelven a estado vacío</li>
     * </ul>
     */
    @Test
    public void testDeleteFromRankings() {
        ranking.addToRankings(perfil1);
        ranking.deleteFromRankings(perfil1);

        assertTrue("Todos los rankings deberían estar vacíos después de eliminar", ranking.rankingsVacios());
    }

    /**
     * Valida el ordenamiento del ranking por puntos totales.
     * Verifica la posición correcta de los perfiles según:
     * <ol>
     *   <li>Perfil con máximo puntaje</li>
     *   <li>Perfil con puntaje intermedio</li>
     *   <li>Perfil con menor puntaje</li>
     * </ol>
     */
    @Test
    public void testOrdenamientoRankingPuntos() {
        ranking.addToRankings(perfil1);
        ranking.addToRankings(perfil2);
        ranking.addToRankings(perfil3);

        TreeSet<Perfil> puntos = ranking.getRankingPuntos();
        Iterator<Perfil> it = puntos.iterator();

        assertEquals("Primer lugar en puntos debería ser perfil2", perfil2, it.next());
        assertEquals("Segundo lugar en puntos debería ser perfil3", perfil3, it.next());
        assertEquals("Tercer lugar en puntos debería ser perfil1", perfil1, it.next());
    }

    /**
     * Prueba el ordenamiento por partidas jugadas.
     * Configura valores específicos y verifica:
     * <ol>
     *   <li>Perfil con mayor cantidad de partidas</li>
     *   <li>Perfil con cantidad intermedia</li>
     *   <li>Perfil con menos partidas</li>
     * </ol>
     */
    @Test
    public void testOrdenamientoRankingPartidasJugadas() {
        // Configurar datos específicos
        perfil1.incrementarPartidasJugadas(15);
        perfil2.incrementarPartidasJugadas(3);
        perfil3.incrementarPartidasJugadas(10);

        ranking.addToRankings(perfil1);
        ranking.addToRankings(perfil2);
        ranking.addToRankings(perfil3);

        Iterator<Perfil> it = ranking.getRankingPartidasJugadas().iterator();
        assertEquals("Primer lugar en partidas jugadas", perfil1, it.next());
        assertEquals("Segundo lugar en partidas jugadas", perfil3, it.next());
        assertEquals("Tercer lugar en partidas jugadas", perfil2, it.next());
    }

    /**
     * Valida el ordenamiento por victorias.
     * Establece valores controlados y comprueba:
     * <ol>
     *   <li>Perfil con más victorias</li>
     *   <li>Perfil con victorias intermedias</li>
     *   <li>Perfil con menos victorias</li>
     * </ol>
     */
    @Test
    public void testOrdenamientoRankingVictorias() {
        // Configurar datos específicos
        perfil1.incrementarPartidasGanadas(10);
        perfil2.incrementarPartidasGanadas(5);
        perfil3.incrementarPartidasGanadas(8);

        ranking.addToRankings(perfil1);
        ranking.addToRankings(perfil2);
        ranking.addToRankings(perfil3);

        Iterator<Perfil> it = ranking.getRankingVictorias().iterator();
        assertEquals("Primer lugar en victorias", perfil1, it.next());
        assertEquals("Segundo lugar en victorias", perfil3, it.next());
        assertEquals("Tercer lugar en victorias", perfil2, it.next());
    }

    /**
     * Prueba el ordenamiento por derrotas.
     * Configura escenario controlado y verifica:
     * <ol>
     *   <li>Perfil con más derrotas</li>
     *   <li>Perfil con derrotas intermedias</li>
     *   <li>Perfil con menos derrotas</li>
     * </ol>
     */
    @Test
    public void testOrdenamientoRankingDerrotas() {
        // Configurar datos específicos
        perfil1.incrementarPartidasPerdidas(5);
        perfil2.incrementarPartidasPerdidas(2);
        perfil3.incrementarPartidasPerdidas(7);

        ranking.addToRankings(perfil1);
        ranking.addToRankings(perfil2);
        ranking.addToRankings(perfil3);

        Iterator<Perfil> it = ranking.getRankingDerrotas().iterator();
        assertEquals("Primer lugar en derrotas", perfil3, it.next());
        assertEquals("Segundo lugar en derrotas", perfil1, it.next());
        assertEquals("Tercer lugar en derrotas", perfil2, it.next());
    }

    /**
     * Valida el mecanismo de desempate por nombre de usuario.
     * Prueba que cuando dos perfiles tienen la misma estadística:
     * <ul>
     *   <li>Se ordenan alfabéticamente por username</li>
     *   <li>El orden no depende del orden de inserción</li>
     * </ul>
     */
    @Test
    public void testDesempatePorUsername() {
        Perfil perfil4 = new Perfil("usuarioD", "pass4", "frase4");
        Perfil perfil5 = new Perfil("usuarioE", "pass5", "frase5");

        // Mismos puntos, diferente username
        perfil4.incrementarPuntos(100);
        perfil5.incrementarPuntos(100);

        ranking.addToRankings(perfil5); // Se añade primero el username "E"
        ranking.addToRankings(perfil4); // Luego el username "D"

        Iterator<Perfil> it = ranking.getRankingPuntos().iterator();
        assertEquals("Debería ordenar alfabéticamente cuando hay empate", perfil4, it.next());
        assertEquals("Segundo lugar por orden alfabético", perfil5, it.next());
    }

    /**
     * Verifica la detección correcta de rankings no vacíos.
     * Comprueba que el sistema reconoce cuando contiene perfiles.
     */
    @Test
    public void testRankingsVaciosConElementos() {
        ranking.addToRankings(perfil1);
        assertFalse("Debería reportar rankings no vacíos", ranking.rankingsVacios());
    }
}