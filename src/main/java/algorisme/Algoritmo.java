package algorisme;

import javax.lang.model.util.SimpleElementVisitor6;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.atomic.AtomicReferenceArray;


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
            // Modificamos los cross-check sets para no computar con letras que no generan palabras válidas
            computarCrossChecks(dawg,tablero,atril);

            // Obtenemos las posiciones de las anclas del tablero
            List<SimpleEntry<Integer, Integer>> anclas = computarAnclas(tablero);

            // Por cada ancla, obtenemos la mejor palaba
            for(SimpleEntry<Integer, Integer> ancla : anclas) {
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabraAncla = computarPalabraAncla(dawg,tablero,ancla,atril);
                int puntuacionPalabra = obtenerPuntuacion(tablero,mejorPalabraAncla);
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
     *  Función que devuelve la mejor palabra que se puede computar en un ancla
     * @param dawg
     * @param tablero
     * @param pos
     * @param atril
     * @return
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarPalabraAncla(Dawg dawg, Tablero tablero, SimpleEntry<Integer, Integer> ancla, String[] atril) {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabraAncla = new ArrayList<>();

        //backtracking

        // Si hay casilla a la izquierda del ancla, se computa la parte izquierda y luego la derecha
        int x = ancla.getKey();
        int y = ancla.getValue();

        // Si la casilla a la izquierda del ancla queda fuera de los límites, la parte izquierda no se computa

        // en este caso la parte izquierda se computa
        if(casillaCorrecta(x, y - 1)) {
            // Mirar si la casilla a la izquierda del ancla es vacía o no
            // Si la casilla esta ocupada (parte izq. compuesta de letras ya en el tablero)
            if(tablero[x][y - 1].getKey().getKey() != null) {

                List<SimpleEntry<String, Boolean>> parteIzquierda = new ArrayList<>();
                //List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> parteDerecha = new ArrayList<>();

                // Computar la parte izquierda con las casillas del tablero
                int max_long = tamañoParteIzquierdaTablero(tablero,x,y); //HECHA

                // Funcion que devuelve la parte izquierda ya en el tablero
                parteIzquierda = computarParteIzquierdaTablero(tablero,max_long,x,y); //HECHA

                // Asignamos las posiciones del tablero a la parte izquierda
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
                mejorPalabra = asignarPosiciones(parteIzquierda,max_long,x,y);

                // Obtenemos el nodo de la última letra de la parte izquierda
                NodoDawg nodo = dawg.getRoot();
                for(int i = 0; i < parteIzquierda.size(); i++) {
                    NodoDawg siguiente = nodo.getHijos().get(parteIzquierda.getFirst().getKey());
                    if(siguiente != null) {
                        parteIzquierda.removeFirst();
                        nodo = siguiente;
                    }
                }

                boolean[] usados = new boolean[atril.length];

                // Backtracking de para encontrar la mejor parte derecha posible para la parte izquierda indicada
                int puntuacion = extenderParteDerecha(tablero,mejorPalabra,atril,usados,nodo,x,y);

                mejorPalabraAncla = mejorPalabra;

            }

            // Si la casilla no esta ocupada (parte izq. compuesta de letras del atril)
            else {

                List<List<SimpleEntry<String, Boolean>>> partesIzquierdas = new ArrayList<>();
                //List<List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>>> parteDerecha = new ArrayList<>();

                // Funcion para saber la logitud maxima de la parte izquierda
                int max_long = tamañoParteIzquierdaAtril(tablero,x,y); //HECHA

                // Backtracking de las partes izquierdas posibles con las fichas del atril y tamaño indicado
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
                mejorPalabra = computarMejorPalabraDelAtril(dawg.getRoot(),atril,max_long,tablero,x,y);

            }

        }

        // en este caso la parte izquierda NO se computa
        else{
            // Computación únicamente de la parte derecha
            mejorPalabraAncla = computarParteDerechaUnicamente(tablero,dawg,atril,x,y);

        }

        return mejorPalabraAncla;
    }

    /**
     *  Función que devuelve la puntuación de la palabra teniendo en cuenta los modificadores
     * @param palabra
     * @return
     */
    private int obtenerPuntuacion(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra) {
        int puntuacion = 0;

        return puntuacion;
    }

    /**
     *
     * @param tablero
     */
    private void transponerTablero(List<List<SimpleEntry<SimpleEntry<String, TipoModificador>, Set>>> tablero) {
        if (tablero.isEmpty()) return;

        List<List<SimpleEntry<SimpleEntry<String,TipoModificador>, Set>>> transpuesta = new ArrayList<>();
        int files = tablero.size();
        int columnes = tablero.get(0).size();
        for (int c = 0; c < columnes; ++c) {
            List<SimpleEntry<SimpleEntry<String, TipoModificador>, Set>> fila = new ArrayList<>();
            for (int f = 0; f < files; ++f)
            {
                fila.add(tablero.get(f).get(c));
            }
            transpuesta.add(fila);
        }
        tablero.clear();
        tablero.addAll(transpuesta);

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
    private boolean esPalabraValida(List<List<SimpleEntry<SimpleEntry<String,TipoModificador>, Set>>> tablero, int fila, int columna, String letra, Dawg dawg)
    {
        int filaIni = fila;
        while (fila > 0 && tablero[fila][columna].getKey().getKey() != null) {
            --fila;
        }
        StringBuilder paraula = new StringBuilder(); // se podria hacer con strings pero si la palabra es larga es mas ineficiente ya que cada vez crea un nuevo string
        while (fila < tablero.size() && (tablero[fila][columna].getKey().getKey() != null || fila == filaIni)) {
            if (fila != filaIni)
                paraula.append(tablero[fila][columna].getKey().getKey());
            else
                paraula.append(letra);
            ++fila;
        }
        return dawg.existePalabra(paraula.toString());
    }

    /**
     *
     * @param dawg
     * @param tablero
     * @param atril
     */
    private void computarCrossChecks(Dawg dawg, Tablero tablero, String[] atril)
    {
        for (int f = 0; f < tablero.size(); ++f) {
            for (int c = 0; c < tablero.get(0).size(); ++c) {

                if (tablero[f][c].getKey().getKey().equals(".")) {
                    tablero[f][c].getValue().clear();
                    for (String letra : atril) {
                        if (esPalabraValida(tablero, f, c, letra, dawg))
                            tablero[f][c].getValue().add(letra);
                    }
                }
            }
        }
    }

    /**
     *
     * @param tablero
     * @return
     */
    private List<SimpleEntry<Integer, Integer>> computarAnclas(Tablero tablero)
    {
        List<SimpleEntry<Integer, Integer>> listaAnchors = new ArrayList<>() ;
        for (int f = 0; f < tablero.size(); ++f)
            for (int c = 0; c < tablero.get(0).size(); ++c) {
                if (tablero[f][c].getKey().getKey() == null && tieneAdyacentes(tablero, f, c)) {
                    listaAnchors.add(new SimpleEntry<>(f, c));
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
    private boolean tieneAdyacentes(Tablero tablero, int fila, int columna)
    {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions) {
            int newFila = direction[0] + fila;
            int newColumna = direction[1] + columna;
            if (casillaCorrecta(newFila, newColumna) && tablero[newFila][newColumna].getKey().getKey() != null)
                return true;
        }
        return false;
    }

    /**
     *  Función que va a computar todas las partes izquierdas posibles con las letras del artril y por cada una va a buscar la mejor parte derecha,
     *  hasta finalmente encontrar la mejor parte izquierda con su mejor parte derecha
     * @param dawg
     * @param atril
     * @param longitud
     * @param tablero
     * @return
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarMejorPalabraDelAtril(NodoDawg root, String[] atril, int longitud, Tablero tablero, int x, int y) {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
        boolean[] usados = new boolean[atril.length];

        computarMejorPalabraDelAtrilAux(root,atril,usados,longitud,new ArrayList<>(),mejorPalabra,tablero,x,y);

        return mejorPalabra;
    }

    /**
     * Función auxiliar a computarPartesIzquierdasDelAtril que devuelve todas las partes izquierdas con letras del atril posibles
     * @param dawg
     * @param nodo
     * @param atril
     * @param usados
     * @param restantes
     * @param camino
     * @param mejorPalabra
     */
    // añadir funcion extender derecha para comparar
    // ALOMEJOR NO HACE FALTA PASAR EL DAWG
    private void computarMejorPalabraDelAtrilAux(NodoDawg nodo, String[] atril, boolean[] usados, int restantes, List<SimpleEntry<String, Boolean>> camino, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, Tablero tablero, int x, int y) {
        if(!camino.isEmpty()) {
            List<SimpleEntry<String, Boolean>> caminoAux = camino;

            List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxConPosiciones = new ArrayList<>();
            caminoAuxConPosiciones = asignarPosiciones(caminoAux,caminoAux.size(),x,y);

            // extenderParteDerecha devuelve CaminoAuxConPosiciones entero y su puntuacion
            int puntuacionCamino = extenderParteDerecha(tablero,caminoAuxConPosiciones,atril,usados,nodo,x,y);


            // Si mejorPalabra no esta asignada, se le asigna la primera palabra que llegue
            if(mejorPalabra.isEmpty()) {
                mejorPalabra.addAll(caminoAuxConPosiciones);;
            }

            // Si mejorPalabra ya esta asignada, comparar valores y asignar la palabra de mayor puntuación
            else {
                if(puntuacionCamino > obtenerPuntuacion(tablero,mejorPalabra)) {
                    mejorPalabra.clear();
                    mejorPalabra.addAll(caminoAuxConPosiciones);
                }
            }


            // mejorPalabra.add(new ArrayList<>(camino));
            // crear nueva lista de simple entrys (palabra) asignandole camino para extender esta variable y no camino
            // extenderemos parte derecha (tablero, usados, palabra (nueva lista), atril, nodo)
            // comparamos puntuacion mejor palabra con la nueva "mejor" palabra y en caso de ser mayor pues mejorPalabra = nuevaPalabra

        }
        if(restantes == 0) return;

        for(int i = 0; i < atril.length; i++) {
            if(!usados[i]) {
                String letra = atril[i];
                NodoDawg siguiente = nodo.getHijos().get(letra);

                if(siguiente != null) {
                    usados[i] = true;
                    camino.add(new SimpleEntry<>(letra, true));
                    computarMejorPalabraDelAtrilAux(siguiente,atril,usados,restantes-1,camino,mejorPalabra,tablero,x,y);
                    camino.remove(camino.size() - 1);
                    usados[i] = false;
                }
            }
        }
    }

    /**
     * Función para extender cada parte izquierda obtenida
     * @param tablero
     * @param caminoAuxConPosiciones
     * @param atril
     * @param usados
     * @param nodo
     * @param x
     * @param y
     * @return
     */
    private int extenderParteDerecha(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxConPosiciones, String[] atril, boolean[] usados, NodoDawg nodo, int x, int y) {

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();

        int puntuacion = extenderParteDerechaAux(tablero,caminoAuxConPosiciones,atril,usados,nodo, mejorPalabra, x, y, 0);

        return puntuacion;
    }

    
    private int extenderParteDerechaAux(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxPos, String[] atril, boolean[] usados,  NodoDawg nodo, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, int x, int y, int puntuacion) {
        // si el nodo es final entonces es una palabra, comprobamos si su puntuacion es mayor que la que mejorPalabra actual y si es así la cambiamos
        int mejorPuntuacion = puntuacion;

        // si no es casilla correcta significa que estamos fuera del tablero y por lo tanto devuelve la mejorPuntuacion
        if (!casillaCorrecta(x,y)) {
            return mejorPuntuacion;
        }

        if (nodo.getEsFinal())
        {
            // lo de mejor palabra vi que era mejor hacerlo de esta forma porque si haces mejorPalabra = caminoAuxPos, lo que haces es "linkarlas" y si caminoAuxPos cambia mejorPalabra también cambia ya que estan linkadas. Asi mejorPalabra guarda lo que tiene caminoAuxPos pero si este cambia mejorPalabra sigue como tendria que estar
            int puntuacionCamino = obtenerPuntuacion(tablero, caminoAuxPos);
            if (puntuacionCamino > mejorPuntuacion)
            {
                mejorPuntuacion = puntuacionCamino;
                mejorPalabra.clear();
                mejorPalabra.addAll(new ArrayList<>(caminoAuxPos));
            }

        }


        // si el tablero con posicion x  y (que hago que sea la posicion en la que estamos de la palabra en construccion) esta vacia probamos todas las letras y lo hacemos recursivamente
        String letraTablero = tablero[x][y].getKey().getKey();
        if (letraTablero == null) {
            for (int i = 0; i < atril.length; i++) {
                if (!usados[i]) {
                    String letra = atril[i];
                    NodoDawg siguiente = nodo.getHijos().get(letra);
                    if (siguiente != null) {
                        usados[i] = true;
                        caminoAuxPos.add(new SimpleEntry<>(new SimpleEntry<>(letra, true), new SimpleEntry<>(x, y)));
                        int puntuacionRec = extenderParteDerechaAux(tablero, caminoAuxPos, atril, usados, siguiente, mejorPalabra, x, y + 1, puntuacion);
                        if (puntuacionRec > mejorPuntuacion) {
                            mejorPuntuacion = puntuacionRec;
                        }
                        caminoAuxPos.remove(caminoAuxPos.size() - 1);
                        usados[i] = false;
                    }
                }
            }
        }
        // en caso de que este ocupada, miramos si el nodo de la palabra que tenemos hasta ahora tiene un hijo con la letra que esta ocupando y si es asi hacemos la llamada recursiva con esta
        // si la casilla se encuentra ocupada
        else {
            // miramos si la parte izquierda puede seguir haciendo una palabra con esa letra
            if (nodo.getHijos().get(letraTablero) != null) {
                caminoAuxPos.add(new SimpleEntry<>(new SimpleEntry<>(tablero[x][y].getKey().getKey(), false), new SimpleEntry<>(x, y)));
                int puntuacionRec = extenderParteDerechaAux(tablero, caminoAuxPos, atril, usados, nodo.getHijos().get(tablero[x][y].getKey().getKey()),mejorPalabra,x,y+1,puntuacion);
                if (puntuacionRec > mejorPuntuacion) {
                    mejorPuntuacion = puntuacionRec;
                }
                caminoAuxPos.remove(caminoAuxPos.size() - 1);
            }
        }

        return mejorPuntuacion;
    }

    /**
     * Función que devuelve la parte izquierda de un ancla cuando ya sabemos que esta está compuesta únicamente por letras ya en el tablero
     * @param tablero
     * @param x (fila del ancla)
     * @param y (columna del ancla)
     * @return
     */
    private List<SimpleEntry<String, Boolean>> computarParteIzquierdaTablero(Tablero tablero, int longitud, int x, int y) {
        List<SimpleEntry<String, Boolean>> parteIzquierda = new ArrayList<>();

        // col se coloca en el principio de la parte izquierda y va retrocediendo hasta su última casilla, justo antes del ancla
        for(int col = y - longitud; col < y; col++) {
            parteIzquierda.add(new SimpleEntry<>(tablero[x][col].getKey().getKey(), false));
        }

        return parteIzquierda;
    }

    /**
     * Función que devuelve únicamente la mejor parte derecha, ya que no es posible tener parte izquierda
     * @param tablero
     * @param dawg
     * @param atril
     * @param x
     * @param y
     * @return
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarParteDerechaUnicamente(Tablero tablero, Dawg dawg, String[] atril, int x, int y) {

    }

    /**
     * Función para saber si una casilla está dentro del tablero o no
     * @param x
     * @param y
     * @return
     */
    private boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < 15 && y >= 0 && y < 15;
    }

    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> asignarPosiciones(List<SimpleEntry<String, Boolean>> palabra, int max_long, int x, int y) {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabraFinal = new ArrayList<>();
        int columna_inicial = y - max_long;

        int size = palabra.size();
        for(int i = 0; i < size; i++) {
            SimpleEntry<String, Boolean> letra = new SimpleEntry<>(palabra.getFirst().getKey(), palabra.getFirst().getValue());
            SimpleEntry<Integer, Integer> posicion = new SimpleEntry<>(x, columna_inicial);
            ++columna_inicial;
            SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> conjunto = new SimpleEntry<>(letra, posicion);
            palabraFinal.add(conjunto);
            palabra.removeFirst();
        }

        return palabraFinal;
    }

    /**
     * Función para saber el tamaño máximo de la parte izquierda cuando la queremos de fichas del atril
     * @param tablero
     * @param x
     * @param y
     * @return
     */
    private int tamañoParteIzquierdaAtril(Tablero tablero, int x, int y) {
        int size = 0;

        // Mientras las casillas a la izquierda sean correctas y no esten ocupadas, sumar uno a size
        for(int col = y - 1; casillaCorrecta(x, col); col--) {
            // Si hay una casilla ya ocupada, no vamos a empezar la palabra directamente a su derecha, sino que vamos a dejar un espacio
            if(tablero[x][col].getKey().getKey() != null) {
                --size;
                return size;
            }
            ++size;
        }

        return size;
    }

    /**
     * Función para saber el tamaño máximo de la parte izquierda cuando la queremos de fichas que ya estan colocadas
     * @param tablero
     * @param x
     * @param y
     * @return
     */
    private int tamañoParteIzquierdaTablero(Tablero tablero, int x, int y) {
        int size = 0;

        // Mientras las casillas a la izquierda sean correctas y esten ocupadas, sumar uno a size
        for(int col = y - 1; tablero[x][col].getKey().getKey() != null && casillaCorrecta(x, col); col--) {
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
