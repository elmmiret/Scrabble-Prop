package algorisme;

import static org.junit.Assert.*;

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
        dawg = new Dawg(Partida.Idioma.CAST, "src/main/java/archivos/castellano.txt");

        // Insertamos algunas palabras manualmente para pruebas
        dawg.insertar("IL·LOGICO");
        dawg.insertar("IL·LEGIBLE");
        dawg.insertar("IL·LEGAL");
        dawg.insertar("CARRERA"); // prueba con dígrafo "ll"
        dawg.acabar(); // Finalizamos la construcción
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
        assertTrue(dawg.existePalabra("PERRO"));
        assertTrue(dawg.existePalabra("LLUVIA"));

        assertFalse(dawg.existePalabra("uyuyu"));
        assertFalse(dawg.existePalabra("perros"));
        assertFalse(dawg.existePalabra(""));
        assertFalse(dawg.existePalabra("xyz"));
    }

    @Test
    public void testCasillaCorrecta() {
        assertTrue(dawg.casillaCorrecta(0, 0));
        assertTrue(dawg.casillaCorrecta(14, 14));
        assertFalse(dawg.casillaCorrecta(-1, 0));
        assertFalse(dawg.casillaCorrecta(15, 15));
    }
}
