package ctrldomini;
import exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

/**
 * Esta clase representa el tablero de Scrabble.
 * Contiene un tablero en el que se almacena la Ficha que se coloca y su modificador, si tiene.
 * 
 * @author: Paula Pérez
 */
public class Tablero {
    private List<List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>>> tablero;
    public static final int FILAS = 15;
    public static final int COLUMNAS = 15;
    public enum TipoModificador {   // si esta null es que no hay niingun bonificador en esa casilla
        dobleTantoDeLetra, tripleTantoDeLetra, dobleTantoDePalabra, tripleTantoDePalabra
    }

    // CONSTRUCTORA

    /**
     * Construye una instancia de Tablero.
     *
     * Todos los tableros tienen la misma estructura y distribución, por lo tanto, lo monta también.
     */
    public Tablero() {
        tablero = new ArrayList<>();
        montarTablero();
    }

    /**
     * Monta el Tablero.
     *
     * Lo inicializa vacío y con los modificadores en sus respectivas casillas segun las reglas del juego de un Tablero 15x15.
     */
    public void montarTablero () {
        TipoModificador m;
        for (int i = 0; i < FILAS; i++) {
            List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                m = null;
                // triple palabra
                if ((i == 0 || i == 7 || i == 14) && (j == 0 || j == 7 || j == 14)) {
                    m = TipoModificador.tripleTantoDePalabra;
                }
                // doble palabra
                else if ((i == j || i + j == 14) && i != 0 && i != 7 && i != 14) {
                    m = TipoModificador.dobleTantoDePalabra;
                }
                // triple letra
                else if ((i == 5 || i == 9) && (j == 1 || j == 5 || j == 9 || j == 13) ||
                        (i == 1 || i == 5 || i == 9 || i == 13) && (j == 5 || j == 9)) {
                    m = TipoModificador.tripleTantoDeLetra;
                }
                // doble letra
                else if ((i == 0 && (j == 3 || j == 11)) ||
                        (i == 2 && (j == 6 || j == 8)) ||
                        (i == 3 && (j == 0 || j == 7 || j == 14)) ||
                        (i == 6 && (j == 2 || j == 6 || j == 8 || j == 12)) ||
                        (i == 7 && (j == 3 || j == 11)) ||
                        (i == 8 && (j == 2 || j == 6 || j == 8 || j == 12)) ||
                        (i == 11 && (j == 0 || j == 7 || j == 14)) ||
                        (i == 12 && (j == 6 || j == 8)) ||
                        (i == 14 && (j == 3 || j == 11))) {
                    m = TipoModificador.dobleTantoDeLetra;
                }

                SimpleEntry<Ficha, TipoModificador> fichaYModificador = new SimpleEntry<>(null, m);
                Set<String> abecedario = new HashSet<>();
                fila.add(new SimpleEntry<>(fichaYModificador, abecedario));
            }
            tablero.add(fila);
        }
    } 

    // MÉTODOS

    /**
     * Obtiene la ficha que se encuentra en la posición (x, y) del tablero.
     *
     * @param x Fila de la ficha.
     * @param y Columna de la ficha.
     * @return La ficha situada en la posición (x, y) o null si no hay ficha.
     */
    public Ficha getFicha(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getKey();
    }

    /**
     * Obtiene el modificador de la casilla del tablero especificada.
     *
     * @param x Fila de la casilla.
     * @param y Columna de la casilla.
     * @return Tipo de modificador asignado a la posición, o null si no tiene.
     */
    public TipoModificador getTipoModificador(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getValue();
    }

    /**
     * Coloca una ficha en la posicion (x, y) del tablero.
     * Si ya hay una ficha, lanza error ya que el juego no permite cambiar fichas una vez están bien colocadas.
     *
     * @pre x Está en mayúscula y pertenece al rango de letras del tablero
     *
     * @param f Ficha que se desea colocar.
     * @param x Fila donde colocar la ficha.
     * @param c Columna donde colocar la ficha.
     */
    public void setFicha(Ficha f, char x_char, int y) throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        int x = x_char - 'A';
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        if (getFicha(x, y) != null) throw new CasillaOcupadaException(x, y);

        SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>> casilla = tablero.get(x).get(y);
        tablero.get(x).set(y, new SimpleEntry<>(new SimpleEntry<>(f, casilla.getKey().getValue()), casilla.getValue()));    }

    //  ESCRITURA

    /**
     * Imprime la representación del tablero de juego.
     *
     * Muestra tanto las fichas colocadas como los modificadores de cada casilla en forma de color.
     * Proveé una pequeña leyenda para asociar el color de una casilla al modificador que representa.
     */
    public void imprimirTablero() throws CoordenadaFueraDeRangoException{
        // todo el texto tiene que ser de tres espacios para garantizar la representacion de todas las letras, ej: L·L
        String color, texto;
        TipoModificador m;
        Ficha f;
        // codigo ANSI para colorear el tablero
        // para los colores, si queremos cambiar el background, la secuencia que hay que seguir es: \u001B[48;2;R;G;Bm
        // el color de la letra a negro es 30 en vez de 48 y 000
        String rojo = "\u001B[48;2;255;0;0m\u001B[30m";
        String azul = "\u001B[48;2;0;0;255m\u001B[30m";
        String cielo = "\u001B[48;2;106;174;234m\u001B[30m";
        String naranja = "\u001B[48;2;245;135;15m\u001B[30m";
        String dorado = "\u001B[48;2;234;200;106m\u001B[30m"; // para el centro

        System.out.println("     1   2   3   4   5   6   7   8   9  10  11  12  13  14  15");
        for (int i = 0; i < FILAS; i++) {
            System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
            System.out.printf("%2s ", (char)('A' + i));
            System.out.print("|");
            for (int j = 0; j < COLUMNAS; j++) {
                color = "";
                texto = "   ";
                f = getFicha(i, j);
                if (f != null) texto = f.getLetra();

                if (texto.length() == 1) texto = " "+texto+" ";
                else if (texto.length() == 2) texto = texto+" ";

                m = getTipoModificador(i, j);
                if (m != null) {
                    switch (m) {
                        case dobleTantoDeLetra:
                            color = cielo;
                            break;
                        case tripleTantoDeLetra:
                            color = azul;
                            break;
                        case dobleTantoDePalabra:
                            color = naranja;
                            break;
                        case tripleTantoDePalabra:
                            color = rojo;
                            break;
                    }
                }
                if (i == FILAS/2 && j == COLUMNAS/2) {
                    color = dorado;
                }
                System.out.print(color + texto + "\u001B[0m");
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");

        // leyenda para el código de colores
        // TODO: Cambiar nomenclatura segun el idioma en el que se juegue
        System.out.println();
        System.out.println("           LEYENDA");
        System.out.print(rojo + "   " + "\u001B[0m");
        System.out.println(" TRIPLE TANTO DE PALABRA");
        System.out.print(naranja + "   " + "\u001B[0m");
        System.out.println(" DOBLE  TANTO DE PALABRA");
        System.out.print(azul + "   " + "\u001B[0m");
        System.out.println(" TRIPLE TANTO DE  LETRA");
        System.out.print(cielo + "   " + "\u001B[0m");
        System.out.println(" DOBLE  TANTO DE  LETRA");
        System.out.print(dorado + "   " + "\u001B[0m");
        System.out.println(" CENTRO");
    }
}
