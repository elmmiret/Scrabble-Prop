package ctrldomini;

import static org.junit.Assert.*;

import algorisme.Dawg;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

public class DawgTest {
    private Dawg dawg;

    @Before
    public void setUp() {
        // Usamos un DAWG pequeño para pruebas
        dawg = new Dawg(Partida.Idioma.CAST);

        // Insertamos algunas palabras manualmente para pruebas
        dawg.insertar2("casa");
        dawg.insertar2("casas");
        dawg.insertar2("casar");
        dawg.insertar2("perro");
        dawg.insertar2("lluvia"); // prueba con dígrafo "ll"
        dawg.acabar2(); // Finalizamos la construcción
    }

    @Test
    public void testDividirPalabra() {
        List<String> esperado = Arrays.asList("ll", "u", "v", "i", "a");
        assertEquals(esperado, dawg.dividirPalabra("lluvia"));

        esperado = Arrays.asList("ch", "a", "c", "a", "l");
        assertEquals(esperado, dawg.dividirPalabra("chacal"));
    }

    @Test
    public void testExistePalabra() {
        assertTrue(dawg.existePalabra("CANOA"));
        assertTrue(dawg.existePalabra("casas"));
        assertTrue(dawg.existePalabra("perro"));
        assertTrue(dawg.existePalabra("lluvia"));

        assertFalse(dawg.existePalabra("cas"));
        assertFalse(dawg.existePalabra("perros"));
        assertFalse(dawg.existePalabra(""));
        assertFalse(dawg.existePalabra("xyz"));
    }

    /*@Test
    public void testExistePrefijo() {
        assertTrue(dawg.existePrefijo("cas"));
        assertTrue(dawg.existePrefijo("per"));
        assertTrue(dawg.existePrefijo("llu"));

        assertFalse(dawg.existePrefijo("xyz"));
        assertFalse(dawg.existePrefijo("casam"));
    }*/

    @Test
    public void testInsertarDiccionario() {
        // Verificamos que las palabras insertadas en el setUp existen
        assertTrue(dawg.existePalabra("casa"));
        assertTrue(dawg.existePalabra("perro"));
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
