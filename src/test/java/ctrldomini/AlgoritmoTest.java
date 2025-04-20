package ctrldomini;

import algorisme.Algoritmo;
import algorisme.Dawg;
import ctrldomini.*;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import org.junit.Before;
import org.junit.Test;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import static org.junit.Assert.*;

public class AlgoritmoTest {
    private Algoritmo algoritmo;
    private Dawg dawg;
    private Tablero tablero;

    @Before
    public void setUp() throws Exception {
        // Inizializar con el diccionario catalán para el testing
        dawg = new Dawg(Partida.Idioma.CAT);
        tablero = new Tablero(Partida.Idioma.CAT);
        algoritmo = new Algoritmo();
    }

    @Test
    public void testMejorMovimientoTableroVacio() throws CoordenadaFueraDeRangoException {
        // Test con tablero vacío y unas pocas fichas
        String[] atril = {"a", "b", "c", "d", "e", "f", "g"};
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado = algoritmo.mejorMovimiento(dawg,tablero,atril);

        // En el tablero vacío, debería poner almenos una ficha (la del centro)
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
    }

    @Test
    public void testMejorMovimientoConPalabraExistente() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una palarbra en horizontal
        String[] atril = {"c", "a", "s", "a"};
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        Ficha f3 = new Ficha("s", 1);
        Ficha f4 = new Ficha("a", 1);

        // Poner la palabra casa horizontalmente empezando en (7,7) - posicion central
        tablero.setFicha(f1,'H', 8);
        tablero.setFicha(f2, 'H', 9);
        tablero.setFicha(f3, 'H', 10);
        tablero.setFicha(f4, 'H', 11);

