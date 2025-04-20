import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Queue;
import static org.junit.jupiter.api.Assertions.*;

package ctrldomini;


/**
 * Tests para probar las funcionalidades de la clase Partida
 *
 * @author: Paula Pérez
 */
class PartidaTest {

    private Partida partidaPvP;
    private Partida partidaPvIA;
    private Perfil creador;
    private Perfil oponente;

    /**
     * Configura el entorno de prueba antes de cada test.
     * Crea instancias de Perfil para creador y oponente, e inicializa partidas
     * en ambos modos (PvP y PvIA) para su uso en las pruebas.
     */
    @BeforeEach
    void setUp() {
        creador = new Perfil("Creador", "123", "azul");
        oponente = new Perfil("Oponente", "321", "verde");
        partidaPvP = new Partida(creador, oponente, 0, "Partida PvP", Partida.Modo.PvP, Partida.Idioma.CAST);
        partidaPvIA = new Partida(creador, 1, "Partida PvIA", Partida.Modo.PvIA, Partida.Idioma.CAST, 2);
    }

    /**
     * Prueba la obtención del identificador único de la partida.
     * Verifica que el ID asignado coincida con el valor esperado en modo PvP.
     */
    @Test
    void testGetId() {
        assertEquals(0, partidaPvP.getId());
    }

    /**
     * Prueba la obtención del nombre de la partida.
     * Verifica que los nombres asignados en ambos modos de juego (PvP y PvIA) sean correctos.
     */
    @Test
    void testGetNombre() {
        assertEquals("Partida PvP", partidaPvP.getNombre());
        assertEquals("Partida PvIA", partidaPvIA.getNombre());
    }

    /**
     * Prueba la fecha y hora de creación de la partida.
     * Verifica que la fecha de creación coincida con el momento de inicialización.
     */
    @Test
    void testGetFechaHoraCreacion() {
        LocalDateTime now = LocalDateTime.now();
        assertTrue(partidaPvP.getFechaHoraCreacion().isEqual(now));
    }

    /**
     * Prueba la obtención del modo de juego.
     * Verifica que se detecte correctamente el tipo de partida (PvP o PvIA).
     */
    @Test
    void testGetModoPartida() {
        assertEquals(Partida.Modo.PvP, partidaPvP.getModoPartida());
        assertEquals(Partida.Modo.PvIA, partidaPvIA.getModoPartida());
    }

    /**
     * Prueba la obtención del nivel de dificultad.
     * Verifica que la dificultad sea 0 en modo PvP y el valor asignado en modo PvIA.
     */
    @Test
    void testGetDificultad() {
        assertEquals(0, partidaPvP.getDificultad());
        assertEquals(2, partidaPvIA.getDificultad());
    }

    /**
     * Prueba la obtención del tablero de juego.
     * Verifica que el tablero se inicialice correctamente en ambos tipos de partida.
     */
    @Test
    void testGetTablero() {
        assertNotNull(partidaPvP.getTablero());
        assertNotNull(partidaPvIA.getTablero());
    }

    /**
     * Prueba la obtención de la bolsa de fichas.
     * Verifica que la bolsa se inicialice correctamente y no esté vacía en ambos modos de juego.
     */
    @Test
    void testGetBolsa() {
        Queue<Ficha> bolsaPvP = partidaPvP.getBolsa();
        Queue<Ficha> bolsaPvIA = partidaPvIA.getBolsa();
        assertNotNull(bolsaPvP);
        assertNotNull(bolsaPvIA);
    }

    /**
     * Prueba la obtención del perfil del creador.
     * Verifica que el creador sea el mismo en ambos tipos de partida.
     */
    @Test
    void testGetCreador() {
        assertEquals(creador, partidaPvP.getCreador());
        assertEquals(creador, partidaPvIA.getCreador());
    }

    /**
     * Prueba la obtención del perfil del oponente.
     * Verifica la presencia de oponente en modo PvP y su ausencia en modo PvIA.
     */
    @Test
    void testGetOponente() {
        assertEquals(oponente, partidaPvP.getOponente());
        assertNull(partidaPvIA.getOponente());
    }
}