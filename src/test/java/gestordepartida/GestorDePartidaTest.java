package gestordepartida;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import gestordeperfil.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ranking.Ranking;

/**
 * Clase de pruebas unitarias para la clase {@link GestorDePartida}.
 * Verifica la creación, gestión y eliminación de partidas, así como operaciones
 * relacionadas con jugadores, intercambio de fichas y validación de frases de recuperación.
 *
 * @author Albert Aulet Niubó
 */
public class GestorDePartidaTest {
    private GestorDePartida gestor;
    private GestorDePerfil gestorPerfiles;
    private Perfil jugador1;
    private Perfil jugador2;
    private final int PARTIDA_ID = 1;
    private final String NOMBRE_PARTIDA = "PartidaTest";

    /**
     * Configura el entorno de prueba antes de cada test:
     * <ul>
     *   <li>Inicializa el gestor de perfiles y partidas</li>
     *   <li>Crea jugadores de prueba ("Jugador1" y "Jugador2")</li>
     *   <li>Obtiene los perfiles de los jugadores creados</li>
     * </ul>
     */
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

    /**
     * Prueba la creación de una partida PvP:
     * <ul>
     *   <li>Verifica que la partida creada no sea nula</li>
     *   <li>Comprueba que se registra en el gestor</li>
     *   <li>Confirma que los jugadores asignados coinciden con los creadores</li>
     * </ul>
     */
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

    /**
     * Prueba la creación de una partida contra la IA:
     * <ul>
     *   <li>Verifica que la partida se crea correctamente</li>
     *   <li>Confirma que la dificultad de la IA se establece</li>
     * </ul>
     */
    @Test
    public void testCrearPartidaPvIA() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAT, jugador1, Partida.Modo.PvIA, null, 2);

        assertNotNull(p);
        assertEquals(1, gestor.getPartidas().size());
        assertEquals(2, p.getDificultad());
    }

    /**
     * Prueba la recuperación de una partida existente por su ID.
     * Verifica que la partida obtenida coincide con el nombre esperado.
     */
    @Test
    public void testObtenerPartidaExistente() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Partida p = gestor.obtenerPartida(PARTIDA_ID);
        assertNotNull(p);
        assertEquals(NOMBRE_PARTIDA, p.getNombre());
    }

    /**
     * Prueba la recuperación de una partida inexistente.
     * Confirma que el método devuelve null cuando no existe la partida.
     */
    @Test
    public void testObtenerPartidaInexistente() {
        assertNull(gestor.obtenerPartida(999));
    }

    /**
     * Prueba la eliminación de una partida:
     * <ul>
     *   <li>Verifica que la partida se elimina del gestor</li>
     *   <li>Confirma que no se puede recuperar después de eliminarla</li>
     * </ul>
     */
    @Test
    public void testEliminarPartida() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        assertTrue(gestor.eliminarPartida(PARTIDA_ID));
        assertNull(gestor.obtenerPartida(PARTIDA_ID));
    }

    /**
     * Prueba la verificación de participación de jugadores en una partida:
     * <ul>
     *   <li>Confirma que el creador está asociado a la partida</li>
     *   <li>Confirma que el oponente está asociado a la partida</li>
     * </ul>
     */
    @Test
    public void testExistePartidaJugador() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        assertTrue(gestor.existePartidaJugador(jugador1, PARTIDA_ID));
        assertTrue(gestor.existePartidaJugador(jugador2, PARTIDA_ID));
    }

    /**
     * Prueba la obtención de todas las partidas de un jugador:
     * <ul>
     *   <li>Verifica que la lista devuelta contiene la partida creada</li>
     *   <li>Confirma que el ID de la partida coincide</li>
     * </ul>
     */
    @Test
    public void testObtenerPartidasJugador() {
        gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador1);
        assertEquals(1, partidas.size());
        assertEquals(PARTIDA_ID, partidas.get(0).getId());
    }

    /**
     * Prueba un intercambio válido de fichas:
     * <ul>
     *   <li>Verifica que el método retorna true cuando hay suficientes fichas</li>
     * </ul>
     */
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

    /**
     * Prueba un intercambio inválido de fichas:
     * <ul>
     *   <li>Verifica que el método retorna false cuando no hay suficientes fichas</li>
     * </ul>
     */
    @Test
    public void testCambiarFichasInvalido() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Map<Ficha, Integer> atril = new HashMap<>();
        atril.put(new Ficha("B", 1), 1);

        boolean resultado = gestor.cambiarFichas(p.getRondas().get(0), atril, List.of("B", "B"));

        assertFalse(resultado);
    }

    /**
     * Prueba la obtención del atril de un jugador:
     * <ul>
     *   <li>Confirma que el atril devuelto no es nulo</li>
     * </ul>
     */
    @Test
    public void testObtenerAtrilJugador() {
        Partida p = gestor.crearPartida(PARTIDA_ID, NOMBRE_PARTIDA, Partida.Idioma.CAST, jugador1, Partida.Modo.PvP, jugador2, 0);
        Map<Ficha, Integer> atril = gestor.obtenerAtrilJugador(p, jugador1);
        assertNotNull(atril);
    }

    /**
     * Prueba la verificación de frases de recuperación:
     * <ul>
     *   <li>Confirma que la frase correcta retorna true</li>
     *   <li>Confirma que una frase incorrecta retorna false</li>
     * </ul>
     */
    @Test
    public void testVerificarFraseRecuperación() {
        assertTrue(gestor.verificarFraseRecuperacion("Jugador1", "Frase1"));
        assertFalse(gestor.verificarFraseRecuperacion("Jugador1", "Frase2"));

    }
}