        // Testear con nuevas fichas
        String[] atrilaux = {"m", "e", "s", "a"};
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado = algoritmo.mejorMovimiento(dawg, tablero, atrilaux);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        // Deberia conectar con la palabra existente
        assertTrue(resultado.size() > 0);
    }

    // REVISAR
    @Test
    public void testComputarAnclas() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una sola ficha en el centro
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f,'H', 8);

        List<SimpleEntry<Integer, Integer>> anclas = algoritmo.computarAnclas(tablero);

        // Deberia encontrar anclas alrededor de la ficha colocada
        assertNotNull(anclas);
        assertFalse(anclas.isEmpty());
        assertTrue(anclas.size() >= 4); // mínimo 4
    }

    @Test
    public void testComputarCrossChecks() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una sola ficha
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 'H', 8);

        String[] atril = {"b", "c", "d", "e", "f", "g", "h"};
        algoritmo.computarCrossChecks(dawg,tablero,atril);

        // Ver que los cross checks se hayan computado en las posiciones adyacentes
        assertNotNull(tablero.getAbecedario(6,7));  // arriba
        assertNotNull(tablero.getAbecedario(8,7));  // abajo
        assertNotNull(tablero.getAbecedario(7, 7)); // izquierda
        assertNotNull(tablero.getAbecedario(7, 9)); // derecha
    }

    @Test
    public void testObtenerPuntuacion() throws CoordenadaFueraDeRangoException {
        // Poner una palabra en el tablero
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra =
                List.of(
                        new SimpleEntry<>(new SimpleEntry<>("c", true), new SimpleEntry<>(7, 7)),
                        new SimpleEntry<>(new SimpleEntry<>("a", true), new SimpleEntry<>(7, 8)),
                        new SimpleEntry<>(new SimpleEntry<>("s", true), new SimpleEntry<>(7, 9)),
                        new SimpleEntry<>(new SimpleEntry<>("a", true), new SimpleEntry<>(7, 10))
                );

        int puntuacion = algoritmo.obtenerPuntuacion(tablero,palabra);

        // Puntuación de casa es 4
        // Más el multiplicador de la casilla del centro 8
        assertEquals(8, puntuacion);

    }

    @Test
    public void testObtenerPuntuacionVertical() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Poner una palabra en vertical
        Ficha f1 = new Ficha("a", 1);
        Ficha f2 = new Ficha("m", 1);
        tablero.setFicha(f1, 'H', 8);
        tablero.setFicha(f2, 'I', 8);

        // Testear la puntuacion para la palabra vertical "am"
        int puntuacion = algoritmo.obtenerPuntuacionPalabraVertical(tablero, 7, 8);

        // Resultado debería ser 2
        assertEquals(2, puntuacion);
    }

    @Test
    public void testEsPalabraValida() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Poner "ca" y ver si añadiendo "s" se genera una palabra válida
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 'H', 8);
        tablero.setFicha(f2, 'H', 9);

        boolean valida = algoritmo.esPalabraValida(tablero, 7, 10, "s", dawg);

        assertTrue(valida); // "cas" es un prefijo válido
    }

    @Test
    public void testTieneAdyacentes() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner una ficha
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 'H', 8);

        // Positions around it should have adjacent tiles
        assertTrue(algoritmo.tieneAdyacentes(tablero, 6, 7)); // arriba izquierda
        assertTrue(algoritmo.tieneAdyacentes(tablero, 7, 7)); // izquierda
        assertTrue(algoritmo.tieneAdyacentes(tablero, 8, 7)); // abajo izquierda
        assertFalse(algoritmo.tieneAdyacentes(tablero, 0, 0)); // esquina vacía
    }

    @Test
    public void testComputarParteIzquierdaTablero() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Poner "ca" y testear la computacion de la parte izquierda
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 'H', 7);
        tablero.setFicha(f2, 'H', 8);

        List<SimpleEntry<String, Boolean>> parteIzquierda = algoritmo.computarParteIzquierdaTablero(tablero, 2, 7, 9);

        assertEquals(2, parteIzquierda.size());
        assertEquals("c", parteIzquierda.get(0).getKey());
        assertEquals("a", parteIzquierda.get(1).getKey());
        assertFalse(parteIzquierda.get(0).getValue()); // del tablero, no del atril
        assertFalse(parteIzquierda.get(1).getValue()); // del tablero, no del atril
    }

    @Test
    public void testAsignarPosiciones() throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<String, Boolean>> palabra =
                List.of(
                        new SimpleEntry<>("c", true),
                        new SimpleEntry<>("a", true),
                        new SimpleEntry<>("s", true),
                        new SimpleEntry<>("a", true)
                );

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> resultado =
                algoritmo.asignarPosiciones(palabra, 4, 7, 7);

        assertEquals(4, resultado.size());
        assertEquals(7, (int) resultado.get(0).getValue().getKey());
        assertEquals(7, (int) resultado.get(0).getValue().getValue());
        assertEquals(7, (int) resultado.get(1).getValue().getKey());
        assertEquals(8, (int) resultado.get(1).getValue().getValue());
    }

    @Test
    public void testTamanoParteIzquierdaAtril() throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        // Con el tablero vacío debería devolver las máximas partes izquierdas posibles
        int size = algoritmo.tamañoParteIzquierdaAtril(tablero, 7, 7);
        assertEquals(7, size); // can go left to column 0

        // Poner una ficha a la izquierda
        Ficha f = new Ficha("a", 1);
        tablero.setFicha(f, 'H', 6);
        size = algoritmo.tamañoParteIzquierdaAtril(tablero, 7, 7);
        assertEquals(0, size);
    }

    @Test
    public void testTamanoParteIzquierdaTablero() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // Tablero vacío debería devolver 0
        int size = algoritmo.tamañoParteIzquierdaTablero(tablero, 7, 7);
        assertEquals(0, size);

        // Place tiles to the left
        Ficha f1 = new Ficha("c", 1);
        Ficha f2 = new Ficha("a", 1);
        tablero.setFicha(f1, 'H', 5);
        tablero.setFicha(f2, 'H', 6);

        size = algoritmo.tamañoParteIzquierdaTablero(tablero, 7, 7);
        assertEquals(2, size); // 2 casillas a la izquierda
    }

    @Test
    public void testCasillaCorrecta() {
        assertTrue(algoritmo.casillaCorrecta(0,0));
        assertTrue(algoritmo.casillaCorrecta(14, 14));
        assertFalse(algoritmo.casillaCorrecta(-1, 0));
        assertFalse(algoritmo.casillaCorrecta(15, 15));
    }

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


