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

    // Constructora
    public Tablero() {
        tablero = new ArrayList<>();
        // montar el tablero aqui directamente
    }

    public void montarTablero () {
        for (int i = 0; i < FILAS; i++) {
            List<SimpleEntry<Ficha, TipoModificador>> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                fila.add(new SimpleEntry<>(null, null));  // segun como lo vayamos a leer
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

}
