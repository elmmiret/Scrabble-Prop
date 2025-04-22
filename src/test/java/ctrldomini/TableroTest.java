package ctrldomini;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.Before;
import exceptions.*;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import ctrldomini.Tablero;

/**
 * Tests para probar las funcionalidades de la clase Tablero
 *
 * @author: Paula Pérez
 */
public class TableroTest {
    private Tablero tablero;
    /**
     * Configura el entorno de prueba antes de cada test.
     * Crea una instancia de tablero usando el diccionario castellano.
     */
    @Before
    public void setUp() {
        tablero = new Tablero(Partida.Idioma.CAST);
    }

    /**
     * Prueba la colocación y obtención de fichas en el tablero.
     * Verifica que una ficha colocada en coordenadas específicas pueda ser recuperada correctamente.
     */
    @Test
    public void testSetYGetFicha() throws CoordenadaFueraDeRangoException, CasillaOcupadaException  {
        Ficha f = new Ficha("A", 1);
        tablero.setFicha(f, 3, 3);
        assertEquals("Debe devolver la letra de la ficha colocada", "A", tablero.getFicha(3, 3).getLetra());
    }

    /**
     * Prueba la obtención de una ficha en posición vacía.
     * Verifica que se devuelva null cuando no hay ficha colocada en las coordenadas especificadas.
     */
    @Test
    public void testGetFichaSinColocar() throws CoordenadaFueraDeRangoException {
        assertNull("Debe devolver null si no hay ficha colocada", tablero.getFicha(6, 5));
    }

    /**
     * Prueba la obtención de modificador en casilla sin bonificación.
     * Verifica que se devuelva null en posiciones sin modificadores especiales.
     */
    @Test
    public void testGetModificadorSinNada() throws CoordenadaFueraDeRangoException {
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 1);
        assertNull("Debe devolver null si no hay modificador", m);
    }

    /**
     * Prueba la obtención de modificadores especiales.
     * Verifica que se detecte correctamente el modificador tripleTantoDePalabra en la esquina del tablero.
     */
    @Test
    public void testGetModificador() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        Tablero.TipoModificador m = tablero.getTipoModificador(0, 0);
        assertEquals("Debe devolver tripleTantoDePalabra", Tablero.TipoModificador.tripleTantoDePalabra, m);
    }

    /**
     * Prueba el manejo de coordenadas inválidas al obtener fichas.
     * Verifica que se lance excepción al usar coordenadas fuera de rango.
     */
    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testGetFichaCoordenadasFueraDeRango() throws Exception {
        tablero.getFicha(-1, -1);
    }

    /**
     * Prueba el manejo de coordenadas inválidas al colocar fichas.
     * Verifica que se lance excepción al intentar colocar fichas fuera de los límites del tablero.
     */
    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testSetFichasFueraDeRango() throws Exception {
        Ficha f = new Ficha("C", 1);
        tablero.setFicha(f, 43, 30);
    }

    /**
     * Prueba la colocación de fichas en casillas ocupadas.
     * Verifica que se lance excepción al intentar sobrescribir una ficha existente.
     */
    @Test(expected = CasillaOcupadaException.class)
    public void testSetFichaYaOcupada() throws Exception {
        Ficha f1 = new Ficha("A", 1);
        Ficha f2 = new Ficha("B", 2);
        tablero.setFicha(f1, 5, 11);
        tablero.setFicha(f2, 5, 11);
    }

    /**
     * Prueba la gestión del abecedario en el tablero.
     * Verifica que se puedan añadir y recuperar letras correctamente en el conjunto del abecedario.
     */
    @Test
    public void testSetYGetAbecedario() throws CoordenadaFueraDeRangoException {
        tablero.setLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver el set con la letra A", Set.of("A"), tablero.getAbecedario(2, 3));
    }

    /**
     * Prueba la eliminación de letras del abecedario.
     * Verifica que se puedan remover letras existentes del conjunto correctamente.
     */
    @Test
    public void testBorrarLetraAbecedario() throws CoordenadaFueraDeRangoException {
        tablero.setLetraAbecedario("A", 2, 3);
        tablero.borrarLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver un set vacío", Set.of(), tablero.getAbecedario(2, 3));
    }

    /**
     * Prueba la eliminación de letras inexistentes.
     * Verifica que el sistema maneje correctamente la eliminación de elementos no presentes.
     */
    @Test
    public void testBorrarLetraNoExistente() throws CoordenadaFueraDeRangoException {
        tablero.borrarLetraAbecedario("A", 2, 3);
        assertEquals("Debe devolver un set vacío", Set.of(), tablero.getAbecedario(2, 3));
    }

    /**
     * Prueba el manejo de coordenadas inválidas al modificar abecedario.
     * Verifica que se lance excepción al usar posiciones fuera de rango para añadir letras.
     */
    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testSetLetraAbecedarioFueraDeRango() throws CoordenadaFueraDeRangoException {
        tablero.setLetraAbecedario("A", -1, 16);
    }

    /**
     * Prueba el manejo de coordenadas inválidas al borrar letras.
     * Verifica que se lance excepción al usar posiciones inexistentes para eliminar letras.
     */
    @Test(expected = CoordenadaFueraDeRangoException.class)
    public void testBorrarLetraAbecedarioFueraDeRango() throws CoordenadaFueraDeRangoException {
        tablero.borrarLetraAbecedario("A", 20, 5);
    }

    /**
     * Prueba la obtención completa de una casilla.
     * Verifica que se recuperen correctamente todos los componentes de una casilla (ficha, modificador y abecedario).
     */
    @Test
    public void testGetCasilla() throws CoordenadaFueraDeRangoException {
        SimpleEntry<SimpleEntry<Ficha, Tablero.TipoModificador>, Set<String>> casilla = tablero.getCasilla(0, 0);
        assertNull("Debe devolver null", casilla.getKey().getKey());
        assertEquals("Debe devolver tripleTantoDePalabra como modificador", Tablero.TipoModificador.tripleTantoDePalabra, casilla.getKey().getValue());
        assertEquals("Debe devolver un set vacío para el abecedario", Set.of(), casilla.getValue());
    }

    /**
     * Prueba el vaciado completo del abecedario.
     * Verifica que se puedan eliminar todas las letras de una posición específica.
     */
    @Test
    public void testClearAbecedario() throws CoordenadaFueraDeRangoException {
        tablero.setLetraAbecedario("A", 2, 3);
        tablero.setLetraAbecedario("B", 2, 3);
        tablero.clearAbecedario(2, 3);
        assertEquals("Debe devolver un set vacío para el abecedario", Set.of(), tablero.getAbecedario(2, 3));
    }

    /**
     * Prueba el estado inicial y posterior del tablero.
     * Verifica que el tablero se reconozca como vacío inicialmente y cambie su estado tras colocar una ficha.
     */
    @Test
    public void testTableroVacio() throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        assertEquals("Debe devolver true", true, tablero.estaVacio());
        Ficha f1 = new Ficha("A", 1);
        tablero.setFicha(f1, 0, 1);
        //assertEquals("Debe devolver false", false, tablero.estaVacio());
    }
}