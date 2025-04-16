package ctrldomini;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;  // para dar formato a la fecha y hora
import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class Partida {
    private static int idPartida = 0;
    private Perfil creador;
    private Perfil oponente;
    private String nombre;
    private Tablero tablero;
    private Queue<Ficha> bolsa;
    private List<Turno> rondas; // mirar de como gestionar esto en turno
    private LocalDateTime fechaHoraCreacion;
    private Modo modoPartida;
    private int dificultad; // depende como lo implementemos, 0 es que no se usa este parametro
    // TODO: añadir diccionario
    // funcion para cargar el diccionario con lo que hay en la rama del algoritmo.

    // implementar diccionario de alguna manera para que se comunique desde la calse partida

    public enum Modo {
        PvP, PvIA
    }

    // Constructora PvP
    public Partida(Perfil creador, Perfil oponente, String nombre, Modo modoPartida) {
        this.idPartida = ++idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = oponente;
        this.nombre = nombre;
        tablero = new Tablero();
        bolsa = new LinkedList<Ficha>();
        rondas = new ArrayList<>();
        fechaHoraCreacion = LocalDateTime.now();
        this.modoPartida = modoPartida;
        dificultad = 0;
    }

    // Constructora PvIA
    public Partida(Perfil creador, Perfil oponente, String nombre, Modo modoPartida, int dificultad) {
        this.idPartida = ++idPartida; // no se como lo vamos a implementar, hacer que simplemente sea incremental?
        this.creador = creador;
        this.oponente = null;
        this.nombre = nombre;
        tablero = new Tablero();
        bolsa = new LinkedList<Ficha>();
        rondas = new ArrayList<>();
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

    public Perfil getCreador() {
        return creador;
    }

    public Perfil getOponente() {
        return oponente;
    }

    public void nuevoTurno() {
        //Turno turno = new Turno(this,...); // mirar de concretar
        //rondas.add(turno);
    }
}
