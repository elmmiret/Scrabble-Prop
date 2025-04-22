package ctrldomini;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import gestordeperfil.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import partida.Ficha;
import partida.GestorDePartida;
import partida.Partida;
import ranking.Ranking;

/**
 * Clase de pruebas unitarias para verificar el funcionamiento del GestorDePartida.
 * Contiene tests para validar la creación, gestión y eliminación de partidas,
 * así como las interacciones con perfiles de jugadores.
 */
public class GestorDePartidaTest {
    private GestorDePartida gestor;
    private GestorDePerfil gestorPerfiles;
    private Perfil jugador1;
    private Perfil jugador2;
    private final int PARTIDA_ID = 1;
    private final String NOMBRE_PARTIDA = "PartidaTest";

    @Before
    public void setUp() {
        gestorPerfiles = new GestorDePerfil(new Ranking());
        gestor = new GestorDePartida(gestorPerfiles);

        // Crear jugadores de prueba
        gestorPerfiles.crearPerfil("Jugador1", "Pass123!", "Frase1");
        gestorPerfiles.crearPerfil("Jugador2", "Pass456!", "Frase2");
        jugador1 = gestorPerfiles.getPerfil("Jugador1");
        jugador2 = gestorPerfiles.getPerfil("Jugador2");
    }

    @Test
    public void testCrearPartidaPvP() {
        Partida p = gestor.crearPartida(
                PARTIDA_ID,
                NOMBRE_PARTIDA,
                Partida.Idioma.CAST,
                jugador1,
                Partida.Modo.PvP,
                jugador2,
                0
        );

        assertNotNull(p);
        assertEquals(1, gestor.getPartidas().size());
        assertEquals(jugador1, p.getCreador());
        assertEquals(jugador2, p.getOponente());
    }

    @Test
    public void testCrearPartidaPvIA() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAT, jugador1, Partida.Modo.PvIA, null, 2);

        assertNotNull(p);
        assertEquals(1, gestor.getPartidas().size());
        assertEquals(2, p.getDificultad());
    }

    @Test
    public void testObtenerPartidaExistente() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Partida p = gestor.obtenerPartida(PARTIDA_ID);
        assertNotNull(p);
        assertEquals(NOMBRE_PARTIDA, p.getNombre());
    }

    @Test
    public void testObtenerPartidaInexistente() {
        assertNull(gestor.obtenerPartida(999));
    }

    @Test
    public void testEliminarPartida() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        assertTrue(gestor.eliminarPartida(PARTIDA_ID));
        assertNull(gestor.obtenerPartida(PARTIDA_ID));
    }

    @Test
    public void testExistePartidaJugador() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        assertTrue(gestor.existePartidaJugador(jugador1, PARTIDA_ID));
        assertTrue(gestor.existePartidaJugador(jugador2, PARTIDA_ID));
    }

    @Test
    public void testObtenerPartidasJugador() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador1);
        assertEquals(1, partidas.size());
        assertEquals(PARTIDA_ID, partidas.get(0).getId());
    }

    @Test
    public void testCambiarFichasValido() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Map<Ficha, Integer> atril = new HashMap<>();
        atril.put(new Ficha("A", 0), 1);
        atril.put(new Ficha("B", 0), 2);
        List<String> letras = Arrays.asList("A", "B");
        boolean resultado = gestor.cambiarFichas(p.getRondas().get(0), atril, letras);

        assertTrue(resultado);

    }

    @Test
    public void testCambiarFichasInvalido() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Map<Ficha, Integer> atril = new HashMap<>();
        atril.put(new Ficha("B", 1), 1);

        boolean resultado = gestor.cambiarFichas(p.getRondas().get(0), atril, List.of("B", "B"));

        assertFalse(resultado);
    }

    @Test
    public void testObtenerAtrilJugador() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(p, jugador1);
        assertNotNull(atril);
    }

    @Test
    public void testVerificarFraseRecuperación() {
        assertTrue(gestor.verificarFraseRecuperacion("Jugador1", "Frase1"));
        assertFalse(gestor.verificarFraseRecuperacion("Jugador1", "Frase2"));

    }
}