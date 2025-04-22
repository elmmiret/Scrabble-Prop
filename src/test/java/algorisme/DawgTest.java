package algorisme;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import gestordepartida.Partida;

public class DawgTest {
    private Dawg dawgING;
    private Dawg dawgCAST;
    private Dawg dawgCAT;


    @Before
    public void setUp() {
        dawgING = new Dawg(Partida.Idioma.ENG);
        dawgCAST = new Dawg(Partida.Idioma.CAST);
        dawgCAT = new Dawg(Partida.Idioma.CAT);
    }

    @Test
    public void testDividirPalabra() {
        List<String> esperado = Arrays.asList("L·L", "U", "V", "I", "A");
        assertEquals(esperado, dawgING.dividirPalabra("L·LUVIA"));

        esperado = Arrays.asList("CH", "A", "C", "A", "L");
        assertEquals(esperado, dawgCAST.dividirPalabra("CHACAL"));

        esperado = Arrays.asList("NY", "A", "C", "A", "L");
        assertEquals(esperado, dawgCAT.dividirPalabra("NYACAL"));
    }

    @Test
    public void testExistePalabra() {
        assertTrue(dawgING.existePalabra("CRAZY"));
        assertTrue(dawgCAST.existePalabra("ARRIBA"));
        assertTrue(dawgCAT.existePalabra("IL·LEGAL"));


        assertFalse(dawgING.existePalabra("cas"));
        assertFalse(dawgCAST.existePalabra("perros"));
        assertFalse(dawgCAT.existePalabra(""));
    }

    @Test
    public void testCasillaCorrecta() {
        assertTrue(dawgING.casillaCorrecta(0, 0));
        assertTrue(dawgCAT.casillaCorrecta(14, 14));
        assertFalse(dawgCAST.casillaCorrecta(-1, 0));
        assertFalse(dawgING.casillaCorrecta(15, 15));
    }
}
