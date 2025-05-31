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

    /**
     * Construye un nuevo Turno inicializando atriles vacíos y con la opción de clonar el tablero.
     *
     * @param partida          Partida asociada al turno.
     * @param jugador          Jugador actual.
     * @param puntosJ1         Puntuación inicial del jugador creador.
     * @param puntosJ2         Puntuación inicial del oponente/IA.
     * @param noClonarTablero  Si es true, inicializa un tablero nuevo en lugar de clonar.
     */
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
     * Obtiene el tablero actual de la partida.
     *
     * @return El tablero actual de la partida.
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

    /**
     * Obtiene la palabra colocada en el turno.
     *
     * @return  Palabra colocada, o null si no se ha colocado ninguna.
     */
    public String getPalabra() {
        return palabra;
    }

    /**
     * Obtiene la coordenada X inicial de la palabra colocada.
     *
     * @return Valor entero representando la posición horizontal inicial en el tablero.
     */
    public Integer getX() {
        return x_ini;
    }

    /**
     * Obtiene la coordenada Y inicial de la palabra colocada.
     *
     * @return Valor entero representando la posición vertical inicial en el tablero.
     */
    public Integer getY() {
        return y_ini;
    }

    /**
     * Obtiene el número de pistas disponibles para un jugador específico.
     *
     * @param jugador Perfil del jugador (creador u oponente).
     * @return Número de pistas restantes para el jugador especificado.
     */
    public int getPistas(Perfil jugador) {
        return jugador == partida.getCreador() ? pistasRestantesJ1 : pistasRestantesJ2;
    }

    /**
     * Obtiene las pistas restantes del jugador creador.
     *
     * @return Número de pistas disponibles para el jugador 1.
     */
    public int getPistasJ1() {
        return pistasRestantesJ1;
    }

    /**
     * Obtiene las pistas restantes del jugador oponente/IA.
     *
     * @return Número de pistas disponibles para el jugador 2.
     */
    public int getPistasJ2() {
        return pistasRestantesJ2;
    }

    /**
     * Indica la orientación de la palabra colocada.
     *
     * @return {@code true} si la palabra es horizontal, {@code false} si es vertical.
     */
    public Boolean getHorizontal() {
        return horizontal;
    }

    /**
     * Obtiene las fichas cambiadas durante el turno.
     *
     * @return Mapa con las fichas y sus cantidades cambiadas, o {@code null} si no hubo cambio.
     */
    public Map<Ficha, Integer> getFichasCambiadas() {
        return fichasCambiadas;
    }

    /**
     * Obtiene el estado del tablero asociado específicamente a este turno.
     * Este es un clon del tablero principal al inicio del turno, permitiendo
     * comparaciones o reversiones.
     *
     * @return Instancia de {@link Tablero} correspondiente al estado inicial del turno.
     *         Puede ser {@code null} si el turno no ha modificado el tablero.
     */
    public Tablero getTableroTurno() {
        return tableroTurno;
    }

    /**
     * Establece la palabra a colocar en el tablero.
     *
     * @param palabra Palabra en formato String (ej. "SCRAbbLE").
     */
    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    /**
     * Define la coordenada X inicial de la palabra colocada en el tablero.
     *
     * @param x_ini Posición horizontal en el tablero (0-14).
     */
    public void setX(int x_ini) {
        this.x_ini = x_ini;
    }

    /**
     * Define la coordenada Y inicial de la palabra colocada en el tablero.
     *
     * @param y_ini Posición vertical en el tablero (0-14).
     */
    public void setY (int y_ini) {
        this.y_ini = y_ini;
    }

    /**
     * Establece la orientación de la palabra a colocar.
     *
     * @param horizontal {@code true} para orientación horizontal, {@code false} para vertical.
     */
    public void setHorizontal (boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Actualiza el contador de pistas restantes del jugador creador.
     *
     * @param pistasRestantesJ1 Nuevo número de pistas disponibles para el jugador 1.
     */
    public void setPistasRestantesJ1(int pistasRestantesJ1) {
        this.pistasRestantesJ1 = pistasRestantesJ1;
    }

    /**
     * Actualiza el contador de pistas restantes del jugador oponente/IA.
     *
     * @param pistasRestantesJ2 Nuevo número de pistas disponibles para el jugador 2.
     */
    public void setPistasRestantesJ2(int pistasRestantesJ2) {
        this.pistasRestantesJ2 = pistasRestantesJ2;
    }

    /**
     * Modifica la puntuación acumulada del jugador creador.
     *
     * @param puntos Nuevo valor de puntos a asignar.
     */
    public void setPuntosJ1(int puntos) {
        this.puntosJ1 = puntos;
    }

    /**
     * Modifica la puntuación acumulada del jugador oponente/IA.
     *
     * @param puntos Nuevo valor de puntos a asignar.
     */
    public void setPuntosJ2(int puntos) {
        this.puntosJ2 = puntos;
    }

    /**
     * Reemplaza completamente el atril del jugador creador.
     *
     * @param atril Nuevo conjunto de fichas en formato Mapa&lt;Ficha, Cantidad&gt;.
     */
    public void setAtrilJ1(Map<Ficha, Integer> atril) {
        this.atrilJ1 = atril;
    }

    /**
     * Reemplaza completamente el atril del jugador oponente/IA.
     *
     * @param atril Nuevo conjunto de fichas en formato Mapa&lt;Ficha, Cantidad&gt;.
     */
    public void setAtrilJ2(Map<Ficha, Integer> atril) {
        this.atrilJ2 = atril;
    }

    /**
     * Registra el tipo de acción realizada en el turno.
     *
     * @param tipo Valor de la enumeración {@link TipoJugada} que representa la acción.
     */
    public void setTipoJugada(TipoJugada tipo) {
        this.tipoJugada = tipo;
    }

    /**
     * Establece manualmente el estado del tablero para este turno.
     * Útil para escenarios avanzados como carga de partidas guardadas
     * o reinicios de jugada.
     *
     * @param tablero Nueva instancia de Tablero que representa el estado deseado.
     *               Si es null, se borrará la referencia al tablero del turno.
     */
    public void setTableroTurno(Tablero tablero) {
        tableroTurno = tablero;
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

    /**
     * Calcula los puntos adicionales generados por fichas adyacentes en dirección vertical a una palabra colocada.
     * <p>
     * Recorre las casillas contiguas arriba y abajo de la palabra vertical, sumando los puntos de las fichas existentes
     * hasta encontrar una casilla vacía o el borde del tablero.
     * </p>
     *
     * @param x_ini    Coordenada X (fila) de la primera letra de la palabra.
     * @param y_ini    Coordenada Y (columna) de la primera letra de la palabra.
     * @param palabra  Palabra colocada en vertical.
     * @return         Puntos extra acumulados por fichas adyacentes en vertical.
     * @throws CoordenadaFueraDeRangoException Si se intenta acceder a una posición fuera del rango del tablero.
     */
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

    /**
     * Genera una sugerencia de movimiento usando el algoritmo de IA y consume una pista del jugador.
     * <p>
     * Prepara el algoritmo con el estado actual del tablero y el atril del jugador, genera movimientos válidos,
     * y devuelve el primer movimiento recomendado. Reduce el contador de pistas disponibles del jugador.
     * </p>
     *
     * @param jugador  Perfil del jugador que solicita la pista (determina qué atril y contador de pistas usar).
     * @return         Mejor movimiento sugerido según el algoritmo, o null si no hay movimientos válidos.
     */
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
            System.out.println("Size de fichas: "+fichas.size());
            System.out.println(partida.getDawg().dividirPalabra(palabra));
            if (esPrimerTurno)
            {
                if (orientacion.equals("vertical"))
                {
                    if (x_ini > 7 || y_ini != 7 || (x_ini + fichas.size()) < 8) return false;

                }
                else if (orientacion.equals("horizontal"))
                {
                    if (x_ini != 7 || y_ini > 7 || (y_ini + fichas.size()) < 8) return false;
                }
            }

            //mirar si tiene las fichas en el atril
            // se crea un atril auxiliar para que no quite palabras en caso de que no las tenga todas
            Map<Ficha, Integer> atrilCheck;
            if (jugador == partida.getCreador()) atrilCheck = new HashMap<>(atrilJ1);
            else atrilCheck = new HashMap<>(atrilJ2);

            // hacer un contador de comodines
            int comodines = 0;
            for (Ficha ficha : atrilCheck.keySet()) {
                if (ficha.getLetra().equals("#")) {
                    ++comodines;
                }
            }
            System.out.println("HOLA1");

            if ("vertical".equals(orientacion)) {
                System.out.println("Entro a Vertical");

                List<String> division = partida.getDawg().dividirPalabra(palabra);
                int size = division.size();
                int pos_division = 0;
                for(int fil = x_ini; fil < x_ini + size; fil++) {
                    if (partida.getTablero().getFicha(fil,y_ini) != null ) { // Hay alguna ficha ya
                        if (!partida.getTablero().getFicha(fil,y_ini).getLetra().equals(division.get(pos_division))) { // si no es la que quiero poner, return false pq no funciona
                            return false;
                        }
                    }
                    else { // No hay ninguna ficha: miro si la tengo en el atril, si esta, paso a la siguiente, si no, uso un comodin
                        Ficha fichaCheck = quitarFichaDelAtril(atrilCheck, fichas.get(pos_division));
                        // si esto es null, es que no tienes esa ficha en el atril, por lo tanto, si hay un comodin libre, lo usas, si no, no la puedes formar
                        if (fichaCheck == null) {
                            // habria que mirar si ya esta puesta antes de restar comodines o devolver false
                            if (comodines == 0) return false;
                            else {
                                --comodines;
                                // NUEVA: cambiar un comodin del tablero a esa letra
                                // la letra la cambiamos y el numero no, pq sigue siendo cero
                                for (Ficha ficha : atrilCheck.keySet()) {
                                    if (ficha.getLetra().equals("#")) {
                                        ficha.setLetra(fichas.get(pos_division));
                                    }
                                }
                            }
                        }
                    }
                    ++pos_division;
                }
                System.out.println("Tengo las fichas en el atril");

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
                        if (fichaEncontrada == null) {
                            System.out.println("No he podido quitar la ficha " + letraBuscada + " del atril");
                            return false;
                        }

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
                System.out.println("HOLA2");

                this.palabra = palabraColocada.toString();

                // cuando acabamos de iterar por todas las fichas, que ya las tenemos colocadas, se multiplican los modificadores de palabra
                // y se le suman los puntos totales al jugador que le pertoca.

                // puntos de juntar con palabras ya hechas
                puntosBasePalabra += calculoPuntosExtraVertical(x_ini, y_ini, palabra);
                // refact. al final
            }
            else { // horizontal
                System.out.println("Entro a horizontal");
                System.out.println("HOLA3");

                // IMPLEMENTACION QUE FALTABA
                List<String> division = partida.getDawg().dividirPalabra(palabra);
                int size = division.size();
                int pos_division = 0;
                for(int col = y_ini; col < y_ini + size; col++) {
                    // Hay alguna ficha ya
                    if(partida.getTablero().getFicha(x_ini,col) != null) {
                        if (!partida.getTablero().getFicha(x_ini,col).getLetra().equals(division.get(pos_division))) { // si no es la que quiero poner, return false pq no funciona
                            // hace falta esto? creo que ya se hace en comprobar palabra
                            return false;
                        }
                    }
                    // No hay ninguna ficha: miro si la tengo en el atril, si esta, paso a la siguiente, si no, uso un comodin
                    else {
                        Ficha fichaCheck = quitarFichaDelAtril(atrilCheck, fichas.get(pos_division));
                        // si esto es null, es que no tienes esa ficha en el atril, por lo tanto, si hay un comodin libre, lo usas, si no, no la puedes formar
                        if (fichaCheck == null) {
                            // habria que mirar si ya esta puesta antes de restar comodines o devolver false
                            if (comodines == 0) return false;
                            else {
                                --comodines;
                                // NUEVA: cambiar un comodin del tablero a esa letra
                                // la letra la cambiamos y el numero no, pq sigue siendo cero
                                for (Ficha ficha : atrilCheck.keySet()) {
                                    if (ficha.getLetra().equals("#")) {
                                        ficha.setLetra(fichas.get(pos_division));
                                    }
                                }
                            }
                        }
                    }
                    ++pos_division;
                }
                // fin de la nueva implementacion
                System.out.println("Tengo las fichas en el atril");

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
                            System.out.println("No he podido quitar la ficha " + letraBuscada + " del atril");
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
                        // hay que añadir que no te hacen falta los comodines entonces
                        f = partida.getTablero().getFicha(x_ini, y_ini + i);
                        puntosBasePalabra += f.getPuntuacion();
                    }

                    System.out.println("HOLA4");

                    // independientemente de si esta puesta o no, explorar alrededor para sumar esos puntos, pero estos
                    // no se multiplican por el modificador de palabra
                    // Explorar verticalmente (arriba)
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
                        while (y < Tablero.FILAS) {
                            Ficha fAbajo = partida.getTablero().getFicha(x_ini, y);
                            if (fAbajo == null) break;
                            puntosVerticalExtra += fAbajo.getPuntuacion();
                            y++;
                        }
                    }
                    System.out.println("HOLA5");

                }

                this.palabra = palabraColocada.toString();

                // cuando acabamos de iterar por todas las fichas, que ya las tenemos colocadas, se multiplican los modificadores de palabra
                // y se le suman los puntos totales al jugador que le pertoca.

                // puntos de juntar con palabras ya hechas
                puntosBasePalabra += calculoPuntosExtraHorizontal(x_ini, y_ini, palabra);
            }
            System.out.println("Salgo de H/V");

        }
        else {
            System.out.println("PETO");
            // compruebo la palabra y esta mal
            return false;
        }

        puntosPorSumar = puntosBasePalabra * modificadorPalabra;
        puntosPorSumar += puntosHorizontalExtra;

        // si se colocan TODAS las fichas del atril +50 pts
        if (jugador == partida.getCreador()) {
            if (atrilJ1.isEmpty()) puntosPorSumar += 50;
            puntosJ1 += puntosPorSumar;
        }
        else {
            if (atrilJ2.isEmpty()) puntosPorSumar += 50;
            puntosJ2 += puntosPorSumar;
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
            if (m == null) {
                Map<Ficha,Integer> fichasPorCambiar = new HashMap<>();
                // cambio las consonantes
                for (Map.Entry<Ficha, Integer> entry : atrilJ2.entrySet()) {
                    Ficha ficha = entry.getKey();
                    int cantidad = entry.getValue();
                    String letra = ficha.getLetra();
                    if (!"AEIOU".contains(letra)) fichasPorCambiar.put(ficha, cantidad);
                }
                cambiarFichas(atrilJ2, fichasPorCambiar);
            } else {
                List<String> trozosPalabra = m.getPalabra();
                StringBuilder palabra = new StringBuilder();
                for (String trozo : trozosPalabra) palabra.append(trozo);

                String orientacion;
                if (m.isVertical()) orientacion = "vertical";
                else orientacion = "horizontal";
                if (!colocarPalabra(palabra.toString(), m.getFila(), m.getColumna(), orientacion)) {
                    Map<Ficha, Integer> fichasPorCambiar = new HashMap<>();
                    // cambio las consonantes
                    for (Map.Entry<Ficha, Integer> entry : atrilJ2.entrySet()) {
                        Ficha ficha = entry.getKey();
                        int cantidad = entry.getValue();
                        String letra = ficha.getLetra();
                        if (!"AEIOU".contains(letra)) fichasPorCambiar.put(ficha, cantidad);
                    }
                    cambiarFichas(atrilJ2, fichasPorCambiar);
                }
            };
        }
    }

    /**
     * Selecciona el mejor movimiento disponible según la dificultad de la IA.
     * <p>
     * La lógica varía por nivel:
     * <ul>
     *   <li><b>Fácil (1)</b>: Elige el movimiento con la <i>menor puntuación posible</i>, ignorando modificadores estratégicos.</li>
     *   <li><b>Difícil (2)</b>: Optimiza para la <i>máxima puntuación</i>, considerando todos los modificadores de letra/palabra.</li>
     * </ul>
     *
     * @param movimientos  Lista de movimientos generados por el algoritmo IA (no null, no vacía).
     * @param dificultad   Nivel de inteligencia (1-3). Valores fuera de rango se tratan como dificultad media.
     * @return Movimiento seleccionado, o null si la lista está vacía.
     *
     */
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
                                break;
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
                                break;
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

            // DIFICIL
            case 2:
                Random rand = new Random();
                int size = movimientos.size();
                int idx_random = rand.nextInt(size);

                best = movimientos.get(idx_random);
                break;
        }
        return best;
    }
}
