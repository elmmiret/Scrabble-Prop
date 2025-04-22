package algorisme;

import static org.junit.Assert.*;

import algorisme.Dawg;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import ctrldomini.*;

public class DawgTest {
    private Dawg dawg;

    @Before
    public void setUp() {
        // Usamos un DAWG pequeño para pruebas
        dawg = new Dawg(Partida.Idioma.CAST);

        // Insertamos algunas palabras manualmente para pruebas
        dawg.insertar2("IL·LOGICO");
        dawg.insertar2("IL·LEGIBLE");
        dawg.insertar2("IL·LEGAL");
        dawg.insertar2("DIARREA"); // prueba con dígrafo "ll"
        dawg.acabar2(); // Finalizamos la construcción
    }

    @Test
    public void testDividirPalabra() {
        List<String> esperado = Arrays.asList("L·L", "U", "V", "I", "A");
        assertEquals(esperado, dawg.dividirPalabra("L·LUVIA"));

        esperado = Arrays.asList("CH", "A", "C", "A", "L");
        assertEquals(esperado, dawg.dividirPalabra("CHACAL"));
    }

    @Test
    public void testExistePalabra() {
        assertTrue(dawg.existePalabra("IL·LOGICO"));
        assertTrue(dawg.existePalabra("IL·LEGIBLE"));
        /*assertTrue(dawg.existePalabra("perro"));
        assertTrue(dawg.existePalabra("lluvia"));

        assertFalse(dawg.existePalabra("cas"));
        assertFalse(dawg.existePalabra("perros"));
        assertFalse(dawg.existePalabra(""));
        assertFalse(dawg.existePalabra("xyz"));*/
    }

    @Test
    public void testInsertarDiccionario() {
        // Verificamos que las palabras insertadas en el setUp existen
        assertTrue(dawg.existePalabra("DIARREA"));
        assertTrue(dawg.existePalabra("IL·LEGAL"));
    }

    @Test
    public void testCasillaCorrecta() {
        assertTrue(dawg.casillaCorrecta(0, 0));
        assertTrue(dawg.casillaCorrecta(14, 14));
        assertFalse(dawg.casillaCorrecta(-1, 0));
        assertFalse(dawg.casillaCorrecta(15, 15));
    }

    @Test
    public void testGetNumeroNodes() {
        int numNodes = dawg.getNumeroNodes();
        assertTrue(numNodes > 0);
    }
}
