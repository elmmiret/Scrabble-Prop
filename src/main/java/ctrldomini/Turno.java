package ctrldomini;
import java.util.Map;
import java.util.HashMap;

public class Turno {
    private Partida partida;
    private int numero;
    private Perfil jugador;
    // mapa de ficha (letra y puntuación) y la cantidad que tienes
    private Map<Ficha,Integer> atrilJ1; 
    private Map<Ficha,Integer> atrilJ2;
    public static final int MAX_FICHAS = 7;

    public enum TipoJugada {
        cambiar, pasar, colocar
    }

    private TipoJugada tipoJugada;


    // Constructora
    public Turno(Partida partida, Perfil jugador, TipoJugada tipoJugada) {
        this.partida = partida;
        this.numero = 0;
        this.jugador = jugador;
        this.atrilJ1 = new HashMap<>();
        this.atrilJ2 = new HashMap<>();
        this.tipoJugada = tipoJugada;
    }


    // Métodos
    public int getNroTurno() {
        return numero;
    }

    public Perfil getJugador() {
        return jugador;
    }

    public Map<Ficha,Integer> getAtrilJ1() {
        return atrilJ1;
    }

    public Map<Ficha,Integer> getAtrilJ2() {
        return atrilJ2;
    }

    public TipoJugada getTipoJugada() {
        return tipoJugada;
    }

    public int cambiarFichas(Map<Ficha,Integer> atril, Ficha f) {
        if (atril.get(f) == 0) return 0;
        else {
            atril.put(f, atril.get(f) - 1);
            return 1;
        }
    }

    public void robarFichas(Map<Ficha,Integer> atril) {
        while (atril.size() < MAX_FICHAS && !partida.getBolsa().isEmpty()) {
            Ficha nuevaFicha = partida.getBolsa().remove();
            atril.put(nuevaFicha, atril.getOrDefault(nuevaFicha, 0) + 1); // si no existe la clave, se añade con valor 1, else se incrementa su valor
        }
    }

    public void pasarTurno() {
        ++numero;
        if (jugador == partida.getCreador()) jugador = partida.getOponente();
        else jugador = partida.getCreador();
    }

    public void colocarFicha(Ficha f, int x, int y) {
        // segun el algoritmo
    }

    public void retirarFicha(int x, int y) {
        if (partida.getTablero().getFicha(x, y) == null) ;// error
        else {
            //Tablero[x][y] que sea null
        }

    }

    public void confirmarPalabra() {

    }

    public String pedirPista() {
        return null; // no implementado aun
    }

}
