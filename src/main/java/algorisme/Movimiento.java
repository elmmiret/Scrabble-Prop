package algorisme;

import java.util.List;

/**
 * Representa un movimiento válido en el juego Scrabble, conteniendo:
 * <ul>
 *   <li>Posición inicial (fila, columna)</li>
 *   <li>Lista de letras que forman la palabra</li>
 *   <li>Orientación del movimiento (horizontal/vertical)</li>
 * </ul>
 *
 * <p>Proporciona métodos para acceder a la información del movimiento
 * y convertir la lista de letras en una palabra concatenada.</p>
 *
 * @author Arnau Miret
 */
public class Movimiento {
    private final int fila;
    private final int columna;
    private final List<String> palabra;
    private final boolean esVertical;

    /**
     * Constructor que crea un nuevo movimiento.
     *
     * @param fila Fila inicial
     * @param columna Columna inicial
     * @param palabra Lista de letras que forman la palabra
     * @param esVertical true si el movimiento es vertical, false si es horizontal
     */
    public Movimiento(int fila, int columna, List<String> palabra, boolean esVertical) {
        this.fila = fila;
        this.columna = columna;
        this.palabra = palabra;
        this.esVertical = esVertical;
    }

    /**
     * Obtiene la fila inicial del movimiento en el tablero.
     *
     * @return Entero que representa la posición vertical (fila) del movimiento
     */
    public int getFila() { return fila; }

    /**
     * Obtiene la columna inicial del movimiento en el tablero.
     *
     * @return Entero que representa la posición horizontal (columna) del movimiento
     */
    public int getColumna() { return columna; }

    /**
     * Obtiene la lista de letras que componen la palabra del movimiento.
     *
     * @return Lista de Strings donde cada elemento es una letra de la palabra
     */
    public List<String> getPalabra() { return palabra; }

    /**
     * Indica si el movimiento es en dirección vertical.
     *
     * @return true si el movimiento es vertical, false si es horizontal
     */
    public boolean isVertical() { return esVertical; }

    /**
     * Convierte la lista de letras del movimiento en una palabra concatenada.
     *
     * @return String que representa la palabra completa formada por todas las letras
     */
    public String getPalabraComoString() {
        return concatenarPalabra(palabra);
    }

    /**
     * Método privado que concatena una lista de letras en una palabra.
     *
     * @param letras Lista de Strings con las letras a concatenar
     * @return String resultante de concatenar todas las letras
     * @throws NullPointerException si la lista de letras es null
     */
    private String concatenarPalabra(List<String> letras) {
        StringBuilder palabra = new StringBuilder();
        for(String s : letras) {
            palabra.append(s);
        }
        return palabra.toString();
    }
}
