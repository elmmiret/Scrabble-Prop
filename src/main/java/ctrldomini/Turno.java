package ctrldomini;

import algorisme.*;
import exceptions.*;
import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;


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
    // TODO: mirar si es mas facil hacerr que el atril sea un set de strings solo
    private Map<Ficha,Integer> atrilJ1; // creador
    private Map<Ficha,Integer> atrilJ2; // oponente oi IA
    private Integer puntosJ1; // creador
    private Integer puntosJ2; // oponente oi IA
    public static final int MAX_FICHAS = 7;

    // TODO: añadir finalizarpartida cuando se finalice
    public enum TipoJugada {
        cambiar, pasar, colocar, finalizar
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

    public int getPuntuacionJ1() {
        return puntosJ1;
    }

    public int getPuntuacionJ2() {
        return puntosJ2;
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
        this.tipoJugada = tipoJugada;
    }

    /**
     * Cambia un conjunto de fichas del atril por otras nuevas de la bolsa.
     * Primero se apartan, luego se roba, y por último se vuelven a insertar en la bolsa.
     *
     * @param atril El atril del jugador.
     * @param fichasParaCambiar Un mapa de fichas y la cantidad que desea cambiar.
     */
    public void cambiarFichas(Map<Ficha,Integer> atril, Map<Ficha,Integer> fichasParaCambiar) {
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
        // Barajar la bolsa (convertir a lista, mezclar y volver a meter en la cola)
        List<Ficha> listaBolsa = new ArrayList<>(partida.getBolsa());
        Collections.shuffle(listaBolsa);
        partida.getBolsa().clear();
        partida.getBolsa().addAll(listaBolsa);

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
        if (jugador == partida.getCreador()) nextJugador = partida.getOponente();
        else nextJugador = partida.getCreador();
        partida.nuevoTurno(nextJugador); // revisar
    }

    private Ficha quitarFichaDelAtril(Map<Ficha, Integer> atril, String letraBuscada) {
        for (Map.Entry<Ficha, Integer> entry : atril.entrySet()) {
            if (entry.getKey().getLetra().equals(letraBuscada)) {
                Ficha fichaEncontrada = entry.getKey();
                int cantidad = entry.getValue();
                if (cantidad > 1) atril.put(fichaEncontrada, cantidad - 1);
                else atril.remove(fichaEncontrada);
                return fichaEncontrada;
            }
        }
        return null;
    }

    private int calculoPuntosExtraHorizontal(int x_ini, int y_ini, String palabra) throws CoordenadaFueraDeRangoException {
        int puntosPorSumar = 0;
        // explorar horizontal hacia la izquierda
        for (int x = x_ini - 1; x >= 0; --x) {
            Ficha f = partida.getTablero().getFicha((char)('A' + x), y_ini);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        // explorar horizontal hacia la derecha después de la palabra
        for (int x = x_ini + palabra.length(); x < Tablero.COLUMNAS; x++) {
            Ficha f = partida.getTablero().getFicha((char)('A' + x), y_ini);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        return puntosPorSumar;
    }

    private int calculoPuntosExtraVertical(int x_ini, int y_ini, String palabra) throws CoordenadaFueraDeRangoException {
        int puntosPorSumar = 0;
        // buscar fichas adyacentes antes (hacia arriba)
        for (int y = y_ini - 1; y >= 0; y--) {
            Ficha f = partida.getTablero().getFicha((char)('A' + x_ini), y);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        // buscar fichas adyacentes después (hacia abajo)
        for (int y = y_ini + palabra.length(); y < Tablero.FILAS; y++) {
            Ficha f = partida.getTablero().getFicha((char)('A' + x_ini), y);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        return puntosPorSumar;
    }

    /**
     * Coloca una palabra en el tablero, calcula y asigna los puntos correspondientes.
     *
     * @param palabra La palabra que se quiere colocar.
     * @param x_ini La coordenada X inicial en el tablero.
     * @param y_ini La coordenada Y inicial en el tablero.
     * @param orientacion La dirección en la que se colocará la palabra ("vertical" u "horizontal").
     * @return true si se ha colocado correctamente la palabra o false si no.
     */
    public boolean colocarPalabra(String palabra, int x_ini, int y_ini, String orientacion) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        // existe la palabra, cabe en el tablero y coincide bien con todas las otras fichas
        int puntosPorSumar = 0;
        int modificadorPalabra = 1;
        int puntosVerticalExtra = 0;
        int puntosHorizontalExtra = 0;
        if (partida.dawg.comprobarPalabra(partida.getTablero(), palabra, x_ini , y_ini , orientacion)) {
            List<String> fichas = partida.getDawg().dividirPalabra(palabra);
            if (orientacion == "vertical") {
                for (int i = 0; i < fichas.size(); ++i) {
                    String letraBuscada = fichas.get(i);
                    Ficha fichaEncontrada;
                    if (jugador == partida.getCreador()) fichaEncontrada = quitarFichaDelAtril(atrilJ1, letraBuscada);
                    else fichaEncontrada = quitarFichaDelAtril(atrilJ2, letraBuscada);
                    if (fichaEncontrada == null) return false;

                    Ficha f = fichaEncontrada;
                    partida.getTablero().setFicha( f, (char) ('A' + x_ini + i), y_ini);
                    if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == Tablero.TipoModificador.dobleTantoDePalabra && modificadorPalabra!= 3) modificadorPalabra = 2;
                    else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == Tablero.TipoModificador.tripleTantoDePalabra) modificadorPalabra = 3;
                    else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == Tablero.TipoModificador.tripleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*3;
                    else if (partida.getTablero().getTipoModificador(x_ini + i, y_ini) == Tablero.TipoModificador.dobleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*2;
                    else puntosPorSumar += f.getPuntuacion();

                    // buscar fichas a la izquierda
                    for (int j = (char)('A' + x_ini) - 1; j >= 'A'; j--) {
                        Ficha fIzq = partida.getTablero().getFicha((char)j, y_ini + j);
                        if (fIzq == null) break;
                        puntosPorSumar += fIzq.getPuntuacion();
                    }

                    // buscar fichas a la derecha
                    for (int j = (char)('A' + x_ini) + 1; j < 'A' +Tablero.COLUMNAS; j++) {
                        Ficha fDer = partida.getTablero().getFicha((char)j, y_ini + j);
                        if (fDer == null) break;
                        puntosPorSumar += fDer.getPuntuacion();
                    }
                }

                // puntos de juntar con palabras ya hechas
                puntosPorSumar += calculoPuntosExtraVertical(x_ini, y_ini, palabra);
                if (modificadorPalabra != 1) puntosPorSumar *= modificadorPalabra;
                puntosPorSumar += puntosHorizontalExtra;

                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;
            }
            else {
                for (int i = 0; i < fichas.size(); ++i) {
                    String letraBuscada = fichas.get(i);
                    Ficha fichaEncontrada;
                    if (jugador == partida.getCreador()) fichaEncontrada = quitarFichaDelAtril(atrilJ1, letraBuscada);
                    else fichaEncontrada = quitarFichaDelAtril(atrilJ2, letraBuscada);
                    if (fichaEncontrada == null) return false;

                    Ficha f = fichaEncontrada;
                    partida.getTablero().setFicha( f, (char) ('A' + x_ini), y_ini + i);
                    if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == Tablero.TipoModificador.dobleTantoDePalabra && modificadorPalabra!= 3) modificadorPalabra = 2;
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == Tablero.TipoModificador.tripleTantoDePalabra) modificadorPalabra = 3;
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == Tablero.TipoModificador.tripleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*3;
                    else if (partida.getTablero().getTipoModificador(x_ini, y_ini + i) == Tablero.TipoModificador.dobleTantoDeLetra) puntosPorSumar += f.getPuntuacion()*2;
                    else puntosPorSumar += f.getPuntuacion();

                    // arriba
                    for (int y = y_ini - 1; y >= 0; y--) {
                        Ficha fArriba = partida.getTablero().getFicha((char)('A' + x_ini + i), y);
                        if (fArriba == null) break;
                        puntosVerticalExtra += fArriba.getPuntuacion();
                    }
                    // abajo
                    for (int y = y_ini + 1; y < partida.getTablero().FILAS; y++) {
                        Ficha fAbajo = partida.getTablero().getFicha((char)('A' + x_ini + i), y);
                        if (fAbajo == null) break;
                        puntosVerticalExtra += fAbajo.getPuntuacion();
                    }

                }
                if (modificadorPalabra != 1) puntosPorSumar *= modificadorPalabra;
                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;

                // puntos de juntar con palabras ya hechas
                puntosPorSumar += calculoPuntosExtraHorizontal(x_ini, y_ini, palabra);
                if (modificadorPalabra != 1) puntosPorSumar *= modificadorPalabra;
                puntosPorSumar += puntosVerticalExtra;

                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;
            }
        }

        setTipoJugada(TipoJugada.colocar);
        if (jugador == partida.getCreador()) robarFichas(atrilJ1);
        else robarFichas(atrilJ2);
        avanzarTurno();
        return true;
    }

    // la necesitare?
    /**
     * Retira una ficha del tablero.
     *
     * @param x La coordenada X de la ficha a retirar.
     * @param y La coordenada Y de la ficha a retirar.
     */
    public void retirarFicha(int x, int y) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        if (partida.getTablero().getFicha(x, y) == null);
        else partida.getTablero().setFicha(null, (char) ('A'+x), y+1);
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

    public void jugarIA() {
        // TODO:
    }

}
