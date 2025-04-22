package gestordepartida;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;

/**
 * Tests para probar las funcionalidades de la clase Ficha
 *
 * @author Paula Pérez
 */
public class FichaTest {
    private Ficha ficha;
    /**
     * Configura el entorno de prueba antes de cada test.
     * Crea una instancia vacía de Ficha.
     */
    @Before
    public void setUp() {
        Ficha ficha;
    }

    /**
     * Prueba la creación básica de una ficha con letra mayúscula y puntuación válida.
     * Verifica que ambos valores se almacenen correctamente.
     */
    @Test
    public void testCrearFicha() {
        ficha = new Ficha("A", 1);
        assertEquals("La letra de la ficha debe ser A", "A", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    /**
     * Prueba la creación de una ficha con un carácter especial del español (Ñ).
     * Verifica que se acepten y almacenen correctamente caracteres no ASCII.
     */
    @Test
    public void testFichaConCaracterEspecial() {
        ficha = new Ficha("Ñ", 8);
        assertEquals("La letra de la ficha debe ser Ñ", "Ñ", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    /**
     * Prueba la conversión automática a mayúsculas al crear una ficha.
     * Verifica que las letras minúsculas en el constructor se conviertan correctamente.
     */
    @Test
    public void testFichaConLetraMinuscula() {
        ficha = new Ficha("e", 1);
        assertEquals("Debe convertir la letra a mayúscula e", "E", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 1", 1, ficha.getPuntuacion());
    }

    /**
     * Prueba la creación de fichas compuestas por múltiples caracteres.
     * Verifica el correcto almacenamiento de secuencias como "RR".
     */
    @Test
    public void testFichaConLetraMayorLongitud() {
        ficha = new Ficha("RR", 8);
        assertEquals("La letra de la ficha debe ser RR", "RR", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 8", 8, ficha.getPuntuacion());
    }

    /**
     * Prueba la creación de fichas compuestas por múltiples caracteres.
     * Verifica el correcto almacenamiento de secuencias como "L·L".
     */
    @Test
    public void testFichaConLetraCaracterEspecial() {
        ficha = new Ficha("L·L", 10);
        assertEquals("La letra de la ficha debe ser L·L", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }

    /**
     * Prueba la conversión a mayúsculas con caracteres especiales.
     * Verifica que los símbolos se mantengan mientras se convierten las letras.
     */
    @Test
    public void testFichaConLetraCaracterEspecialMinuscula() {
        ficha = new Ficha("l·l", 10);
        assertEquals("Debe convertir la letra a mayúscula  l·l", "L·L", ficha.getLetra());
        assertEquals("La puntuación de la ficha debe ser 10", 10, ficha.getPuntuacion());
    }
}
