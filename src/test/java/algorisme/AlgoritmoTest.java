package algorisme;

import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import gestordeperfil.Perfil;
import org.junit.Before;
import org.junit.Test;
import gestordepartida.Ficha;
import gestordepartida.Partida;
import gestordepartida.Tablero;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import static org.junit.Assert.*;

/**
 * Test unitarios para la clase Algoritmo.
 * Valida la lógica de puntuación, búsqueda de movimientos y validación de palabras para el juego.
 *
 * @author Arnau Miret Barrull
 */
public class AlgoritmoTest {
    private Algoritmo algoritmo;
    private Dawg dawg;
    private Tablero tablero;
    private Perfil creador;
    private Perfil oponente;

    @Before
    public void setUp() throws Exception {
        // Inizializar con el diccionario catalán para el testing
        creador = new Perfil("Creador", "123", "azul");
        oponente = new Perfil("Oponente", "321", "verde");
        dawg = new Dawg(Partida.Idioma.CAT);
        tablero = new Tablero(Partida.Idioma.CAT);
        algoritmo = new Algoritmo(new Partida(creador, oponente, 1, "test", Partida.Modo.PvP,Partida.Idioma.CAT));
    }


    /**
     * Testea que el algoritmo sugiera un movimiento válido cuando el tablero está vacío.
     * Espera que al menos una ficha sea colocada (normalmente en el centro).
     * @throws CoordenadaFueraDeRangoException si alguna coordenada es inválida.
     */
    @Test
    public void testMejorMovimientoTableroVacio() throws CoordenadaFueraDeRangoException {
        // Test con tablero vacío y unas pocas fichas
        String[] atril = {"S", "I"};
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado = algoritmo.mejorMovimiento(dawg,tablero,atril);

        // En el tablero vacío, debería poner almenos una ficha (la del centro)
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    /**
     * Verifica que el algoritmo encuentre un movimiento correcto en un tablero con una palabra preexistente.
     * La jugada sugerida debe conectar con las fichas ya colocadas.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testMejorMovimientoConPalabraExistente() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una palarbra en horizontal
        String[] atril = {"c", "a", "s", "a"};
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        Ficha f3 = new Ficha("s", 1);
        Ficha f4 = new Ficha("a", 1);

        // Poner la palabra casa horizontalmente empezando en el 7,7 (posición central)
        tablero.setFicha(f1,7, 8);
        tablero.setFicha(f2, 7, 9);
        tablero.setFicha(f3, 7, 10);
        tablero.setFicha(f4, 7, 11);

        // Testear con nuevas fichas
        String[] atrilaux = {"m", "e", "s", "a"};
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado = algoritmo.mejorMovimiento(dawg, tablero, atrilaux);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        // Deberia conectar con la palabra existente
        assertTrue(resultado.size() > 0);
    }

    /**
     * Comprueba que el algoritmo detecte correctamente las anclas alrededor de una ficha en el tablero.
     * Las anclas representan posiciones candidatas para formar nuevas palabras.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testComputarAnclas() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una sola ficha en el centro
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f,7, 8);

        List<SimpleEntry<Integer, Integer>> anclas = algoritmo.computarAnclas(tablero);

        // Deberia encontrar anclas alrededor de la ficha colocada
        assertNotNull(anclas);
        assertFalse(anclas.isEmpty());
        assertTrue(anclas.size() >= 4); // mínimo 4
    }

    /**
     * Evalúa que el algoritmo compute los cross-checks correctamente en un tablero con una ficha colocada.
     * Los cross-checks limitan las letras posibles para una posición.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testComputarCrossChecks() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una sola ficha
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 7, 8);

        String[] atril = {"b", "c", "d", "e", "f", "g", "h"};
        algoritmo.computarCrossChecks(dawg,tablero,atril);

        // Ver que los cross checks se hayan computado en las posiciones adyacentes
        assertNotNull(tablero.getAbecedario(6,7));  // arriba
        assertNotNull(tablero.getAbecedario(8,7));  // abajo
        assertNotNull(tablero.getAbecedario(7, 7)); // izquierda
        assertNotNull(tablero.getAbecedario(7, 9)); // derecha
    }

    /**
     * Verifica que la puntuación total de una palabra calculada sea correcta, en este caso la palabra "casa".
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     */
    @Test
    public void testObtenerPuntuacion() throws CoordenadaFueraDeRangoException {
        // Poner una palabra en el tablero
        assertNotNull(algoritmo);
        assertNotNull(tablero);

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra =
                List.of(
                        new SimpleEntry<>(new SimpleEntry<>("c", true), new SimpleEntry<>(7, 7)),
                        new SimpleEntry<>(new SimpleEntry<>("a", true), new SimpleEntry<>(7, 8)),
                        new SimpleEntry<>(new SimpleEntry<>("s", true), new SimpleEntry<>(7, 9)),
                        new SimpleEntry<>(new SimpleEntry<>("a", true), new SimpleEntry<>(7, 10))
                );


        int puntuacion = algoritmo.obtenerPuntuacion(tablero,palabra);
        // Puntuación de casa es 4
        assertEquals(4, puntuacion);
    }

    /**
     * Comprueba que la puntuación vertical de una palabra sea correcta.
     * En el ejemplo se usa la palabra "am" y se espera que la puntuación sea 2.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testObtenerPuntuacionVertical() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Poner una palabra en vertical
        Ficha f1 = new Ficha("a", 1);
        Ficha f2 = new Ficha("m", 1);
        tablero.setFicha(f1, 7, 8);
        tablero.setFicha(f2, 8, 8);

        // Testear la puntuacion para la palabra vertical "am"
        int puntuacion = algoritmo.obtenerPuntuacionPalabraVertical(tablero, 7, 8);

        // Resultado debería ser 2
        assertEquals(2, puntuacion);
    }

    /**
     * Testea si el algoritmo valida correctamente una palabra construida en el tablero.
     * En este caso, comprueba que "CA" sea reconocido como válido con ayuda del DAWG.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testEsPalabraValida() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Poner "ca" y ver si añadiendo "s" se genera una palabra válida
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 7, 8);
        tablero.setFicha(f2, 7, 9);

        boolean valida = algoritmo.esPalabraValida(tablero, 7, 7, "CA", dawg);

        assertTrue(valida); // "cas" es un prefijo válido
    }

    /**
     * Evalúa si el algoritmo detecta fichas adyacentes a una coordenada.
     * Usa distintas posiciones para verificar comportamiento con y sin fichas alrededor.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testTieneAdyacentes() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una ficha
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 7, 8);

        // Positions around it should have adjacent tiles
        assertFalse(algoritmo.tieneAdyacentes(tablero, 6, 7)); // arriba izquierda
        assertTrue(algoritmo.tieneAdyacentes(tablero, 7, 7)); // izquierda
        assertFalse(algoritmo.tieneAdyacentes(tablero, 8, 7)); // abajo izquierda
        assertFalse(algoritmo.tieneAdyacentes(tablero, 0, 0)); // esquina vacía
    }

    /**
     * Verifica que el algoritmo extraiga correctamente la parte izquierda de una palabra en el tablero.
     * Comprueba que la lista obtenida refleje las fichas colocadas y su origen.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testComputarParteIzquierdaTablero() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner "ca" y testear la computacion de la parte izquierda
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 7, 7);
        tablero.setFicha(f2, 7, 8);

        List<SimpleEntry<String, Boolean>> parteIzquierda = algoritmo.computarParteIzquierdaTablero(tablero, 2, 7, 9);

        assertEquals(2, parteIzquierda.size());
        assertEquals("c", parteIzquierda.get(0).getKey());
        assertEquals("a", parteIzquierda.get(1).getKey());
        assertFalse(parteIzquierda.get(0).getValue()); // del tablero, no del atril
        assertFalse(parteIzquierda.get(1).getValue()); // del tablero, no del atril
    }

    /**
     * Testea que el algoritmo asigne correctamente las posiciones para una palabra.
     * Comprueba que cada letra se coloque en las coordenadas previstas.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     */
    @Test
    public void testAsignarPosiciones() throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<String, Boolean>> palabra =
                List.of(
                        new SimpleEntry<>("c", true),
                        new SimpleEntry<>("a", true),
                        new SimpleEntry<>("s", true),
                        new SimpleEntry<>("a", true)
                );

        // nse si va false o true pero falla en los dos casos
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado =
                algoritmo.asignarPosiciones(palabra, 4, 7, 7, true);

        assertEquals(4, resultado.size());
        assertEquals(7, (int) resultado.get(0).getValue().getKey());
        assertEquals(7, (int) resultado.get(0).getValue().getValue());
        assertEquals(7, (int) resultado.get(1).getValue().getKey());
        assertEquals(8, (int) resultado.get(1).getValue().getValue());
    }

