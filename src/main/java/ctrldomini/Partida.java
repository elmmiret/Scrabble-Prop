package ctrldomini;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;  // para dar formato a la fecha y hora
import java.util.Queue;
import java.util.LinkedList;

public class Partida {
    private static int idPartida = 0;
    private Jugador creador;
    private Jugador oponente;
    private String nombre;
    private Tablero tablero;
    private Queue<Ficha> bolsa;
    private List<Turno> rondas; // mirar de como gestionar esto en turno
    private LocalDateTime fechaHoraCreacion;
    private Modo modoPartida;
    private int dificultad; // depende como lo implementemos

    public enum Modo {
        PvP, PvIA
    }

    // Constructora PvP
    public Partida(Jugador creador, Jugador oponente, String nombre, Modo modoPartida) {
        this.idPartida = ++idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = oponente;
        this.nombre = nombre;
        tablero = new Tablero();
        bolsa = new queue<Ficha>();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        dificultad = null;
    }

    // Constructora PvIA
    public Partida(Jugador creador, Jugador oponente, String nombre, Modo modoPartida, int dificultad) {
        this.idPartida = ++idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = null;
        this.nombre = nombre;
        tablero = new Tablero();
        bolsa = new queue<Ficha>();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        this.dificultad = dificultad;
    }

    // Métodos
    public int getId() {
        return idPartida;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDateTime getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public Modo getModoPartida() {
        return modoPartida;
    }

    public int getDificultad() {
        return dificultad;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Queue<Ficha> getBolsa() {
        return bolsa;
    }

    public Jugador getCreador() {
        return creador;
    }

    public Jugador getOponente() {
        return oponente;
    }
}
