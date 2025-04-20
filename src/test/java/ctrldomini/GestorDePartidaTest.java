package ctrldomini;

import gestordeperfil.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;
import ranking.Ranking;

/**
 * Clase de pruebas unitarias para verificar el funcionamiento del GestorDePartida.
 * Contiene tests para validar la creación, gestión y eliminación de partidas,
 * así como las interacciones con perfiles de jugadores.
 */
public class GestorDePartidaTest {
    private GestorDePerfil gestorPerfiles;
    private GestorDePartida gestorPartidas;
    private Perfil jugador1;
    private Perfil jugador2;
    private final int ID_PARTIDA = 1;

    /**
     * Configura el entorno de prueba antes de cada test.
     * Crea perfiles de prueba y una instancia de GestorDePartida.
     */
    @Before
    public void setUp() {
        Ranking ranking = new Ranking();
        gestorPerfiles = new GestorDePerfil(ranking);
        gestorPerfiles.crearPerfil("user1", "Pass123!", "Frase1");
        gestorPerfiles.crearPerfil("user2", "Pass456!", "Frase2");
        jugador1 = gestorPerfiles.getPerfil("user1");
        jugador2 = gestorPerfiles.getPerfil("user2");
        gestorPartidas = new GestorDePartida(gestorPerfiles);
    }

    @Test
    public void testConstructorInicializaMapaVacío() {
        Ranking newRanking = new Ranking();
        GestorDePerfil newGestorDePerfil = new GestorDePerfil(newRanking);
        GestorDePartida newGestorDePartida = new GestorDePartida(newGestorDePerfil);
        assertTrue(newGestorDePartida.getPartidas().isEmpty());
    }

}