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

    @BeforeEach
    void setUp() {
        creador = new Perfil("Creador", "123", "azul");
        oponente = new Perfil("Oponente", "321", "verde");
        partidaPvP = new Partida(creador, oponente, 0, "Partida PvP", Partida.Modo.PvP, Partida.Idioma.CAST);
        partidaPvIA = new Partida(creador, 1, "Partida PvIA", Partida.Modo.PvIA, Partida.Idioma.CAST, 2);
    }

    @Test
    void testGetId() {
        assertEquals(0, partidaPvP.getId());
    }

    @Test
    void testGetNombre() {
        assertEquals("Partida PvP", partidaPvP.getNombre());
        assertEquals("Partida PvIA", partidaPvIA.getNombre());
    }

    @Test
    void testGetFechaHoraCreacion() {
        LocalDateTime now = LocalDateTime.now();
        assertTrue(partidaPvP.getFechaHoraCreacion().isEqual(now));
    }

    @Test
    void testGetModoPartida() {
        assertEquals(Partida.Modo.PvP, partidaPvP.getModoPartida());
        assertEquals(Partida.Modo.PvIA, partidaPvIA.getModoPartida());
    }

    @Test
    void testGetDificultad() {
        assertEquals(0, partidaPvP.getDificultad());
        assertEquals(2, partidaPvIA.getDificultad());
    }

    @Test
    void testGetTablero() {
        assertNotNull(partidaPvP.getTablero());
        assertNotNull(partidaPvIA.getTablero());
    }

    @Test
    void testGetBolsa() {
        Queue<Ficha> bolsaPvP = partidaPvP.getBolsa();
        Queue<Ficha> bolsaPvIA = partidaPvIA.getBolsa();
        assertNotNull(bolsaPvP);
        assertNotNull(bolsaPvIA);
    }

    @Test
    void testGetCreador() {
        assertEquals(creador, partidaPvP.getCreador());
        assertEquals(creador, partidaPvIA.getCreador());
    }

    @Test
    void testGetOponente() {
        assertEquals(oponente, partidaPvP.getOponente());
        assertNull(partidaPvIA.getOponente());
    }
}