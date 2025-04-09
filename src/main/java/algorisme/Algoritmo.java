package algorisme;

import javax.lang.model.util.SimpleElementVisitor6;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;


public class Algoritmo {


    /**
     *
     *  Funcion que devuelva la mejor palara a colocar
     *  Va a devolver una lista de (string, pair<x,y>) para cada palabra, para saber sus caracteres o digrafos y su posicion
     *  @param tablero
     *
     *  1. Recorremos la matriz del tablero por filas
     *
     *  2. Computar los cross-checks de las casillas directamente arriba y abajo de casillas colocadas, sin tener en cuenta otras filas.
     *     Esto nos permite, a la hora de hacer movimientos, evitar poner letras que no generan palabras validas (en vertical), si estas tocan con casillas colocadas
     *
     *  3. A la que se encuentra una casilla ocupada, vemos si el ancla tiene una casilla ocupada a su izquierda
     *      3.1 Si la tiene, trataremos de extender el ancla y la parte derecha solamente con esa parte izquierda
     *      3.2 Si no la tiene, la parte izquerda sera computada con letras del atril para luego extender estas partes hacia el ancla y parte derecha
     *         (se computa con letras del atril que existen en los cross-checks)
     *
     *  4. Generar la lista con la mejor palabra (pair<pair<string,bool>, pair<x,y>>)
     *                                                (letra, esDeAtril)   (posicion)
     */

    public List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorMovimiento(Dawg dawg, Tablero tablero, String[] atril) {

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
        int mejorPuntuacion = 0;

        // HORIZONTAL Y VERTICALMENTE (TRANSPUESTO)

        for(int i = 0; i < 2; i++) {
            // Modificamos los cross-check sets para no computar con letras qhe no generan palabras válidas
            computarCrossChecks(dawg,tablero,atril);

            // Obtenemos las posiciones de las anclas del tablero
            List<SimpleEntry<Integer, Integer>> anclas = computarAnclas(tablero);

            // Por cada ancla, obtenemos la mejor palaba
            for(SimpleEntry<Integer, Integer> ancla : anclas) {
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabraAncla = computarPalabraAncla(dawg,tablero,ancla,atril);
                int puntuacionPalabra = computarPuntuacionPalabra(mejorPalabraAncla);
                if(puntuacionPalabra > mejorPuntuacion) {
                    mejorPalabra = mejorPalabraAncla;
                }
            }

            // Transponemos la matriz del tablero
            transponerTablero(tablero);
        }

        return mejorPalabra;

    }

    /**
     *
     * @param dawg
     * @param tablero
     * @param pos
     * @param atril
     * @return
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarPalabraAncla(Dawg dawg, Tablero tablero, SimpleEntry<Integer, Integer> pos, String[] atril) {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabraAncla = new ArrayList<>();

        //backtracking

        // Si hay casilla a la izquierda del ancla, se computa la parte izquierda y luego la derecha
        int x = pos.getKey();
        int y = pos.getValue();

        if(casillaCorrecta(x, y)) {

            // Mirar si la casilla a la izquierda del ancla es vacía o no
            // Si la casilla esta ocupada
            if(tablero[x][y].getKey().getKey() != null) {

                // Computar la parte izquierda con las casillas del tablero

            }

            // Si la casilla no esta ocupada
            else {

                // Computar la parte izquierda con las casillas del atril
                // Funcion para saber la logitud maxima de la parte izquierda
                int maxParteIzquierda = tamañoParteIzquierda(tablero, pos);

                // Backtracking de las partes izquierdas posibles con las fichas del atril y tamaño indicado


            }

        }

        return mejorPalabraAncla;
    }

    /**
     *
     * @param palabra
     * @return
     */
    private int computarPuntuacionPalabra(List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra) {

    }

    /**
     *
     * @param tablero
     */
    private void transponerTablero(List<List<SimpleEntry<SimpleEntry<String, TipoModificador>, Set>>> tablero) {

    }


    /**
     *
     *  Funcion que coloque la palabra en el tablero y modifique la estadisticas del jugador que ha hecho el movimiento
     *                      (en clase PARTTIDA, partida obtiene la palabra y computa)
     *
     */

    /**
     *
     * @param tablero
     * @param fila
     * @param columna
     * @param letra
     * @param diccionario
     * @return
     */
    private boolean esPalabraValida(char[][] tablero, int fila, int columna, char letra, Set<String> diccionario)
    {
        int filaIni = fila;
        while (fila > 0 && fila != '.')
        {
            --fila;
        }
        StringBuilder paraula = new StringBuilder(); // se podria hacer con strings pero si la palabra es larga es mas ineficiente ya que cada vez crea un nuevo string
        while (fila < tablero[0].length && (fila != '.' || fila == filaIni))
        {
            if (fila != filaIni)
                paraula.append(tablero[fila][columna]);
            else
                paraula.append(letra);
            ++fila;
        }
        return diccionario.contains(paraula);
    }

    /**
     *
     * @param dawg
     * @return
     */
    public Map<Integer, Set<Character>> computarCrossChecks(Dawg dawg/*tablero y atril*/)
    {
        Map<Integer, Set<Character>> crossChecksValidos = new HashMap<>();
        for (int f = 0; f < tablero[0].length; ++f) {
            for (int c = 0; c < tablero.length; ++c) {
                if (tablero[f][c] == '.') {
                    Set<Character> caracteresValidos = new HashSet<>();
                    for (char letra : letrasAtril) {
                        if (esPalabraValida(tablero, f, c, letra, diccionario)) {
                            caracteresValidos.add(letra);
                        }
                    }
                    crossChecksValidos.put(f * tablero[0].length + c, caracteresValidos);

                }
            }
        }
        return crossChecksValidos;
    }

    /**
     *
     * @param tablero
     * @return
     */
    public List<SimpleEntry<Integer, Integer>> computarAnclas(char[][] tablero)
    {
        List<int[]> listaAnchors = new ArrayList<>() ;
        for (int fila = 0; fila < tablero[0].length; ++fila)
            for (int columna = 0; columna < tablero.length; ++columna)
            {
                if (tablero[fila][columna] == '.' && tieneAdyacentes(tablero, fila, columna))
                {
                    listaAnchors.add(new int[]{fila, columna});
                }
            }
        return listaAnchors;
    }

    /**
     *  Función auxiliar para ayuda a computarAnclas
     * @param tablero
     * @param fila
     * @param columna
     * @return
     */
    private boolean tieneAdyacentes(char[][] tablero, int fila, int columna)
    {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions)
        {
            int dirX = direction[0] + fila;
            int dirY = direction[1] + columna;
            if (tablero[dirX][dirY] != '.')
                return true;
        }
        return false;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    private boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < 15 && y >= 0 && y < 15;
    }

    private int tamañoParteIzquierda(Tablero tablero, SimpleEntry<Integer, Integer> ancla) {
        int size = 0;
        int y = ancla.getValue();

        // Mientras las casillas a la izquierda sean correctas y no esten ocupadas, sumar uno a size
        for(int x = ancla.getKey() - 1; tablero[x][y].getKey().getKey() != null && casillaCorrecta(x, y); x--) {
            ++size;
        }

        return size;
    }



    //función que devuelve la mejor palabra que se puede colocar en el tablero
    // a partir de las letras del atril
    //devuelve una lista con la letra o digrafo y su correspondiente posicion x, y
    /*public List<SimpleEntry<String, SimpleEntry<Integer, Integer>>> MejorMovimiento(char[][]  tablero, Set<Character> letrasAtril) {

    }*/
}
