package ctrldomini;

import algorisme.*;
import exceptions.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.*;

// TODO: concretar lo de los puntos de colocarPalabra
//       hacer que se llame a la IA para jugar
//       repasar driver de partida para que llame a turno (hacer driver turno)

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
     * Cambia un conjunto de fichas del atril por otras nuevas de la bolsa.
     * Primero se apartan, luego se roba, y por último se vuelven a insertar en la bolsa.
     *
     * @param atril El atril del jugador.
     * @param fichasParaCambiar Un mapa de fichas y la cantidad que desea cambiar.
     */
    public void cambiarFichas(Map<Ficha,Integer> atril, Map<Ficha,Integer> FichasParaCambiar) {
        Queue<Ficha> colaTemporal = new LinkedList<>();
        // añadir las fichas a la cola temporal y restarlas del atril
        for (Map.Entry<Ficha, Integer> entry : fichasParaCambiar.entrySet()) {
            Ficha ficha = entry.getKey();
            int cantidad = entry.getValue();

            for (int i = 0; i < cantidad; i++) {
                if (atril.containsKey(ficha) && atril.get(ficha) > 0) {
                    colaTemporal.add(ficha);
                    atril.put(ficha, atril.get(ficha) - 1);
                    if (atril.get(ficha) == 0) atril.remove(ficha);
                }
            }
        }

        robarFichas(atril);
        partida.getBolsa().addAll(colaTemporal);
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
     * Coloca una palabra en el tablero, calcula y asigna los puntos correspondientes.
     *
     * @param palabra La palabra que se quiere colocar.
     * @param x_ini La coordenada X inicial en el tablero.
     * @param y_ini La coordenada Y inicial en el tablero.
     * @param orientacion La dirección en la que se colocará la palabra ("vertical" u "horizontal").
     */
    public void colocarPalabra(String palabra, int x_ini, int y_ini, String orientacion) throws CoordenadaFueraDeRangoException{
        // existe la palabra, cabe en el tablero y coincide bien con todas las otras fichas
        int puntosPorSumar = 0;
        int modificadorPalabra = 1;
        if (partida.dawg.comprobarPalabra(partida.getTablero(), palabra, x_ini , y_ini , orientacion)) {
            List<String> fichas = dawg.dividirPalabra(String palabra);
            if (orientacion == "vertical") {
                for (int i = 0; i < fichas.size(); ++i) {
                    String letraBuscada = fichas.get(i);
                    Ficha fichaencontrada = null;
                    for (Map.Entry<Ficha, Integer> entry : mapaFichas.entrySet()) {
                        if (entry.getKey().getLetra().equals(letraBuscada)) {
                            fichaEncontrada = entry.getKey();
                            break;
                        }
                    }

                    if (fichaEncontrada == null); // TODO: no existe en el diccionario y habria que retornar algun tipo de error

                    Ficha f = fichaEncontrada;
                    partida.tablero.setFicha( f, 'A' + x_ini + i, y_ini);
                        if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == dobleTantoDePalabra && modificadorPalabra!= 3) modificadorPalabra = 2;
                        else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == tripleTantoDePalabra) modificadorPalabra = 3;
                        else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == tripleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*3
                        else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == dobleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*2
                        else puntosPorSumar += f.getPuntuacion()

                }
                if (modificadorPalabra != 1) puntosPorSumar *= modificadorPalabra;
                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;
                // TODO MUY IMPORTANTE: si se juntan otras palabras que ya habian puestas,
                // se tiene que sumar esa puntuacion pero SIN CONTAR MODIFICADORES, solo puntuacion de las fichas

            }
            else {
                for (int i = 0; i < fichas.size(); ++i) {
                    String letraBuscada = fichas.get(i);
                    Ficha fichaencontrada = null;
                    for (Map.Entry<Ficha, Integer> entry : mapaFichas.entrySet()) {
                        if (entry.getKey().getLetra().equals(letraBuscada)) {
                            fichaEncontrada = entry.getKey();
                            break;
                        }
                    }

                    if (fichaEncontrada == null); // TODO: no existe en el diccionario y habria que retornar algun tipo de error

                    Ficha f = fichaEncontrada;
                    partida.tablero.setFicha( f, 'A' + x_ini, y_ini + i);
                    if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == dobleTantoDePalabra && modificadorPalabra!= 3) modificadorPalabra = 2;
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == tripleTantoDePalabra) modificadorPalabra = 3;
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == tripleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*3
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == dobleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*2
                    else puntosPorSumar += f.getPuntuacion()

                }
                if (modificadorPalabra != 1) puntosPorSumar *= modificadorPalabra;
                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;
                // TODO MUY IMPORTANTE: si se juntan otras palabras que ya habian puestas,
                // se tiene que sumar esa puntuacion pero SIN CONTAR MODIFICADORES, solo puntuacion de las fichas

            }
        }

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
