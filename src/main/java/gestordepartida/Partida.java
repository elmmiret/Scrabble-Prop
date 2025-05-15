package gestordepartida;

import algorisme.AlfabetoCAST;
import algorisme.AlfabetoCAT;
import algorisme.AlfabetoING;
import gestordeperfil.*;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import algorisme.*;

/**
 * Esta clase representa una partida del juego Scrabble, permitiendo modalidades
 * de juego entre dos jugadores (PvP) o contra una inteligencia artificial (PvIA).
 * Gestiona todos los elementos del juego incluyendo jugadores, tablero, bolsa de fichas,
 * turnos y validación de palabras mediante un diccionario DAWG.
 *
 * <p>Características principales:</p>
 * <ul>
 *   <li>Soporte para múltiples idiomas (CAST, CAT, ENG)</li>
 *   <li>Generación aleatoria de la bolsa de fichas</li>
 *   <li>Sistema de turnos con registro histórico</li>
 *   <li>Mecánica de puntuación basada en fichas</li>
 *   <li>Detección de palabras válidas mediante DAWG</li>
 * </ul>
 *
 * @author Paula Pérez Chia
 */
public class Partida {

    /** Identificador único de la partida */
    private int idPartida;

    /** Perfil del jugador creador de la partida */
    private Perfil creador;

    /** Perfil del oponente (null en modo PvIA) */
    private Perfil oponente;

    /** Nombre descriptivo de la partida */
    private String nombre;

    /** Tablero de juego con celdas y multiplicadores */
    private Tablero tablero;

    /** Mapa de fichas disponibles con sus cantidades */
    private Map<Ficha,Integer> mapaFichas;

    /** Mapa rápido de letras a fichas para consulta de puntuación */
    private Map<String, Ficha> mapaLetras;

    /** Bolsa de fichas mezcladas para extracción aleatoria */
    private Queue<Ficha> bolsa;

    /** Lista histórica de todos los turnos jugados */
    private List<Turno> rondas; // mirar de como gestionar esto en turno

    /** Fecha y hora de creación de la partida */
    private LocalDateTime fechaHoraCreacion;

    /** Modalidad de juego (PvP/PvIA) */
    private Modo modoPartida;

    /** Idioma base para fichas y diccionario */
    private Idioma idiomaPartida;

    /** Diccionario DAWG para validación de palabras */
    public  Dawg dawg; // diccionario segun el idioma seleccionado

    /**  */
    private Algorithm algorithm;        // se mueve a partida

    /** Nivel de dificultad IA (0 = no aplicable) */
    private int dificultad; // depende como lo implementemos, 0 es que no se usa este parametro      AUN NO IMPLEMENTADA EN EL ALGORITMO

    /** Modalidades de juego disponibles */
    public enum Modo {
        PvP, PvIA
    }

    /** Idiomas soportados por el juego */
    public enum Idioma {
        CAST, CAT, ENG
    }

    /**
     * Construye una partida multijugador (PvP).
     *
     * @param creador Perfil del jugador creador
     * @param oponente Perfil del jugador oponente
     * @param id Identificador único de la partida
     * @param nombre Nombre descriptivo de la partida
     * @param modoPartida Modalidad de juego (PvP/PvIA)
     * @param idiomaPartida Idioma para fichas y diccionario
     * @throws IllegalArgumentException Si hay error al cargar el diccionario
     */
    public Partida(Perfil creador, Perfil oponente, int id, String nombre, Modo modoPartida, Idioma idiomaPartida) {
        //this.idPartida = idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = oponente;
        this.idPartida = id;
        this.nombre = nombre;
        this.idiomaPartida = idiomaPartida;
        try {
            dawg = new Dawg(idiomaPartida);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Error al inicializar el DAWG: " + e.getMessage());
        }
        tablero = new Tablero(idiomaPartida);
        mapaFichas = new HashMap<>();
        mapaLetras = new HashMap<>();
        bolsa = new LinkedList<Ficha>();
        setBolsa();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        algorithm = null;
        dificultad = 0;
        rondas = new ArrayList<>();
        inicializarPrimerTurno();
    }

    /**
     * Construye una partida contra IA (PvIA).
     *
     * @param creador Perfil del jugador humano
     * @param id Identificador único de la partida
     * @param nombre Nombre descriptivo de la partida
     * @param modoPartida Modalidad de juego (debe ser PvIA)
     * @param idiomaPartida Idioma para fichas y diccionario
     * @param dificultad Nivel de dificultad IA (1-10)
     * @throws IllegalArgumentException Si hay error al cargar el diccionario
     */
    public Partida(Perfil creador, int id, String nombre, Modo modoPartida, Idioma idiomaPartida, int dificultad) {
        //this.idPartida = idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = null;
        this.idPartida = id;
        this.nombre = nombre;
        this.idiomaPartida = idiomaPartida;
        try {
            dawg = new Dawg(idiomaPartida);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Error al inicializar el DAWG: " + e.getMessage());
        }
        tablero = new Tablero(idiomaPartida);
        mapaFichas = new HashMap<>();
        mapaLetras = new HashMap<>();
        bolsa = new LinkedList<Ficha>();
        setBolsa();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        algorithm = new Algorithm(dawg);
        this.dificultad = dificultad;
        rondas = new ArrayList<>();
        inicializarPrimerTurno();
    }

    /**
     * Obtiene el identificador único de la partida.
     * @return Número entero con el ID
     */
    public int getId() {
        return idPartida;
    }

    /**
     * Obtiene el nombre descriptivo de la partida.
     * @return Cadena con el nombre
     */
    public String getNombre() {
        return nombre;
    }


