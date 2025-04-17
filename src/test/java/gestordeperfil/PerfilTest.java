package gestordeperfil;
        
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PerfilTest {

    private Perfil perfil;

    @Before
    public void setUp() {
        perfil = new Perfil("user1", "Testeando12", "my recovery phrase");
    }

    @Test
    public void testConstructorInitialization() {
        assertEquals("user1", perfil.getUsername());
        assertEquals("Testeando12", perfil.getPassword());
        assertEquals("my recovery phrase", perfil.getFraseRecuperacion());
        assertEquals(0, perfil.getPartidasJugadas());
        assertEquals(0, perfil.getPartidasGanadas());
        assertEquals(0, perfil.getPartidasPerdidas());
        assertEquals(0, perfil.getPuntos());
    }

    @Test
    public void testCambiarUsername() {
        perfil.cambiarUsername("newUser");
        assertEquals("newUser", perfil.getUsername());
    }

    @Test
    public void testCambiarPassword() {
        perfil.cambiarPassword("newPassword");
        assertEquals("newPassword", perfil.getPassword());
    }

    @Test
    public void testIncrementarPartidasJugadas() {
        perfil.incrementarPartidasJugadas();
        assertEquals(1, perfil.getPartidasJugadas());
        perfil.incrementarPartidasJugadas();
        assertEquals(2, perfil.getPartidasJugadas());
    }

    @Test
    public void testIncrementarPartidasGanadas() {
        perfil.incrementarPartidasGanadas();
        assertEquals(1, perfil.getPartidasGanadas());
        perfil.incrementarPartidasGanadas();
        assertEquals(2, perfil.getPartidasGanadas());
    }

    @Test
    public void testIncrementarPartidasPerdidas() {
        perfil.incrementarPartidasPerdidas();
        assertEquals(1, perfil.getPartidasPerdidas());
        perfil.incrementarPartidasPerdidas();
        assertEquals(2, perfil.getPartidasPerdidas());
    }

    @Test
    public void testIncrementarPuntos() {
        perfil.incrementarPuntos(10);
        assertEquals(10, perfil.getPuntos());
        perfil.incrementarPuntos(-5);
        assertEquals(5, perfil.getPuntos());
        perfil.incrementarPuntos(3);
        assertEquals(8, perfil.getPuntos());
    }

    @Test
    public void testMultipleIncrementsAndGetters() {
        perfil.incrementarPartidasJugadas();
        perfil.incrementarPartidasGanadas();
        perfil.incrementarPartidasPerdidas();
        perfil.incrementarPuntos(15);

        assertEquals(1, perfil.getPartidasJugadas());
        assertEquals(1, perfil.getPartidasGanadas());
        assertEquals(1, perfil.getPartidasPerdidas());
        assertEquals(15, perfil.getPuntos());
    }

    @Test
    public void testFraseRecuperacionRemainsUnchanged() {
        perfil.cambiarUsername("anotherUser");
        perfil.cambiarPassword("anotherPass");
        assertEquals("my recovery phrase", perfil.getFraseRecuperacion());
    }
}