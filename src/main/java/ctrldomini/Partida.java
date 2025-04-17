package ctrldomini;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import algorisme.*;

// TODO: la partida acaba cuando se pasa en 2 turnos consecutivos
//  o cuando uno de los jugadores se queda sin fichas en le atril y en la bolsa no quedan mas

// TODO: en el driver de turno o partida poner si jugar o ir al menu para guardar, salir, etc

/**
 * Esta clase representa una Partida de Scrabble.
 * La Partida puede jugadase entre dos jugadores (PvP) o entre un jugador y una inteligencia artificial (PvIA).
 * Contiene información sobre los jugadores, un tablero, una bolsa que contiene las fichas e imformacion de la partida.
 *
 * @author: Paula Pérez
 */
public class Partida {
    private int idPartida;
    private Perfil creador;
    private Perfil oponente;
    private String nombre;
    private Tablero tablero;
    // TODO: implementar comodines
    private Queue<Ficha> bolsa;
    private List<Turno> rondas; // mirar de como gestionar esto en turno
    private LocalDateTime fechaHoraCreacion;
    private Modo modoPartida;
    private Idioma idiomaPartida;
    public  Dawg dawg; // diccionario segun el idioma seleccionado
    private int dificultad; // depende como lo implementemos, 0 es que no se usa este parametro      AUN NO IMPLEMENTADA EN EL ALGORITMO

    public enum Modo {
        PvP, PvIA
    }

    // en un futuro hacer personalizados?
    public enum Idioma {
        CAST, CAT, ENG
    }


    // CONSTRUCTORA

    /**
     * Construye una instancia de Partida en modo PvP.
     *
     * Se inicializan tablero y bolsa segun el idioma seleccionado.
     */
    public Partida(Perfil creador, Perfil oponente, int id, String nombre, Modo modoPartida, Idioma idiomaPartida) {
        this.idPartida = idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = oponente;
        idPartida = id;
        this.nombre = nombre;
        this.idiomaPartida = idiomaPartida;
        dawg = new Dawg(idiomaPartida);
        tablero = new Tablero(idiomaPartida);
        bolsa = new LinkedList<Ficha>();
        setBolsa();
        rondas = new ArrayList<>();
        inicializarPrimerTurno();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        dificultad = 0;
    }

    /**
     * Construye una instancia de Partida en modo PvIA.
     *
     * Se inicializan tablero y bolsa segun el idioma seleccionado.
     */
    public Partida(Perfil creador, int id, String nombre, Modo modoPartida, Idioma idiomaPartida, int dificultad) {
        this.idPartida = idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = null;
        idPartida = id;
        this.nombre = nombre;
        this.idiomaPartida = idiomaPartida;
        dawg = new Dawg(idiomaPartida);
        tablero = new Tablero(idiomaPartida);
        bolsa = new LinkedList<Ficha>();
        setBolsa();
        rondas = new ArrayList<>();
        inicializarPrimerTurno();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        this.dificultad = dificultad;
    }

    // MÉTODOS

    /**
     * Obtiene el identificador único de la partida.
     *
     * @return El identificador de la partida.
     */
    public int getId() {
        return idPartida;
    }

    /**
     * Obtiene el nombre de la partida.
     *
     * @return El nombre de la partida.
     */
    public String getNombre() {
        return nombre;
    }


    /**
     * Obtiene la fecha y hora de creación de la partida.
     *
     * @return La fecha y hora de creación de la partida.
     */
    public LocalDateTime getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    /**
     * Obtiene el modo de la partida (PvP o PvIA).
     *
     * @return El modo de la partida.
     */
    public Modo getModoPartida() {
        return modoPartida;
    }

    /**
     * Obtiene la dificultad de la partida (solo aplicable en modo PvIA).
     *
     * @return La dificultad de la partida o cero en caso de PvP.
     */
    public int getDificultad() {
        return dificultad;
    }

    /**
     * Obtiene el tablero asociado a la partida.
     *
     * @return El tablero de la partida.
     */
    public Tablero getTablero() {
        return tablero;
    }

    /**
     * Obtiene la bolsa de fichas de la partida.
     *
     * @return La bolsa de fichas de la partida.
     */
    public Queue<Ficha> getBolsa() {
        return bolsa;
    }

    /**
     * Obtiene el perfil del creador de la partida.
     *
     * @return El perfil del creador de la partida.
     */
    public Perfil getCreador() {
        return creador;
    }

    /**
     * Obtiene el perfil del oponente en la partida (solo aplicable en modo PvP).
     *
     * @return El perfil del oponente o null si no hay oponente.
     */
    public Perfil getOponente() {
        return oponente;
    }

    /**
     * Crea un nuevo turno en la partida y lo añade a la lista de rondas.
     */
    public void nuevoTurno(Perfil jugador) {
        Turno turno = new Turno(this, jugador);
        rondas.add(turno);
    }

    public SimpleEntry<Ficha, Ficha> sortearPrimerTurno() {
        Ficha fichaj1 = partida.getBolsa();
        Ficha fichaj2 = partida.getBolsa();
        return {fichaj1, fichaj2};
    }

    public void inicializarPrimerTurno() {
        Perfil primerJugador;
        SimpleEntry<Ficha, Ficha> sorteo = sortearPrimerTurno();
        if (sorteo.getKey().getLetra() <= sorteo.getValue().getLetra()) primerJugador = creador;
        else primerJugador = oponente;
        nuevoTurno(primerJugador);
        rondas.get(0).inicializarAtriles();
    }

    /**
     * Configura la bolsa de fichas de la partida en función del idioma seleccionado.
     * Mezcla la bolsa de fichas para ganrantizar una partida correcta con su perteneciente parte de aleatoriedad.
     */
    public void setBolsa() {
        Map<Ficha, Integer> mapaBolsa = new HashMap<>();
        switch (idiomaPartida) {
            case CAT:
                AlfabetoCAT alfabetoCat = new AlfabetoCAT();
                mapaBolsa = alfabetoCat.getMapaFichas();
                break;
            case CAST:
                AlfabetoCAST alfabetoCast = new AlfabetoCAST();
                mapaBolsa = alfabetoCast.getMapaFichas();
                break;
            case ENG:
                AlfabetoING alfabetoING = new AlfabetoING();
                mapaBolsa = alfabetoING.getMapaFichas();
                break;
            default:
                break;
        }

        List<Ficha> listaTemporal = new ArrayList<>();
        for (Map.Entry<Ficha, Integer> entry : mapaBolsa.entrySet()) {
            Ficha ficha = entry.getKey();
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) listaTemporal.add(ficha);
        }

        // mezclo las fichas para randomizar las posiciones
        Collections.shuffle(listaTemporal);
        bolsa.addAll(listaTemporal);
    }
}
