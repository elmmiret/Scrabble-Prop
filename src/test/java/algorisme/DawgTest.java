package algorisme;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import gestordepartida.Partida;

/**
 * Clase de pruebas unitarias para la clase {@link Dawg}.
 * Verifica el correcto funcionamiento de los métodos relacionados con el DAWG,
 * la división de palabras y la validación de coordenadas en el tablero.
 *
 * @author Arnau Miret Barrull
 */
public class DawgTest {
    private Dawg dawgING;
    private Dawg dawgCAST;
    private Dawg dawgCAT;


    /**
     * Configura el entorno de prueba inicializando instancias de {@link Dawg}
     * para cada idioma soportado (inglés, castellano y catalán).
     * Este método se ejecuta antes de cada prueba.
     */
    @Before
    public void setUp() {
        dawgING = new Dawg(Partida.Idioma.ENG);
        dawgCAST = new Dawg(Partida.Idioma.CAST);
        dawgCAT = new Dawg(Partida.Idioma.CAT);
    }

    /**
     * Prueba el método dividirPalabra para verificar
     * la correcta división de palabras considerando digrafos/trigrafos en diferentes idiomas.
     * Casos de prueba:
     * <ul>
     *   <li>Inglés: "L·LUVIA" se divide como ["L·L", "U", "V", "I", "A"]</li>
     *   <li>Castellano: "CHACAL" se divide como ["CH", "A", "C", "A", "L"]</li>
     *   <li>Catalán: "NYACAL" se divide como ["NY", "A", "C", "A", "L"]</li>
     * </ul>
     */
    @Test
    public void testDividirPalabra() {
        List<String> esperado = Arrays.asList("L·L", "U", "V", "I", "A");
        assertEquals(esperado, dawgING.dividirPalabra("L·LUVIA"));

        esperado = Arrays.asList("CH", "A", "C", "A", "L");
        assertEquals(esperado, dawgCAST.dividirPalabra("CHACAL"));

        esperado = Arrays.asList("NY", "A", "C", "A", "L");
        assertEquals(esperado, dawgCAT.dividirPalabra("NYACAL"));
    }

    /**
     * Prueba el método existePalabra para validar:
     * <ul>
     *   <li>Palabras existentes en cada diccionario (CRAZY, ARRIBA, IL·LEGAL)</li>
     *   <li>Palabras inexistentes o vacías ("cas", "perros", "")</li>
     * </ul>
     */
    @Test
    public void testExistePalabra() {
        assertTrue(dawgING.existePalabra("CRAZY"));
        assertTrue(dawgCAST.existePalabra("ARRIBA"));
        assertTrue(dawgCAT.existePalabra("IL·LEGAL"));


        assertFalse(dawgING.existePalabra("cas"));
        assertFalse(dawgCAST.existePalabra("perros"));
        assertFalse(dawgCAT.existePalabra(""));
    }

    /**
     * Prueba el método casillaCorrecta para verificar:
     * <ul>
     *   <li>Coordenadas válidas: (0,0) y (14,14)</li>
     *   <li>Coordenadas inválidas: (-1,0) y (15,15)</li>
     * </ul>
     */
    @Test
    public void testCasillaCorrecta() {
        assertTrue(dawgING.casillaCorrecta(0, 0));
        assertTrue(dawgCAT.casillaCorrecta(14, 14));
        assertFalse(dawgCAST.casillaCorrecta(-1, 0));
        assertFalse(dawgING.casillaCorrecta(15, 15));
    }
}
