import Ficha // de alguna manera

public class Turno {
    private int numero;
    private Jugador jugador;
    // mapa de ficha (letra y puntuación) y la cantidad que tienes
    private Map<Ficha,Integer> atrilJ1;
    private Map<Ficha,Integer> atrilJ2;

    const TipoJugada = {
        CAMBIAR: "cambiar",
        PASAR: "pasar",
        COLOCAR: "colocar",
    }
    private TipoJugada tipoJugada;


    // Constructora
    public Turno(int numero, String jugador,Map<Ficha,Integer> atrilJ1, Map<Ficha,Integer> atrilJ2, TipoJugada tipoJugada) {
        this.numero = numero;
        this.jugador = jugador;
        this.atrilJ1 = atrilJ1;
        this.atrilJ2 = atrilJ2;
        this.tipoJugada = tipoJugada;
    }

    // Métodos

    // Retorna el numero de fichas descartadas para saber cuantas ha de robar por consecuente
    public int cambiarFichas( {

    }

    public void robarFichas() {

    }

    public void pasarTurno() {

    }

    public void colocarFicha(Ficha f, int x, int y) {

    }

    public void retirarFicha(int x, int y) {
        // el tablero estaá en la calse partida
        if (Tablero[x][y].empty()) ;// error
        else {
            //Tablero[x][y] que sea null
        }

    }

    public void confirmarPalabra() {

    }

    public String pedirPista() {

    }
}
