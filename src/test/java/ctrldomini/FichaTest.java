package ctrldomini;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests para probar las funcionalidades de la clase Ficha
 *
 * @author: Paula Pérez
 */
public class FichaTest {

    @Test
    public void testCrearFicha() {
        Ficha ficha = new Ficha("A", 1);
        assertEquals("La letra de la ficha debe ser A", "A", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConCaracterEspecial() {
        Ficha ficha = new Ficha("Ñ", 8);
        assertEquals("La letra de la ficha debe ser Ñ", "Ñ", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraMinuscula() {
        Ficha ficha = new Ficha("e", 1);
        assertEquals("Debe convertir la letra a mayúscula e", "E", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraMayorLongitud() {
        Ficha ficha = new Ficha("RR", 8);
        assertEquals("La letra de la ficha debe ser RR", "RR", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraCaracterEspecial() {
        Ficha ficha = new Ficha("L·L", 10);
        assertEquals("La letra de la ficha debe ser L·L", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraCaracterEspecialMinuscula() {
        Ficha ficha = new Ficha("l·l", 10);
        assertEquals("Debe convertir la letra a mayúscula  l·l", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }
}

// TODO: hacer un test para mirar que la letra pertenezca al diccionario?

/* el orden de las assertions es
assertEquals(expected, actual);
assertEquals(String mensaje, expected, actual);
 */