    /**
     * Evalúa la cantidad máxima de fichas que cabrían a la izquierda de una posición en un tablero vacío o parcialmente lleno.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testTamanoParteIzquierdaAtril() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Con el tablero vacío debería devolver las máximas partes izquierdas posibles
        int size = algoritmo.tamañoParteIzquierdaAtril(tablero, 7, 7);
        assertEquals(7, size); // can go left to column 0

        // Poner una ficha a la izquierda
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 7, 6);
        size = algoritmo.tamañoParteIzquierdaAtril(tablero, 7, 7);
        assertEquals(0, size);
    }

    /**
     * Testea la cantidad de fichas que existen a la izquierda de una posición en el tablero.
     * Comprueba comportamiento tanto en tablero vacío como con fichas colocadas.
     * @throws CoordenadaFueraDeRangoException si las coordenadas están fuera de rango.
     * @throws CasillaOcupadaException si se intenta ocupar una casilla ya ocupada.
     */
    @Test
    public void testTamanoParteIzquierdaTablero() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Tablero vacío debería devolver 0
        int size = algoritmo.tamañoParteIzquierdaTablero(tablero, 7, 7);
        assertEquals(0, size);

        // Place tiles to the left
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 7, 5);
        tablero.setFicha(f2, 7, 6);

        size = algoritmo.tamañoParteIzquierdaTablero(tablero, 7, 7);
        assertEquals(2, size); // 2 casillas a la izquierda
    }

    /**
     * Verifica que el método casillaCorrecta() detecta correctamente si una coordenada está dentro de los límites del tablero.
     */
    @Test
    public void testCasillaCorrecta() {
        assertTrue(algoritmo.casillaCorrecta(0,0));
        assertTrue(algoritmo.casillaCorrecta(14, 14));
        assertFalse(algoritmo.casillaCorrecta(-1, 0));
        assertFalse(algoritmo.casillaCorrecta(15, 15));
    }

    /**
     * Test que espera una excepción {@link CoordenadaFueraDeRangoException}
     * cuando se accede a coordenadas fuera del rango permitido en el tablero.
     * @throws CoordenadaFueraDeRangoException siempre, debido al override forzado en el test.
     */
    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testCoordenadaFueraDeRango() throws CoordenadaFueraDeRangoException {
        algoritmo.computarAnclas(new Tablero(Partida.Idioma.CAT) {
            @Override
            public Ficha getFicha(int x, int y) throws CoordenadaFueraDeRangoException {
                if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) {
                    throw new CoordenadaFueraDeRangoException(x, y);
                }
                return null;
            }
        });
    }
}
