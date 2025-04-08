package algorisme;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Algoritmo {


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

    public Map<Integer, Set<Character>> comprobarCrossChecks(char[][] tablero, Set<Character> letrasAtril, Set<String> diccionario)
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


    public List<int[]> computeAnchors(char[][] tablero)
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



    //función que devuelve la mejor palabra que se puede colocar en el tablero
    // a partir de las letras del atril
    //devuelve una lista con la letra o digrafo y su correspondiente posicion x, y
    /*public List<SimpleEntry<String, SimpleEntry<Integer, Integer>>> MejorMovimiento(char[][]  tablero, Set<Character> letrasAtril) {

    }*/
}
