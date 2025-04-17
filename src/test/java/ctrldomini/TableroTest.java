package ctrldomini;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import exceptions.*;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;


/**
 * Tests para probar las funcionalidades de la clase Tablero
 *
 * @author: Paula Pérez
 */
public class TableroTest {

    @Test
    public void testSetYGetFicha() throws CoordenadaFueraDeRangoException, CasillaOcupadaException  {
        Tablero tablero = new Tablero(CAST);
        Ficha f = new Ficha("A", 1);
        tablero.setFicha(f, 'D', 4);
        assertEquals("Debe devolver la letra de la ficha colocada", "A", tablero.getFicha(3, 3).getLetra());
    }

    @Test
    public void testGetFichaSinColocar() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        assertNull("Debe devolver null si no hay ficha colocada", tablero.getFicha(6, 5));
    }

    @Test
    public void testGetModificadorSinNada() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 1);
        assertNull("Debe devolver null si no hay modificador", m);
    }

    @Test
    public void testGetModificador() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        Tablero tablero = new Tablero(CAST);
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 0);
        assertEquals("Debe devolver tripleTantoDePalabra", Tablero.TipoModificador.tripleTantoDePalabra, m);
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testGetFichaCoordenadasFueraDeRango() throws Exception {
        Tablero tablero = new Tablero(CAST);
        tablero.getFicha(-1, -1);
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testSetFichasFueraDeRango() throws Exception {
        Tablero tablero = new Tablero(CAST);
        Ficha f = new Ficha("C", 1);
        tablero.setFicha(f, 'Z', 30);
    }

    @Test(expected = CasillaOcupadaException.class)
    public void testSetFichaYaOcupada() throws Exception {
        Tablero tablero = new Tablero(CAST);
        Ficha f1 = new Ficha("A", 1);
        Ficha f2 = new Ficha("B", 2);
        tablero.setFicha(f1, 'F', 11);
        tablero.setFicha(f2, 'F', 11);
    }

    @Test
    public void testSetYGetAbecedario() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.setLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver el set con la letra A", Set.of("A"), tablero.getAbecedario(2, 3));
    }

    @Test
    public void testBorrarLetraAbecedario() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.setLetraAbecedario("A", 2, 3);
        tablero.borrarLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver un set vacío", Set.of(), tablero.getAbecedario(2, 3));
    }

    @Test
    public void testBorrarLetraNoExistente() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.borrarLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver un set vacío", Set.of(), tablero.getAbecedario(2, 3));
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testSetLetraAbecedarioFueraDeRango() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.setLetraAbecedario("A", -1, 16);
    }

    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testBorrarLetraAbecedarioFueraDeRango() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.borrarLetraAbecedario("A", 20, 5);
    }

    @Test
    public void testGetCasilla() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        SimpleEntry<SimpleEntry<Ficha, Tablero.TipoModificador>, Set<String>> casilla = tablero.getCasilla(0, 0);
        assertNull("Debe devolver null", casilla.getKey().getKey());
        assertEquals("Debe devolver tripleTantoDePalabra como modificador", Tablero.TipoModificador.tripleTantoDePalabra, casilla.getKey().getValue());
        assertEquals("Debe devolver un set vacío para el abecedario", Set.of(), casilla.getValue());
    }

    @Test
    public void testClearAbecedario() throws CoordenadaFueraDeRangoException {
        Tablero tablero = new Tablero(CAST);
        tablero.setLetraAbecedario("A", 2, 3);
        tablero.setLetraAbecedario("B", 2, 3);
        tablero.clearAbecedario(2, 3);
        assertEquals("Debe devolver un set vacío para el abecedario", Set.of(), tablero.getAbecedario(2, 3));
    }

    @Test
    public void testTableroVacio()  {
        Tablero tablero = new Tablero(CAST);
        assertEquals("Debe devolver true", true, tablero.estaVacio());
        Ficha f1 = new Ficha("A", 1);
        tablero.setFicha(f1, 'A', 1);
        assertEquals("Debe devolver false", false, tablero.estaVacio());
    }
}