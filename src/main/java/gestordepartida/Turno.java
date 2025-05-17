package gestordepartida;

import gestordeperfil.*;
import algorisme.*;
import exceptions.*;

import java.util.Map;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

/**
 * Clase que representa un turno dentro de una partida de Scrabble.
 * Gestiona las acciones realizadas por los jugadores, como colocar palabras, cambiar fichas,
 * pasar turnos, calcular puntos y manejar la lógica de la IA. También administra los atriles
 * de fichas y las interacciones con el tablero y la bolsa de fichas.
 *
 * <p>Incluye métodos para validar movimientos, aplicar modificadores de puntuación,
 * y gestionar el flujo del juego entre jugadores humanos y/o IA.</p>
 *
 * @author Paula Pérez Chia
 */
public class Turno {

    /** Partida actual asociada al turno. */
    private Partida partida;

    /** Perfil del jugador actual (null si es IA). */
    private Perfil jugador;

    /** Mapa de fichas en el atril del jugador creador (clave: Ficha, valor: cantidad). */
    private Map<Ficha,Integer> atrilJ1; // creador

    /** Mapa de fichas en el atril del oponente o IA (clave: Ficha, valor: cantidad). */
    private Map<Ficha,Integer> atrilJ2; // oponente oi IA

    /** Puntuación acumulada del jugador creador. */
    private Integer puntosJ1; // creador

    /** Puntuación acumulada del oponente o IA. */
    private Integer puntosJ2; // oponente oi IA

    /** Número de pistas disponibles que tiene el jugador 1. */
    private Integer pistasRestantesJ1;

    /** Número de pistas disponibles que tiene el jugador 2. */
    private Integer pistasRestantesJ2;

    /** Número máximo de fichas permitidas en un atril. */
    public static final int MAX_FICHAS = 7;

    /** Tipo de jugada realizada en el turno (cambiar, pasar, colocar, finalizar). */
    private TipoJugada tipoJugada;

    /** Palabra que se ha colocado en el tablero en ese turno*/
    private String palabra;

    /** Posición x inicial de la palabra colocada */
    private Integer x_ini;

    /** Posición y inicial de la palabra colocada */
    private Integer y_ini;

    /** Orientación de la palabra colocada */
    private Boolean horizontal;

    /** Fichas cambiadas durante el turno */
    private Map<Ficha, Integer> fichasCambiadas;

    /** Estado del tablero en el turno*/
    private Tablero tableroTurno;

    /**
     * Enumeración que representa los tipos de acciones disponibles en un turno.
     */
    // TODO: añadir finalizarpartida cuando se finalice
    public enum TipoJugada {
        cambiar, pasar, colocar, finalizar
    }

    /**
     * Construye un nuevo Turno inicializando atriles vacíos.
     *
     * @param partida   Partida asociada al turno.
     * @param jugador   Jugador actual.
     * @param puntosJ1  Puntuación inicial del jugador creador.
     * @param puntosJ2  Puntuación inicial del oponente/IA.
     */
    public Turno(Partida partida, Perfil jugador, int puntosJ1, int puntosJ2) {
        this.partida = partida;
        this.jugador = jugador;
        this.puntosJ1 = puntosJ1;
        this.puntosJ2 = puntosJ2;
        pistasRestantesJ1 = 3;
        pistasRestantesJ2 = 3;
        this.atrilJ1 = new HashMap<>();
        this.atrilJ2 = new HashMap<>();
        this.tipoJugada = TipoJugada.pasar;
        this.tableroTurno = partida.getTablero().clonar();

    }

    /**
     * Construye un Turno con atriles específicos.
     *
     * @param partida   Partida asociada al turno.
     * @param jugador   Jugador actual.
     * @param puntosJ1  Puntuación inicial del jugador creador.
     * @param puntosJ2  Puntuación inicial del oponente/IA.
     * @param atrilJ1   Atril del jugador creador.
     * @param atrilJ2   Atril del oponente/IA.
     */
    public Turno(Partida partida, Perfil jugador, int puntosJ1, int puntosJ2, int numPistasJ1, int numPistasJ2, Map<Ficha,Integer> atrilJ1, Map<Ficha,Integer> atrilJ2) {
        this.partida = partida;
        this.jugador = jugador;
        this.puntosJ1 = puntosJ1;
        this.puntosJ2 = puntosJ2;
        pistasRestantesJ1 = numPistasJ1;
        pistasRestantesJ2 = numPistasJ2;
        this.atrilJ1 = atrilJ1;
        this.atrilJ2 = atrilJ2;
        this.atrilJ1 = new HashMap<>(atrilJ1);
        this.atrilJ2 = new HashMap<>(atrilJ2);
        this.tableroTurno = new Tablero(partida.getIdioma());
    }


