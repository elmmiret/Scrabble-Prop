package ctrldomini;

import gestordeperfil.*;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

import partida.Ficha;
import partida.Partida;
import partida.Turno;

public class TurnoTest {

    private Turno turno;
    private Partida partidaPvP;
    private Partida partidaPvIA;
    private Perfil jugador1;
    private Perfil jugador2;
    private Ficha fichaA;
    private Map<Ficha, Integer> atrilTest;

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
     * Verifica que todos los atributos se inicializan correctamente
     */
    @Test
    public void testConstructorInicializaCorrectamente() {
        assertEquals(jugador1, turno.getJugador());
        assertNotNull(turno.getAtrilJ1());
        assertNotNull(turno.getAtrilJ2());
        assertNull(turno.getTipoJugada());
    }

    /**
     * Prueba que los atriles no sobrepasen su capacidad y cuenten bien el numero de fichas que tiene.
     */
    @Test
    public void testInicializarAtrilesLlenaAtrilesHastaMaximo() {
        turno.inicializarAtriles();
        assertEquals(Turno.MAX_FICHAS, turno.getTotalFichas(turno.getAtrilJ1()));
        assertEquals(Turno.MAX_FICHAS, turno.getTotalFichas(turno.getAtrilJ2()));
    }

}
