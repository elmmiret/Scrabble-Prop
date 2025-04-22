package ctrldomini;

import gestordeperfil.*;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import gestordepartida.Ficha;
import gestordepartida.Partida;
import gestordepartida.Turno;

/**
 * Clase de pruebas unitarias para la clase {@link Turno}.
 * Verifica el correcto funcionamiento de la inicialización de atributos,
 * la gestión de atriles y las operaciones básicas del turno en una partida.
 *
 * @author Paula Pérez Chia
 */
public class TurnoTest {

    private Turno turno;
    private Partida partidaPvP;
    private Partida partidaPvIA;
    private Perfil jugador1;
    private Perfil jugador2;
    private Ficha fichaA;
    private Map<Ficha, Integer> atrilTest;

    /**
     * Configura el entorno de prueba antes de cada test:
     * <ul>
     *   <li>Crea perfiles de jugadores</li>
     *   <li>Inicializa partidas PvP y PvIA</li>
     *   <li>Prepara un turno con la partida PvP</li>
     *   <li>Configura un atril de prueba con fichas</li>
     * </ul>
     */
    @Before
    public void setUp() {
        jugador1 = new Perfil("Jugador1", "123", "azul");
        jugador2 = new Perfil("Jugador2", "321", "verde");
        partidaPvP = new Partida(jugador1, jugador2 , 123, "Partida PvP", Partida.Modo.PvP, Partida.Idioma.CAST);
        partidaPvIA = new Partida(jugador1, 1234, "Partida PvIA", Partida.Modo.PvIA, Partida.Idioma.CAST, 1);
        turno = new Turno(partidaPvP, jugador1, 0, 0);

        fichaA = new Ficha("A", 1);
        atrilTest = new HashMap<>();
        atrilTest.put(fichaA, 2);
    }

    /**
     * Prueba que el constructor de Turno inicialice correctamente:
     * <ul>
     *   <li>El jugador actual coincide con el proporcionado</li>
     *   <li>Los atriles J1 y J2 no son nulos</li>
     *   <li>El tipo de jugada inicial es null</li>
     * </ul>
     */
    @Test
    public void testConstructorInicializaCorrectamente() {
        assertEquals(jugador1, turno.getJugador());
        assertNotNull(turno.getAtrilJ1());
        assertNotNull(turno.getAtrilJ2());
        assertNull(turno.getTipoJugada());
    }

    /**
     * Prueba que el método inicializarAtriles llene los atriles:
     * <ul>
     *   <li>Atril J1 contiene exactamente MAX_FICHAS fichas</li>
     *   <li>Atril J2 contiene exactamente MAX_FICHAS fichas</li>
     * </ul>
     * Verifica que no se exceda la capacidad máxima definida.
     */
    @Test
    public void testInicializarAtrilesLlenaAtrilesHastaMaximo() {
        turno.inicializarAtriles();
        assertEquals(Turno.MAX_FICHAS, turno.getTotalFichas(turno.getAtrilJ1()));
        assertEquals(Turno.MAX_FICHAS, turno.getTotalFichas(turno.getAtrilJ2()));
    }

}
