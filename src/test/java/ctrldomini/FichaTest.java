package ctrldomini;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests para probar las funcionalidades de la clase Ficha
 *
 * @author: Paula Pérez
 */
public class FichaTest {

    @BeforeEach
    void setUp() {
        Ficha ficha;
    }

    @Test
    public void testCrearFicha() {
        ficha = new Ficha("A", 1);
        assertEquals("La letra de la ficha debe ser A", "A", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConCaracterEspecial() {
        ficha = new Ficha("Ñ", 8);
        assertEquals("La letra de la ficha debe ser Ñ", "Ñ", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraMinuscula() {
        ficha = new Ficha("e", 1);
        assertEquals("Debe convertir la letra a mayúscula e", "E", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraMayorLongitud() {
        ficha = new Ficha("RR", 8);
        assertEquals("La letra de la ficha debe ser RR", "RR", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraCaracterEspecial() {
        ficha = new Ficha("L·L", 10);
        assertEquals("La letra de la ficha debe ser L·L", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }

    @Test
    public void testFichaConLetraCaracterEspecialMinuscula() {
        ficha = new Ficha("l·l", 10);
        assertEquals("Debe convertir la letra a mayúscula  l·l", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }
}