    public Turno(Partida partida, Perfil jugador, int puntosJ1, int puntosJ2, boolean noClonarTablero) {
        this.partida = partida;
        this.jugador = jugador;
        this.puntosJ1 = puntosJ1;
        this.puntosJ2 = puntosJ2;
        this.atrilJ1 = new HashMap<>();
        this.atrilJ2 = new HashMap<>();
        this.tipoJugada = TipoJugada.pasar;

        if (!noClonarTablero) {
            this.tableroTurno = partida.getTablero().clonar();
        } else {
            this.tableroTurno = new Tablero(partida.getIdioma());
        }
    }


    /**
     * Obtiene el jugador actual del turno.
     *
     * @return El perfil del jugador actual.
     */
    public Perfil getJugador() {
        return jugador;
    }

    /**
     * Obtiene la puntuación del creador
     *
     * @return Puntuación del jugador creador.
     */
    public int getPuntuacionJ1() {
        return puntosJ1;
    }

    /**
     * Obtiene la puntuación del oponente o la IA
     *
     * @return Puntuación del jugador oponente o IA.
     */
    public int getPuntuacionJ2() {
        return puntosJ2;
    }

    /**
     * Obtiene la instancia de la partida a la que pertenece el turno.
     *
     * @return La instancia a la que pertenece el turno
     */
    public Partida getPartida() {
        return partida;
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
     * Obtiene el tipo de jugada realizada en el turno.
     *
     * @return El tipo de jugada (cambiar, pasar, colocar).
     */
    public Tablero getTablero() {
        return partida.getTablero();
    }

    /**
     * Obtiene el número de fichas que hay en el atril.
     *
     * @param atril El atril del jugador.
     * @return El número de fichas que hay.
     */
    public int getTotalFichas(Map<Ficha, Integer> atril) {
        if (atril == null) return 0;
        int total = 0;
        for (Integer cantidad : atril.values()) {
            total += cantidad;
        }
        return total;
    }

    public String getPalabra() {
        return palabra;
    }

    public Integer getX() {
        return x_ini;
    }

    public Integer getY() {
        return y_ini;
    }

    public Boolean getHorizontal() {
        return horizontal;
    }

    public Map<Ficha, Integer> getFichasCambiadas() {
        return fichasCambiadas;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public void setX(int x_ini) {
        this.x_ini = x_ini;
    }

    public void setY (int y_ini) {
        this.y_ini = y_ini;
    }

    public void setHorizontal (boolean horizontal) {
        this.horizontal = horizontal;
    }

    public void setPistasRestantesJ1(int pistasRestantesJ1) {
        this.pistasRestantesJ1 = pistasRestantesJ1;
    }

    public void setPistasRestantesJ2(int pistasRestantesJ2) {
        this.pistasRestantesJ2 = pistasRestantesJ2;
    }

    public void setPuntosJ1(int puntos) {
        this.puntosJ1 = puntos;
    }

    public void setPuntosJ2(int puntos) {
        this.puntosJ2 = puntos;
    }

    // Setters para atriles (si es necesario)
    public void setAtrilJ1(Map<Ficha, Integer> atril) {
        this.atrilJ1 = atril;
    }

    public void setAtrilJ2(Map<Ficha, Integer> atril) {
        this.atrilJ2 = atril;
    }
    public int getPistas(Perfil jugador) {
        return jugador == partida.getCreador() ? pistasRestantesJ1 : pistasRestantesJ2;
    }

    public int getPistasJ1() {
        return pistasRestantesJ1;
    }

    public int getPistasJ2() {
        return pistasRestantesJ2;
    }

    /**
     * Establece el tipo de jugada realizada en el turno.
     *
     * @param tipo El tipo de jugada a establecer.
     */
    public void setTipoJugada(TipoJugada tipo) {
        this.tipoJugada = tipo;
    }

    /**
     * Cambia fichas del atril por nuevas de la bolsa.
     *
     * @param atril              Atril del jugador.
     * @param fichasParaCambiar  Mapa de fichas y cantidades a cambiar.
     */
    public void cambiarFichas(Map<Ficha,Integer> atril, Map<Ficha,Integer> fichasParaCambiar) {
        // Reconstruir el mapa usando las instancias reales del atril
        this.fichasCambiadas = fichasParaCambiar;

        Map<Ficha,Integer> cambioReal = new HashMap<>();
        for (Map.Entry<Ficha,Integer> entry : fichasParaCambiar.entrySet()) {
            String letra = entry.getKey().getLetra();
            int cantidad = entry.getValue();
            // Buscar la ficha en el atril que coincida por letra
            Ficha fichaReal = null;
            for (Ficha fAtril : atril.keySet()) {
                if (fAtril.getLetra().equalsIgnoreCase(letra)) {
                    fichaReal = fAtril;
                    break;
                }
            }
            if (fichaReal != null) {
                cambioReal.put(fichaReal, cambioReal.getOrDefault(fichaReal, 0) + cantidad);
            }
        }

        Queue<Ficha> colaTemporal = new LinkedList<>();
        // Retirar las fichas del atril real
        for (Map.Entry<Ficha, Integer> entry : cambioReal.entrySet()) {
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

        // Robar nuevas fichas
        robarFichas(atril);

        // Devolver las cambiadas a la bolsa y barajar
        partida.getBolsa().addAll(colaTemporal);
        List<Ficha> listaBolsa = new ArrayList<>(partida.getBolsa());
        Collections.shuffle(listaBolsa);
        partida.getBolsa().clear();
        partida.getBolsa().addAll(listaBolsa);

        // Robar de nuevo hasta tener MAX_FICHAS
        robarFichas(atril);

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
        while (getTotalFichas(atril) < MAX_FICHAS && !partida.getBolsa().isEmpty())  {
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
        tableroTurno = partida.getTablero().clonar();
        if (partida.getModoPartida() == Partida.Modo.PvIA) {
            nextJugador = (jugador == partida.getCreador()) ? null : partida.getCreador();
        } else {
            nextJugador = (jugador == partida.getCreador()) ? partida.getOponente() : partida.getCreador();
        }
        partida.nuevoTurno(nextJugador, puntosJ1, puntosJ2, pistasRestantesJ1, pistasRestantesJ2, atrilJ1, atrilJ2);
    }

    public Tablero getTableroTurno() {
        return tableroTurno;
    }


    public void setTableroTurno(Tablero tablero) {
        tableroTurno = tablero;
    }
    /**
     * Retira una ficha específica del atril.
     *
     * @param atril          Atril del jugador.
     * @param letraBuscada   Letra de la ficha a retirar.
     * @return               Ficha retirada, o null si no se encontró.
     */
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

    /**
     * Calcula puntos adicionales por fichas adyacentes en dirección horizontal.
     *
     * @param x_ini    Coordenada X inicial.
     * @param y_ini    Coordenada Y inicial.
     * @param palabra  Palabra colocada.
     * @return         Puntos extra calculados.
     * @throws CoordenadaFueraDeRangoException Si se accede a coordenadas inválidas.
     */
    private int calculoPuntosExtraHorizontal(int x_ini, int y_ini, String palabra) throws CoordenadaFueraDeRangoException {
        int puntosPorSumar = 0;
        // explorar horizontal hacia la izquierda
        for (int x = x_ini - 1; x >= 0; --x) {
            Ficha f = partida.getTablero().getFicha( x, y_ini);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        // explorar horizontal hacia la derecha después de la palabra
        for (int x = x_ini + palabra.length(); x < Tablero.COLUMNAS; x++) {
            Ficha f = partida.getTablero().getFicha(x, y_ini);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        return puntosPorSumar;
    }

    private int calculoPuntosExtraVertical(int x_ini, int y_ini, String palabra) throws CoordenadaFueraDeRangoException {
        int puntosPorSumar = 0;
        // buscar fichas antes de la palabra (hacia arriba)
        for (int y = y_ini - 1; y >= 0; y--) {
            Ficha f = partida.getTablero().getFicha(x_ini, y);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        // buscar fichas después de la palabra (hacia abajo)
        for (int y = y_ini + palabra.length(); y < Tablero.FILAS; y++) {
            Ficha f = partida.getTablero().getFicha(x_ini, y);
            if (f == null) break;
            puntosPorSumar += f.getPuntuacion();
        }
        return puntosPorSumar;
    }

    public Movimiento pedirPista(Perfil jugador) {
       if (jugador.equals(partida.getCreador())) {
           partida.getAlgorithm().preparar(getTablero(), atrilJ1);
           pistasRestantesJ1--;
       } else {
           partida.getAlgorithm().preparar(getTablero(), atrilJ2);
           pistasRestantesJ2--;
       }
       List<Movimiento>  movimientosPosibles = partida.getAlgorithm().generarMovimientos();
       return (movimientosPosibles == null || movimientosPosibles.isEmpty()) ? null : movimientosPosibles.get(0);
    }

    /**
     * Coloca una palabra en el tablero y calcula los puntos.
     *
     * @param palabra      Palabra a colocar.
     * @param x_ini        Coordenada X inicial.
     * @param y_ini        Coordenada Y inicial.
     * @param orientacion  Dirección de colocación ("vertical" u "horizontal").
     * @return             true si la palabra se colocó correctamente, false en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero.
     * @throws CasillaOcupadaException         Si una casilla ya está ocupada.
     */
    public boolean colocarPalabra(String palabra, int x_ini, int y_ini, String orientacion) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        System.out.println("Entro a colocarPalabra " + palabra + " " + x_ini + " " + y_ini + " " + orientacion);

        int puntosPorSumar = 0;
        int modificadorPalabra = 1;
        int puntosVerticalExtra = 0;
        int puntosHorizontalExtra = 0;
        int puntosBasePalabra = 0;

        // pasarle si es el primer turno o no
        boolean esPrimerTurno = false;
        if (getTablero().estaVacio()) esPrimerTurno = true;

        if (partida.dawg.comprobarPalabra(partida.getTablero(), palabra, x_ini , y_ini , orientacion, esPrimerTurno)) {

            List<String> fichas = partida.getDawg().dividirPalabra(palabra);

            String palabraCompleta = partida.getDawg().construirPalabraCompleta(getTablero(), fichas, x_ini, y_ini, orientacion);

            if (!partida.getDawg().existePalabra(palabraCompleta)) {
                System.out.println("Palabra completa no válida: " + palabraCompleta);
                return false;
            }

            if (esPrimerTurno) {
                boolean pasaPorCentro = false;
                if (orientacion.equals("vertical")) {
                    // Para vertical, verificar si alguna letra pasa por (7,7)
                    for (int i = 0; i < fichas.size(); i++) {
                        if ((x_ini + i) == Tablero.FILAS/2 && y_ini == Tablero.COLUMNAS/2) {
                            pasaPorCentro = true;
                            break;
                        }
                    }
                } else { // horizontal
                    // Para horizontal, verificar si alguna letra pasa por (7,7)
                    for (int i = 0; i < fichas.size(); i++) {
                        if (x_ini == Tablero.FILAS/2 && (y_ini + i) == Tablero.COLUMNAS/2) {
                            pasaPorCentro = true;
                            break;
                        }
                    }
                }
                if (!pasaPorCentro) {
                    System.out.println("Falla verificación primer turno - no pasa por centro");
                    return false;
                }
            }

            if ("vertical".equals(orientacion)) {
                System.out.println("Entro a Vertical");

                //mirar si tiene las fichas en el atril
                Map<Ficha, Integer> atrilCheck;
                if (jugador == partida.getCreador()) atrilCheck = new HashMap<>(atrilJ1);
                else atrilCheck = new HashMap<>(atrilJ2);
                for (int i = 0; i < fichas.size(); ++i)
                {
                    if (partida.getTablero().getFicha(x_ini + i, y_ini) == null) {
                        Ficha fichaCheck = quitarFichaDelAtril(atrilCheck, fichas.get(i));
                        if (fichaCheck == null) return false;
                    }
                }
                StringBuilder palabraColocada = new StringBuilder();
                this.x_ini = x_ini;
                this.y_ini = y_ini;
                horizontal = false;
                for (int i = 0; i < fichas.size(); i++) { // por cada ficha
                    String letraBuscada = fichas.get(i);
                    palabraColocada.append(letraBuscada);


                    Ficha f;
                    if (partida.getTablero().getFicha(x_ini + i, y_ini) == null) {  // hay que quitar la ficha del atril, colocarla en el tablero y sumar los puntos como de normal y los modificadores
                        Ficha fichaEncontrada;

                        if (jugador == partida.getCreador()) fichaEncontrada = quitarFichaDelAtril(atrilJ1, letraBuscada);
                        else fichaEncontrada = quitarFichaDelAtril(atrilJ2, letraBuscada);
                        if (fichaEncontrada == null) return false;

                        f = fichaEncontrada;
                        partida.getTablero().setFicha( f, x_ini + i, y_ini);

                        Tablero.TipoModificador mod = partida.getTablero().getTipoModificador(x_ini + i, y_ini);
                        if (mod != null) { // si tiene modificador
                            if (mod == Tablero.TipoModificador.tripleTantoDePalabra) modificadorPalabra = 3;
                            else if (mod == Tablero.TipoModificador.dobleTantoDePalabra && modificadorPalabra != 3) modificadorPalabra = 2;
                            else if (mod == Tablero.TipoModificador.tripleTantoDeLetra) puntosBasePalabra += f.getPuntuacion() * 3;
                            else if (mod == Tablero.TipoModificador.dobleTantoDeLetra) puntosBasePalabra += f.getPuntuacion() * 2;
                            else puntosBasePalabra += f.getPuntuacion();
                        }// si no hay modificador
                        else puntosBasePalabra += f.getPuntuacion();
                    }
                    else { // si esta ya puesta, solo se suman los puntos de la ficha, sin modificadores
                        f = partida.getTablero().getFicha(x_ini + i, y_ini);
                        puntosBasePalabra += f.getPuntuacion();
                    }

                    // independientemente de si esta puesta o no, explorar alrededor para sumar esos puntos, pero estos
                    // no se multiplican por el modificador de palabra
                    // explorar horizontalmente (izquierda)
                    if (partida.getTablero().getFicha(x_ini + i, y_ini - 1) != null || partida.getTablero().getFicha(x_ini + i, y_ini + 1) != null) {
                        int x = x_ini + i - 1;
                        while (x >= 0) {
                            Ficha fIzq = partida.getTablero().getFicha(x, y_ini);
                            if (fIzq == null) break;
                            puntosHorizontalExtra += fIzq.getPuntuacion();
                            x--;
                        }

                        // explorar horizontalmente (derecha)
                        x = x_ini + i + 1;
                        while (x < Tablero.COLUMNAS) {
                            Ficha fDer = partida.getTablero().getFicha(x, y_ini);
                            if (fDer == null) break;
                            puntosHorizontalExtra += fDer.getPuntuacion();
                            x++;
                        }
                    }
                }

                this.palabra = palabraColocada.toString();

                // cuando acabamos de iterar por todas las fichas, que ya las tenemos colocadas, se multiplican los modificadores de palabra
                // y se le suman los puntos totales al jugador que le pertoca.

                // puntos de juntar con palabras ya hechas
                puntosBasePalabra += calculoPuntosExtraVertical(x_ini, y_ini, palabra);
                puntosPorSumar = puntosBasePalabra * modificadorPalabra;
                puntosPorSumar += puntosHorizontalExtra;

                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;
            }
            else { // horizontal
                System.out.println("Entro a horizontal");

                //mirar si tiene las fichas en el atril
                Map<Ficha, Integer> atrilCheck;
                if (jugador == partida.getCreador()) atrilCheck = new HashMap<>(atrilJ1);
                else atrilCheck = new HashMap<>(atrilJ2);
                for (int i = 0; i < fichas.size(); ++i)
                {
                    if (partida.getTablero().getFicha(x_ini, y_ini + i) == null) {
                        Ficha fichaCheck = quitarFichaDelAtril(atrilCheck, fichas.get(i));
                        if (fichaCheck == null) return false;
                    }
                }

                StringBuilder palabraColocada = new StringBuilder();
                this.x_ini = x_ini;
                this.y_ini = y_ini;
                horizontal = false;
                for (int i = 0; i < fichas.size(); i++) {
                    String letraBuscada = fichas.get(i);
                    palabraColocada.append(letraBuscada);
                    Ficha f;
                    if (partida.getTablero().getFicha(x_ini, y_ini + i) == null) {  // hay que quitar la ficha del atril, colocarla en el tablero y sumar los puntos como de normal
                        Ficha fichaEncontrada;
                        if (jugador == partida.getCreador()) fichaEncontrada = quitarFichaDelAtril(atrilJ1, letraBuscada);
                        else fichaEncontrada = quitarFichaDelAtril(atrilJ2, letraBuscada);
                        if (fichaEncontrada == null) {
                            return false;
                        }

                        f = fichaEncontrada;
                        partida.getTablero().setFicha( f, x_ini, y_ini + i);

                        Tablero.TipoModificador mod = partida.getTablero().getTipoModificador(x_ini, y_ini + i);
                        if (mod != null) { // si tiene modificador
                            if (mod == Tablero.TipoModificador.tripleTantoDePalabra) modificadorPalabra = 3;
                            else if (mod == Tablero.TipoModificador.dobleTantoDePalabra && modificadorPalabra != 3) modificadorPalabra = 2;
                            else if (mod == Tablero.TipoModificador.tripleTantoDeLetra) puntosBasePalabra += f.getPuntuacion() * 3;
                            else if (mod == Tablero.TipoModificador.dobleTantoDeLetra) puntosBasePalabra += f.getPuntuacion() * 2;
                            else puntosBasePalabra += f.getPuntuacion();
                        }// si no hay modificador
                        else puntosBasePalabra += f.getPuntuacion();
                    }
                    else { // si esta ya puesta, solo se suman los puntos de la ficha, sin modificadores
                        f = partida.getTablero().getFicha(x_ini, y_ini + i);
                        puntosBasePalabra += f.getPuntuacion();
                    }

                    // independientemente de si esta puesta o no, explorar alrededor para sumar esos puntos, pero estos
                    // no se multiplican por el modificador de palabra
                    // Explorar verticalmente (arriba)
                    if(x_ini - 1 >= 0 && x_ini + 1 < Tablero.FILAS) {
                        if (partida.getTablero().getFicha(x_ini - 1, y_ini + i) != null || partida.getTablero().getFicha(x_ini + 1, y_ini + i) != null) {
                            int y = y_ini + i - 1;
                            while (y >= 0) {
                                Ficha fArriba = partida.getTablero().getFicha(x_ini, y);
                                if (fArriba == null) break;
                                puntosVerticalExtra += fArriba.getPuntuacion();
                                y--;
                            }

                            // Explorar verticalmente (abajo)
                            y = y_ini + i + 1;
                            while (y > 0 && y < Tablero.FILAS) {
                                Ficha fAbajo = partida.getTablero().getFicha(x_ini, y);
                                if (fAbajo == null) break;
                                puntosVerticalExtra += fAbajo.getPuntuacion();
                                y++;
                            }
                        }
                    }

                }

                this.palabra = palabraColocada.toString();

                // cuando acabamos de iterar por todas las fichas, que ya las tenemos colocadas, se multiplican los modificadores de palabra
                // y se le suman los puntos totales al jugador que le pertoca.

                // puntos de juntar con palabras ya hechas
                puntosBasePalabra += calculoPuntosExtraHorizontal(x_ini, y_ini, palabra);
                puntosPorSumar = puntosBasePalabra * modificadorPalabra;  // **Cambio aquí: calculamos puntosPorSumar al final**
                puntosPorSumar += puntosVerticalExtra;

                if (jugador == partida.getCreador()) puntosJ1 += puntosPorSumar;
                else puntosJ2 += puntosPorSumar;

            }
            System.out.println("Salgo de H/V");

        }
        else {
            System.out.println("PETO");
            // compruebo la palabra y esta mal
            return false;
        }

        setTipoJugada(TipoJugada.colocar);
        if (jugador == partida.getCreador()) robarFichas(atrilJ1);
        else robarFichas(atrilJ2);
        System.out.println("Voy a avanzar turno");
        avanzarTurno();
        return true;
    }

    /**
     * Inicializa los atriles de ambos jugadores con fichas de la bolsa.
     */
    public void inicializarAtriles() {
        while (getTotalFichas(atrilJ1) < MAX_FICHAS) {
            Ficha ficha = partida.getBolsa().poll();
            atrilJ1.put(ficha, atrilJ1.getOrDefault(ficha, 0) + 1);
        }

        while (getTotalFichas(atrilJ2) < MAX_FICHAS) {
            Ficha ficha = partida.getBolsa().poll();
            atrilJ2.put(ficha, atrilJ2.getOrDefault(ficha, 0) + 1);
        }
    }

    /**
     * Ejecuta la lógica de la IA para seleccionar y colocar la mejor palabra posible.
     *
     * @throws CoordenadaFueraDeRangoException Si las coordenadas generadas son inválidas.
     * @throws CasillaOcupadaException         Si se intenta colocar en una casilla ocupada.
     */
    public void jugarIA(int dificultad) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        int nroFichas = getTotalFichas(atrilJ2);
        String[] atril = new String[nroFichas];
        int index = 0;
        for (Map.Entry<Ficha, Integer> entry : atrilJ2.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                atril[index++] = entry.getKey().getLetra();
            }
        }

        // nueva implementacion:
        partida.getAlgorithm().preparar(getTablero(), atrilJ2);
        List<Movimiento> movimientosValidos = partida.getAlgorithm().generarMovimientos();

        // si la IA no ha encontrado posibles movimientos
        if (movimientosValidos == null || movimientosValidos.isEmpty()) {
            if (partida.getBolsa() == null) pasarTurno();
            else {
                Map<Ficha,Integer> fichasPorCambiar = new HashMap<>();
                // cambio las consonantes
                for (Map.Entry<Ficha, Integer> entry : atrilJ2.entrySet()) {
                    Ficha ficha = entry.getKey();
                    int cantidad = entry.getValue();
                    String letra = ficha.getLetra();
                    if (!"AEIOU".contains(letra)) fichasPorCambiar.put(ficha, cantidad);
                }
                cambiarFichas(atrilJ2, fichasPorCambiar);
            }
        }

        // si la IA ha encoentrado movimientos
        else {
            Movimiento m = getMejorMovimiento(movimientosValidos, dificultad);

            List<String> trozosPalabra = m.getPalabra();
            StringBuilder palabra = new StringBuilder();
            for (String trozo : trozosPalabra) palabra.append(trozo);

            String orientacion;
            if (m.isVertical()) orientacion = "vertical";
            else orientacion = "horizontal";
            colocarPalabra(palabra.toString(), m.getFila(), m.getColumna(), orientacion);
        }
    }

    private Movimiento getMejorMovimiento(List<Movimiento> movimientos, int dificultad) {
        Map<String, Ficha> fichasAlfabeto = partida.getMapaLetras();
        Tablero tablero = partida.getTablero();

        Movimiento best = null;

        // Recorrer toda la lista y obtener el mejor movimiento en funcion de la dificultad en la que se esté jugando y las posiciones del tablero
        // Ver si al colocar la palabra en el tablero, su puntuación es mayor que mejorPuntuacion
        // por cada movimiento, bucle de las letras del movimeinto, viendo las puntuaciones de la colocacion de las palabras en el tablero

        switch (dificultad) {
            // Fácil
            case 1 :
                int mejorPuntuacionFacil = 1000000;
                Movimiento mejorMovimientoFacil = null;

                for(Movimiento mov : movimientos) {
                    List<String> letras = mov.getPalabra();
                    int idx = 0;
                    int puntuacionPalabra = 0;
                    boolean dobleTantoPalabra = false;
                    boolean tripleTantoPalabra = false;
                    // si el movimiento es vertical
                    if(mov.isVertical()) {
                        int y = mov.getColumna();

                        for(int x = mov.getFila(); idx < letras.size(); x++) {
                            int puntuacionLetra;
                            try {
                                if(tablero.getTipoModificador(x, y) != null) {
                                    switch (tablero.getTipoModificador(x, y)) {
                                        case dobleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += 2 * puntuacionLetra;
                                            ++idx;
                                            break;

                                        case tripleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += 3 * puntuacionLetra;
                                            ++idx;
                                            break;

                                        case dobleTantoDePalabra:
                                            dobleTantoPalabra = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += puntuacionLetra;
                                            ++idx;
                                            break;

                                        case tripleTantoDePalabra:
                                            tripleTantoPalabra = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += puntuacionLetra;
                                            ++idx;
                                            break;
                                    }
                                }

                                else {
                                    puntuacionPalabra += fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                    idx++;
                                }
                            }
                            catch (CoordenadaFueraDeRangoException e){
                                // No debería passar
                            }
                        }

                        if(dobleTantoPalabra && tripleTantoPalabra) puntuacionPalabra *= 6;
                        else if(dobleTantoPalabra) puntuacionPalabra *= 2;
                        else if(tripleTantoPalabra) puntuacionPalabra *= 3;

                        if(puntuacionPalabra < mejorPuntuacionFacil) {
                            mejorPuntuacionFacil = puntuacionPalabra;
                            mejorMovimientoFacil = mov;
                        }
                    }

                    // si el movimiento es horizontal
                    else {
                        int x = mov.getFila();

                        for(int y = mov.getColumna(); idx < letras.size(); y++) {
                            int puntuacionLetra;
                            try {
                                if(tablero.getTipoModificador(x, y) != null) {
                                    switch (tablero.getTipoModificador(x, y)) {
                                        case dobleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += 2 * puntuacionLetra;
                                            ++idx;
                                            break;

                                        case tripleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += 3 * puntuacionLetra;
                                            ++idx;
                                            break;

                                        case dobleTantoDePalabra:
                                            dobleTantoPalabra = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += puntuacionLetra;
                                            ++idx;
                                            break;

                                        case tripleTantoDePalabra:
                                            tripleTantoPalabra = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                            puntuacionPalabra += puntuacionLetra;
                                            ++idx;
                                            break;
                                    }
                                }

                                else {
                                    puntuacionPalabra += fichasAlfabeto.get(letras.get(idx)).getPuntuacion();
                                    idx++;
                                }
                            }
                            catch (CoordenadaFueraDeRangoException e){
                                // No debería passar
                            }
                        }

                        if(dobleTantoPalabra && tripleTantoPalabra) puntuacionPalabra *= 6;
                        else if(dobleTantoPalabra) puntuacionPalabra *= 2;
                        else if(tripleTantoPalabra) puntuacionPalabra *= 3;

                        if(puntuacionPalabra < mejorPuntuacionFacil) {
                            mejorPuntuacionFacil = puntuacionPalabra;
                            mejorMovimientoFacil = mov;
                        }
                    }
                }

                best = mejorMovimientoFacil;
                break;

            // Medio
            case 2:
                Random rand = new Random();
                int size = movimientos.size();
                int idx_random = rand.nextInt(size);

                best = movimientos.get(idx_random);
                break;


            // Difícil
            case 3:
                int mejorPuntuacionDificil = 0;
                Movimiento mejorMovimientoDificil = null;

                for(Movimiento mov2 : movimientos) {
                    List<String> letras2 = mov2.getPalabra();
                    int idx2 = 0;
                    int puntuacionPalabra2 = 0;
                    boolean dobleTantoPalabra2 = false;
                    boolean tripleTantoPalabra2 = false;
                    // si el movimiento es vertical
                    if(mov2.isVertical()) {
                        int y = mov2.getColumna();

                        for(int x = mov2.getFila(); idx2 < letras2.size(); x++) {
                            int puntuacionLetra;
                            try {
                                if(tablero.getTipoModificador(x, y) != null) {
                                    switch (tablero.getTipoModificador(x, y)) {
                                        case dobleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += 2 * puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case tripleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += 3 * puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case dobleTantoDePalabra:
                                            dobleTantoPalabra2 = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case tripleTantoDePalabra:
                                            tripleTantoPalabra2 = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += puntuacionLetra;
                                            ++idx2;
                                            break;
                                    }
                                }

                                else {
                                    puntuacionPalabra2 += fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                    idx2++;
                                }
                            }
                            catch (CoordenadaFueraDeRangoException e){
                                // No debería passar
                            }
                        }

                        if(dobleTantoPalabra2 && tripleTantoPalabra2) puntuacionPalabra2 *= 6;
                        else if(dobleTantoPalabra2) puntuacionPalabra2 *= 2;
                        else if(tripleTantoPalabra2) puntuacionPalabra2 *= 3;

                        if(puntuacionPalabra2 > mejorPuntuacionDificil) {
                            mejorPuntuacionDificil = puntuacionPalabra2;
                            mejorMovimientoDificil = mov2;
                        }
                    }

                    // si el movimiento es horizontal
                    else {
                        int x = mov2.getFila();

                        for(int y = mov2.getColumna(); idx2 < letras2.size(); y++) {
                            int puntuacionLetra;
                            try {
                                if(tablero.getTipoModificador(x, y) != null) {
                                    switch (tablero.getTipoModificador(x, y)) {
                                        case dobleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += 2 * puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case tripleTantoDeLetra:
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += 3 * puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case dobleTantoDePalabra:
                                            dobleTantoPalabra2 = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += puntuacionLetra;
                                            ++idx2;
                                            break;

                                        case tripleTantoDePalabra:
                                            tripleTantoPalabra2 = true;
                                            puntuacionLetra = fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                            puntuacionPalabra2 += puntuacionLetra;
                                            ++idx2;
                                            break;
                                    }
                                }

                                else {
                                    puntuacionPalabra2 += fichasAlfabeto.get(letras2.get(idx2)).getPuntuacion();
                                    idx2++;
                                }
                            }
                            catch (CoordenadaFueraDeRangoException e){
                                // No debería passar
                            }
                        }

                        if(dobleTantoPalabra2 && tripleTantoPalabra2) puntuacionPalabra2 *= 6;
                        else if(dobleTantoPalabra2) puntuacionPalabra2 *= 2;
                        else if(tripleTantoPalabra2) puntuacionPalabra2 *= 3;

                        if(puntuacionPalabra2 > mejorPuntuacionDificil) {
                            mejorPuntuacionDificil = puntuacionPalabra2;
                            mejorMovimientoDificil = mov2;
                        }
                    }
                }

                best = mejorMovimientoDificil;
                break;

        }
        return best;
    }
}
