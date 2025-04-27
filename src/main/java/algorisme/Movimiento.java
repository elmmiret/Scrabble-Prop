package algorisme;

import java.util.List;

public class Movimiento {
    private final int fila;
    private final int columna;
    private final List<String> palabra;
    private final boolean esVertical;

    public Movimiento(int fila, int columna, List<String> palabra, boolean esVertical) {
        this.fila = fila;
        this.columna = columna;
        this.palabra = palabra;
        this.esVertical = esVertical;
    }

    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public List<String> getPalabra() { return palabra; }
    public boolean isVertical() { return esVertical; }

    public String getPalabraComoString() {
        return concatenarPalabra(palabra);
    }

    private String concatenarPalabra(List<String> letras) {
        StringBuilder palabra = new StringBuilder();
        for(String s : letras) {
            palabra.append(s);
        }
        return palabra.toString();
    }
}
