package ctrldomini;
import exceptions.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

public class Tablero {
    private List<List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>>> tablero;
    public static final int FILAS = 15;
    public static final int COLUMNAS = 15;

    // que cada casilla tenga un set de fichas y si se pone una ficha borramos el set entero.

    public enum TipoModificador {
        dobleTantoDeLetra, tripleTantoDeLetra, dobleTantoDePalabra, tripleTantoDePalabra
    }
    // si esta null es que no hay niingun bonificador en esa casilla

    // Constructora
    public Tablero() {
        tablero = new ArrayList<>();
        // montar el tablero aqui directamente
        montarTablero();
    }

    public void montarTablero () {
        TipoModificador[][] mapaModificadores = new TipoModificador[][] {
                {TipoModificador.tripleTantoDePalabra, null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null, TipoModificador.tripleTantoDePalabra},
                {null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null},
                {null, null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null, null},
                {TipoModificador.dobleTantoDeLetra, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, TipoModificador.dobleTantoDeLetra},
                {null, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, null},
                {null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null},
                {null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDeLetra, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null},
                {TipoModificador.tripleTantoDePalabra, null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null, TipoModificador.tripleTantoDePalabra},
                {null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDeLetra, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null},
                {null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null},
                {null, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, null},
                {TipoModificador.dobleTantoDeLetra, null, null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null, null, TipoModificador.dobleTantoDeLetra},
                {null, null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null, null},
                {null, TipoModificador.dobleTantoDePalabra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDeLetra, null, null, null, TipoModificador.dobleTantoDePalabra, null},
                {TipoModificador.tripleTantoDePalabra, null, null, TipoModificador.dobleTantoDeLetra, null, null, null, TipoModificador.tripleTantoDePalabra, null, null, null, TipoModificador.dobleTantoDeLetra, null, null, TipoModificador.tripleTantoDePalabra}
        };

        tablero = new ArrayList<>();
        for (int i = 0; i < FILAS; i++) {
            List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                SimpleEntry<Ficha, TipoModificador> fichaYModificador = new SimpleEntry<>(null, mapaModificadores[i][j]);
                Set<String> abecedario = new HashSet<>();
                fila.add(new SimpleEntry<>(fichaYModificador, abecedario));
            }
            tablero.add(fila);
        }
    } 

    // Métodos
    public Ficha getFicha(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getKey();
    }

    public String getLetra(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);

        SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>> casilla = tablero.get(x).get(y);
        if (casilla.getKey().getKey() != null) return casilla.getKey().getKey().getLetra();
        else return null;
    }

    public int getPuntuacion(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getKey().getPuntuacion();
    }

    public TipoModificador getTipoModificador(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getValue();
    }

    public void setFicha(Ficha f, int x, int y) throws CoordenadaFueraDeRangoException, CasillaOcupadaException{
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        if (getFicha(x, y) != null) throw new CasillaOcupadaException(x, y);
        // si ya tiene algo escrito que de error, depende como implementemos el poder ir poniendo sin verifcar o colocar una vez verificada que la palabra existe y cabe

        SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>> casilla = tablero.get(x).get(y);
        tablero.get(x).set(y, new SimpleEntry<>(new SimpleEntry<>(f, casilla.getKey().getValue()), casilla.getValue()));    }

    // imprimir
    public void imprimirTablero() throws CoordenadaFueraDeRangoException{
        // todo el texto tiene que ser de tres espacios para garantizar la representacion de todas las letras, ej: L·L
        String color, texto, ficha;
        TipoModificador m;
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
                ficha = getLetra(i, j);
                if (ficha == null) texto = "   ";
                else if (ficha.length() == 1) texto = " "+ficha+" ";
                else if (ficha.length() == 2) texto = ficha+" ";
                else texto = ficha;
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