    /**
     * Obtiene la fecha y hora de creación.
     * @return Objeto LocalDateTime con la marca temporal
     */
    public LocalDateTime getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    /**
     * Obtiene la modalidad de juego actual.
     * @return Valor del enum Modo
     */
    public Modo getModoPartida() {
        return modoPartida;
    }

    /**
     * Obtiene el nivel de dificultad IA.
     * @return Entero entre 0 (inactivo) y 10
     */
    public int getDificultad() {
        return dificultad;
    }

    /**
     * Obtiene el idioma configurado para la partida.
     * @return Valor del enum Idioma
     */
    public Idioma getIdioma() {
        return idiomaPartida;
    }

    /**
     * Obtiene el diccionario DAWG para validación.
     * @return Instancia de Dawg configurada
     */
    public Dawg getDawg() {
        return dawg;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Obtiene el tablero de juego actual.
     * @return Instancia de Tablero
     */
    public Tablero getTablero() {
        return this.tablero;
    }

    /**
     * Obtiene la bolsa de fichas disponible.
     * @return Cola de Fichas mezcladas
     */
    public Queue<Ficha> getBolsa() {
        return bolsa;
    }

    /**
     * Obtiene el mapa de fichas del alfabeto elegido para la partida
     * @return Mapa de las fichas del alfabeto de la partida
     */
    public Map<Ficha, Integer> getMapaFichas() { return mapaFichas; }

    /**
     * Obtiene el mapa de letras - fichas para la obtencion de las puntuaciones
     * @return Mapa String - Ficha del alfabeto de la partida
     */
    /**
     * Obtiene el historial completo de turnos.
     * @return Lista de Turnos en orden cronológico
     */
    public List<Turno> getRondas() {
        return rondas;
    }

    /**
     * Obtiene el perfil del jugador creador.
     * @return Instancia de Perfil del creador
     */
    public Perfil getCreador() {
        return creador;
    }


    public Map<String, Ficha> getMapaLetras() {
        return mapaLetras;
    }

    /**
     * Obtiene el perfil del oponente (PvP).
     * @return Instancia de Perfil o null en PvIA
     */
    public Perfil getOponente() {
        return oponente;
    }

    /**
     * Crea un nuevo turno y actualiza el estado del juego.
     *
     * @param jugador Perfil del jugador activo
     * @param puntosJ1 Puntuación acumulada jugador 1
     * @param puntosJ2 Puntuación acumulada jugador 2
     * @param atrilJ1 Fichas disponibles del jugador 1
     * @param atrilJ2 Fichas disponibles del jugador 2
     */
    public void nuevoTurno(Perfil jugador, int puntosJ1, int puntosJ2, Map<Ficha,Integer> atrilJ1, Map<Ficha,Integer> atrilJ2) {
        Turno turno = new Turno(this, jugador, puntosJ1, puntosJ2, atrilJ1, atrilJ2);
        rondas.add(turno);
    }


    /**
     * Determina el orden inicial de juego mediante sorteo de fichas.
     *
     * @return Par ordenado con la ficha de cada jugador
     */
    public SimpleEntry<Ficha, Ficha> sortearPrimerTurno() {
        Ficha fichaj1 = getBolsa().poll();
        Ficha fichaj2 = getBolsa().poll();
        SimpleEntry<Ficha, Ficha> resultado = new SimpleEntry<>(fichaj1, fichaj2);
        getBolsa().add(fichaj2);
        getBolsa().add(fichaj1);
        return resultado;
    }

    /**
     * Inicializa el primer turno tras determinar el orden de juego.
     * Crea el primer registro de turno e inicializa los atriles.
     */
    public void inicializarPrimerTurno() {
        Perfil primerJugador;
        SimpleEntry<Ficha, Ficha> sorteo = sortearPrimerTurno();
        if (sorteo.getKey().getLetra() == sorteo.getValue().getLetra()) primerJugador = creador;
        else primerJugador = oponente;
        Turno turno = new Turno(this, primerJugador, 0, 0);
        rondas.add(turno);
        rondas.get(0).inicializarAtriles();
    }

    /**
     * Configura la bolsa de fichas según el idioma seleccionado.
     * Mezcla las fichas y prepara las estructuras de acceso rápido.
     */
    public void setBolsa() {
        switch (idiomaPartida) {
            case CAT:
                AlfabetoCAT alfabetoCat = new AlfabetoCAT();
                mapaFichas = alfabetoCat.getMapaFichas();
                break;
            case CAST:
                AlfabetoCAST alfabetoCast = new AlfabetoCAST();
                mapaFichas = alfabetoCast.getMapaFichas();
                break;
            case ENG:
                AlfabetoING alfabetoING = new AlfabetoING();
                mapaFichas = alfabetoING.getMapaFichas();
                break;
            default:
                break;
        }

        List<Ficha> listaTemporal = new ArrayList<>();
        for (Map.Entry<Ficha, Integer> entry : mapaFichas.entrySet()) {
            Ficha ficha = entry.getKey();
            mapaLetras.put(ficha.getLetra(), ficha);
            int cantidad = entry.getValue();
            for (int i = 0; i < cantidad; i++) listaTemporal.add(ficha);
        }

        // mezclo las fichas para randomizar las posiciones
        Collections.shuffle(listaTemporal);
        bolsa.addAll(listaTemporal);
    }

    public void setBolsa(Queue<Ficha> bolsa) {
        this.bolsa = bolsa;
    }

    /**
     * Obtiene la puntuación asociada a una letra específica.
     *
     * @param letra Carácter a consultar (mayúscula)
     * @return Valor numérico de la puntuación
     * @throws NullPointerException Si la letra no existe en el idioma
     */
    public int getPuntuacionFicha(String letra)
    {
        return mapaLetras.get(letra).getPuntuacion();
    }
}
