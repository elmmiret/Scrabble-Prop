package ctrldomini;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import exceptions.*;

/**
 * Tests para probar las funcionalidades de la clase Tablero
 *
 * @author: Paula Pérez
 */
public class TableroTest {

    @Test
    public void testSetYGetFicha() throws CoordenadaFueraDeRangoException, CasillaOcupadaException  {
        Tablero tablero = new Tablero();
        Ficha f = new Ficha("A", 1);
        tablero.setFicha(f, 'D', 4);
        assertEquals("Debe devolver la letra de la ficha colocada", "A", tablero.getFicha(3, 3).getLetra());
    }

    @Test
    public void testGetFichaSinColocar() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero();
        assertNull("Debe devolver null si no hay ficha colocada", tablero.getFicha(6, 5));
    }

    @Test
    public void testGetModificadorSinNada() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero();
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 1);
        assertNull("Debe devolver null si no hay modificador", m);
    }

    @Test
    public void testGetModificador() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        Tablero tablero = new Tablero();
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 0);
        assertEquals("Debe devolver tripleTantoDePalabra", Tablero.TipoModificador.tripleTantoDePalabra, m);
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testGetFichaCoordenadasFueraDeRango() throws Exception {
        Tablero tablero = new Tablero();
        tablero.getFicha(-1, -1);
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testSetFichasFueraDeRango() throws Exception {
        Tablero tablero = new Tablero();
        Ficha f = new Ficha("C", 1);
        tablero.setFicha(f, 'Z', 30);
    }

    @Test(expected = CasillaOcupadaException.class)
    public void testSetFichaYaOcupada() throws Exception {
        Tablero tablero = new Tablero();
        Ficha f1 = new Ficha("A", 1);
        Ficha f2 = new Ficha("B", 2);
        tablero.setFicha(f1, 'F', 11);
        tablero.setFicha(f2, 'F', 11);
    }
}