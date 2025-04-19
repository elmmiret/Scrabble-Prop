package ctrldomini;

import algorisme.*;
import exceptions.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Esta clase representa un turno dentro de una partida de Scrabble.
 * Gestiona las acciones realizadas por los jugadores como cambiar fichas,
 * pasar el turno, colocar palabras, y robar fichas.
 * Contiene información sobre las fichas que hay en los atriles y los puntos de cada jugador.
 *
 * @author Paula Pérez
 */
public class Turno {
    private Partida partida;
    // private int numero; no cal pq lo podemos buscar en el indice ed la estructura rondas en partida
    // TODO: cambiar a solo id de jugador
    private Perfil jugador; // null si es la IA
    // si el numero de turno es 0 que se inicialicen los atriles
    private Map<Ficha,Integer> atrilJ1; // creador
    private Map<Ficha,Integer> atrilJ2; // oponente oi IA
    private puntosJ1; // creador
    private puntosJ2; // oponente oi IA
    public static final int MAX_FICHAS = 7;

    // añadir finalizarpartida?
    public enum TipoJugada {
        cambiar, pasar, colocar
    }
    private TipoJugada tipoJugada;


    // CONSTRUCTORA

    /**
     * Construye una instancia de Turno.
     * Inicializa un nuevo turno con la partida y el jugador actual.
     *
     * @param partida La partida en la que se está jugando.
     * @param jugador El perfil del jugador que realiza el turno.
     */
    public Turno(Partida partida, Perfil jugador) {
        this.partida = partida;
        this.jugador = jugador;
        this.atrilJ1 = new HashMap<>();
        this.atrilJ2 = new HashMap<>();
        this.tipoJugada = tipoJugada;
    }

    // MÉTODOS

    /**
     * Obtiene el jugador actual del turno.
     *
     * @return El perfil del jugador actual.
     */
    public Perfil getJugador() {
        return jugador;
    }

    /**
     * Obtiene el atril del jugador creador.
     *
     * @return El atril del jugador creador.
     */
    public Map<Ficha,Integer> getAtrilJ1() {
        return atrilJ1;
    }

    /**
     * Obtiene el atril del jugador oponente o IA.
     *
     * @return El atril del jugador oponente o IA.
     */
    public Map<Ficha,Integer> getAtrilJ2() {
        return atrilJ2;
    }

    /**
     * Obtiene el tipo de jugada realizada en el turno.
     *
     * @return El tipo de jugada (cambiar, pasar, colocar).
     */
    public TipoJugada getTipoJugada() {
        return tipoJugada;
    }

    /**
     * Establece el tipo de jugada realizada en el turno.
     *
     * @param tipoJugada El tipo de jugada a establecer.
     */
    public void setTipoJugada(TipoJugada tipojugada) {
        this tipoJugada = tipoJugada;
    }

    /**
     * Cambia una ficha del atril por otra de la bolsa.
     *
     * @param atril El atril del jugador.
     * @param f La ficha a cambiar.
     * @return debereia ser un void?  TODO: revisar lo q hice
     */
    public int cambiarFichas(Map<Ficha,Integer> atril, Ficha f) {
        if (atril.get(f) == 0) return 0;
        else {
            atril.put(f, atril.get(f) - 1);
            return 1;
        }
        setTipoJugada(TipoJugada.cambiar);
        avanzarTurno();
    }

    /**
     * Roba fichas de la bolsa.
     * Acaba cuando llena al máximo el atril o no quedan mas fichas en la bolsa.
     *
     * @param atril El atril del jugador.
     */
    public void robarFichas(Map<Ficha,Integer> atril) {
        while (atril.size() < MAX_FICHAS || !partida.getBolsa().isEmpty()) {
            Ficha nuevaFicha = partida.getBolsa().remove();
            atril.put(nuevaFicha, atril.getOrDefault(nuevaFicha, 0) + 1); // si no existe la clave, se añade con valor 1, else se incrementa su valor
        }
    }

    /**
     * Pasa el turno al siguiente jugador.
     */
    public void pasarTurno(){
        setTipoJugada(TipoJugada.pasar);
        avanzarTurno();
    }

    /**
     * Avanza el turno al siguiente jugador.
     */
    public void avanzarTurno() {
        Perfil nextJugador;
        if (jugador == partida.getCreador()) nextjugador = partida.getOponente();
        else nextjugador = partida.getCreador();
        partida.nuevoTurno(nextjugador); // revisar
    }

    /**
     * TODO: reformular pq antes estab ahecho para ficha por ficha
     *
     * Coloca una palabra en el tablero.
     *
     * @param f La ficha a colocar.
     * @param x La coordenada X en el tablero.
     * @param y La coordenada Y en el tablero.
     */
    public void colocarPalabra(Ficha f, int x, int y) {
        // segun el algoritmo
        // TODO: sumar puntos, tener en cuenta de que si ya habia algo puestom se cuentan los puntos
        //  que ya habia pero no se cuentan los modificadores
        setTipoJugada(TipoJugada.colocar);
        if (jugador == partida.getCreador()) robarFichas(atrilJ1);
        else robarFichas(atrilJ2);
        avanzarTurno();
    }

    // la necesitare?
    /**
     * Retira una ficha del tablero.
     *
     * @param x La coordenada X de la ficha a retirar.
     * @param y La coordenada Y de la ficha a retirar.
     */
    public void retirarFicha(int x, int y) throws CoordenadaFueraDeRangoException{
        if (partida.getTablero().getFicha(x, y) == null);
        else partida.getTablero().serFicha(null, 'A'+x, y+1);
    }

    /**
     * Solicita una pista para el jugador actual.
     *
     * @return Una pista.
     */
    public String pedirPista() {
        return null; // no implementado aun
    }

    /**
     * Inicializa los atriles de ambos jugadores con fichas de la bolsa.
     */
    public void inicializarAtriles() {
        while (atrilJ1.size() < MAX_FICHAS) {
            Ficha ficha = partida.getBolsa().poll();
            atrilJ1.put(ficha, atrilJ1.getOrDefault(ficha, 0) + 1);
        }

        while (atrilJ2.size() < MAX_FICHAS) {
            Ficha ficha = partida.getBolsa().poll();
            atrilJ2.put(ficha, atrilJ2.getOrDefault(ficha, 0) + 1);
        }
    }

}
