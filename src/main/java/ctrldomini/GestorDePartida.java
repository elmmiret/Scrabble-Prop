package ctrldomini;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import gestordeperfil.*;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;

public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    private GestorDePerfil gestorDePerfil;

    public GestorDePartida(GestorDePerfil gDP) {
        partidas = new HashMap<>();
        gestorDePerfil = gDP;
    }

    public Map<Integer, Partida> getPartidas() {
        return partidas;
    }

    public boolean verificarFraseRecuperacion(String username, String frase) {
        return gestorDePerfil.esFraseRecuperacionCorrecta(username, frase);
    }

    public void obtenerRepresentacionTablero(Tablero tablero) throws CoordenadaFueraDeRangoException {
        tablero.imprimirTablero();
    }

    public boolean colocarPalabra(Turno turno, String palabra, int x, int y, String orientacion)
            throws CasillaOcupadaException, CoordenadaFueraDeRangoException {
        return turno.colocarPalabra(palabra, x, y, orientacion);
    }

    public boolean cambiarFichas(Turno turno, Map<Ficha, Integer> atril, List<String> letras) {
        Map<Ficha, Integer> cambio = new HashMap<>();
        for (String letra : letras) {
            Ficha fichaReal = null;
            for (Ficha fAtril : atril.keySet()) {
                if (fAtril.getLetra().equalsIgnoreCase(letra)) {
                    fichaReal = fAtril;
                    break;
                }
            }
            if (fichaReal == null) {
                return false;
            }
            cambio.put(fichaReal, cambio.getOrDefault(fichaReal, 0) + 1);
        }
        // Verificar cantidad suficiente
        for (Map.Entry<Ficha, Integer> entry : cambio.entrySet()) {
            Ficha f = entry.getKey();
            int needed = entry.getValue();
            if (!atril.containsKey(f) || atril.get(f) < needed) {
                return false;
            }
        }
        turno.cambiarFichas(atril, cambio);
        return true;
    }

    public Partida crearPartida(int id, String nombre, Partida.Idioma idioma,
                                Perfil jugadorPrincipal, Partida.Modo modo,
                                Perfil oponente, int dificultad) {
        Partida nuevaPartida;
        if (modo == Partida.Modo.PvP) {
            nuevaPartida = new Partida(jugadorPrincipal, oponente, id, nombre, modo, idioma);
        } else {
            nuevaPartida = new Partida(jugadorPrincipal, id, nombre, modo, idioma, dificultad);
        }
        partidas.put(id, nuevaPartida);
        return nuevaPartida;
    }


    public Partida obtenerPartida(int idPartida) {
        return partidas.get(idPartida);
    }

    public boolean existePartidaJugador(Perfil jugador, int idpartida) {
        Partida p = partidas.get(idpartida);
        return p != null && (p.getCreador().equals(jugador) || (p.getOponente() != null && p.getOponente().equals(jugador)));
    }

    public List<Partida> obtenerPartidasJugador(Perfil jugador) {
        List<Partida> partidasJugador = new ArrayList<>();
        for (Partida partida : partidas.values()) {
            if (partida.getCreador().equals(jugador) || (partida.getOponente() != null && partida.getOponente().equals(jugador))) {
                partidasJugador.add(partida);
            }
        }
        return partidasJugador;
    }

    public boolean eliminarPartida(int idPartida) {
        return partidas.remove(idPartida) != null;
    }

    public Map<Ficha, Integer> obtenerAtrilJugador(Partida partida, Perfil jugador) {
        Turno turno = partida.getRondas().get(partida.getRondas().size() - 1);
        if (jugador == null) return turno.getAtrilJ2();
        else return jugador.equals(partida.getCreador()) ? turno.getAtrilJ1() : turno.getAtrilJ2();
    }

}