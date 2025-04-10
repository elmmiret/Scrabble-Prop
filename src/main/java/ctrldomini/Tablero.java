package ctrldomini;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

//

public class Tablero {
    private List<List<SimpleEntry<Ficha, TipoModificador>>> tablero;
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
        TipoModificador m;
        for (int i = 0; i < FILAS; i++) {
            List<SimpleEntry<Ficha, TipoModificador>> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                if (i == j || i+j == FILAS-1) {             // construccion diagonales
                    if (i == 0 || i == FILAS-1) m = TipoModificador.tripleTantoDePalabra;
                    else if (i == 5 || i == 9) m = TipoModificador.tripleTantoDeLetra;
                    else m = TipoModificador.dobleTantoDePalabra;
                }
                else if (i == 0 || i == FILAS-1) {          // consruccion triangulos arriba/abajo
                    if (j == 3 || j == COLUMNAS-1-3) m = TipoModificador.dobleTantoDeLetra;
                    if (j == (COLUMNAS-1)/2) m = TipoModificador.tripleTantoDePalabra;
                }
                else if (i == 1 || i == FILAS-1-1) {
                    if (j == 5 || j == COLUMNAS-1-5) m = TipoModificador.tripleTantoDeLetra;
                }
                else if (i == 2 || i == FILAS-1-2) {
                    if (j == 6 || j == COLUMNAS-1-6) m = TipoModificador.dobleTantoDeLetra;
                }
                else if (i == 3 || i == FILAS-1-3) {
                    if (j == COLUMNAS-1) m = TipoModificador.dobleTantoDeLetra;
                }
                else if (j == 0 || j == COLUMNAS-1) {          // consruccion triangulos izquierda/derecha
                    if (i == 3 || i == FILAS-1-3) m = TipoModificador.dobleTantoDeLetra;
                    if (i == (FILAS-1)/2) m = TipoModificador.tripleTantoDePalabra;
                }
                else if (j == 1 || j == COLUMNAS-1-1) {
                    if (i == 5 || i == COLUMNAS-1-5) m = TipoModificador.tripleTantoDeLetra;
                }
                else if (j == 2 || j == COLUMNAS-1-2) {
                    if (i == 6 || i == FILAS-1-6) m = TipoModificador.dobleTantoDeLetra;
                }
                else if (j == 3 || j == COLUMNAS-1-3) {
                    if (i == FILAS-1) m = TipoModificador.dobleTantoDeLetra;
                }
                else m = null;
                fila.add(new SimpleEntry<>(null, m));  // segun como lo vayamos a leer
            }
            tablero.add(fila);
        }
    } 

    // Métodos
    public Ficha getFicha(int x, int y) {
        return tablero.get(x).get(y).getKey();
    }

    public String getLetra(int x, int y) {
        return tablero.get(x).get(y).getKey().getLetra();
    }

    public int getPuntuacion(int x, int y) {
        return tablero.get(x).get(y).getKey().getPuntuacion();
    }

    public TipoModificador getTipoModificador(int x, int y) {
        return tablero.get(x).get(y).getValue();
    }

    public void setFicha(int x, int y, Ficha f) {
        tablero.get(x).set(y, new SimpleEntry<>(f, tablero.get(x).get(y).getValue()));
    }

    // imprimir
    public void imprimirTablero() {
        System.out.println("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15");
        for (int i = 0; i < FILAS; i++) {
            System.out.printf("%2d ", 'A' + i);
            for (int j = 0; j < COLUMNAS; j++) {
                String ficha = getLetra(i, j);
                TipoModificador m = getTipoModificador(i, j);

                // TODO: Cambiar nomenclatura segun el idioma en el que se juegue
                //       Ponerle casillas con ------  y ||||||
                if (ficha != null) System.out.print(ficha + " ");
                else if (m == TipoModificador.tripleTantoDePalabra) System.out.print("3P ");
                else if (m == TipoModificador.dobleTantoDePalabra) System.out.print("2P ");
                else if (m == TipoModificador.tripleTantoDeLetra) System.out.print("3L ");
                else if (m == TipoModificador.dobleTantoDeLetra) System.out.print("2L ");
                else System.out.print(" . ");
            }
            System.out.println();
        }
    }
}
