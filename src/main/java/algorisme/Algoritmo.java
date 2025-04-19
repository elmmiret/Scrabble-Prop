package algorisme;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import ctrldomini.*;
import exceptions.CoordenadaFueraDeRangoException;
/**
 * Clase que implementa algoritmos para encontrar el mejor movimiento en un juego de Scrabble.
 * Utiliza estructuras DAWG para validación de palabras y estrategias de backtracking para optimizar búsquedas.
 *
 * @author Albert Aulet Niubó
 * @author Arnau Miret Barrull
 */
public class Algoritmo {
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;
    private static final Set<String> Digrafos = new HashSet<>(Arrays.asList("rr", "ny", "ll", "l·l", "ch"));

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

    /**
     * Calcula la mejor palabra para colocar en el tablero, considerando direcciones horizontales y verticales.
     * @param dawg Estructura DAWG para la comprobación de palabras
     * @param tablero Estructura del tablero actual
     * @param atril Fichas disponibles en el atril del jugador
     * @return Lista de entradas con la palabra, si es del atril y su posición.
     * @throws CoordenadaFueraDeRangoException Si se accede a una posición fuera del tablero
     * @author Albert Aulet Niubó
     * @author Arnau Miret Barrull
     */
    public List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorMovimiento(Dawg dawg, Tablero tablero, String[] atril) throws CoordenadaFueraDeRangoException {

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
            tablero.transponerTablero();
        }

        return mejorPalabra;

    }

    /**
     * Calcula la mejor palabra para una posición ancla específica.
     *
     * @param dawg Estructura DAWG para validación de palabras.
     * @param tablero Tablero actual del juego.
     * @param ancla Posición ancla (x, y) donde se inicia la palabra.
     * @param atril Fichas disponibles en el atril del jugador.
     * @return Lista de entradas con la mejor palabra para el ancla.
     * @throws CoordenadaFueraDeRangoException Si la posición ancla es inválida.
     * @author Arnau Miret Barrull
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarPalabraAncla(Dawg dawg, Tablero tablero, SimpleEntry<Integer, Integer> ancla, String[] atril) throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabraAncla = new ArrayList<>();

        //backtracking

        // Si hay casilla a la izquierda del ancla, se computa la parte izquierda y luego la derecha
        int x = ancla.getKey();
        int y = ancla.getValue();

        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x,y);

        // Si la casilla a la izquierda del ancla queda fuera de los límites, la parte izquierda no se computa

        // en este caso la parte izquierda se computa
        if(casillaCorrecta(x, y - 1)) {
            // Mirar si la casilla a la izquierda del ancla es vacía o no
            // Si la casilla esta ocupada (parte izq. compuesta de letras ya en el tablero)
            if(tablero.getFicha(x,y-1).getLetra() != null) {

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

            List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
            boolean[] usados = new boolean[atril.length];

            int puntuacion = extenderParteDerecha(tablero,mejorPalabra,atril,usados,dawg.getRoot(),x,y);

            // Computación únicamente de la parte derecha
            //mejorPalabraAncla = computarParteDerechaUnicamente(tablero,dawg,atril,x,y);

        }

        return mejorPalabraAncla;
    }

    /**
     * Calcula la puntuación de una palabra considerando modificadores del tablero.
     *
     * @param tablero Tablero actual del juego.
     * @param palabra Lista de entradas que representan la palabra y sus posiciones.
     * @return Puntuación total de la palabra, incluyendo bonificaciones.
     * @author Albert Aulet Niubó
     */
    private int obtenerPuntuacion(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra) throws CoordenadaFueraDeRangoException {
        int puntuacion = 0;
        int puntuacion_vertical = 0;
        int multiplicadorPalabra = 1;
        int fichasAtril = 0;
        for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> letra_i : palabra) {
            String letra = letra_i.getKey().getKey();
            boolean esDelAtril = letra_i.getKey().getValue();
            int pos_x = letra_i.getValue().getKey();
            int pos_y = letra_i.getValue().getValue();
            int valor_letra = tablero.getFicha(pos_x, pos_y).getPuntuacion();
            if (esDelAtril) {
                Tablero.TipoModificador mod = tablero.getTipoModificador(pos_x, pos_y);
                switch (mod) {
                    case dobleTantoDeLetra:
                        valor_letra *= 2;
                        break;
                    case tripleTantoDeLetra:
                        valor_letra *= 3;
                        break;
                    case dobleTantoDePalabra:
                        multiplicadorPalabra *= 2;
                        break;
                    case tripleTantoDePalabra:
                        multiplicadorPalabra *= 3;
                        break;
                }
                puntuacion_vertical += obtenerPuntuacionPalabraVertical(tablero, pos_x, pos_y);
                fichasAtril += 1;
            }
            puntuacion += valor_letra;
        }
        puntuacion *= multiplicadorPalabra;
        if (fichasAtril == 7) {
            puntuacion += 50;
        }

        return puntuacion + puntuacion_vertical;
    }

    /**
     * Calcula la puntuación de una palabra vertical formada en una posición específica del tablero.
     * Considera modificadores de letra/palabra y si las fichas pertenecen al atril del jugador.
     *
     * @param tablero Tablero actual del juego.
     * @param x Fila inicial de la posición a evaluar.
     * @param y Columna de la posición a evaluar.
     * @return Puntuación total de la palabra vertical, aplicando modificadores relevantes.
     * @author Albert Aulet Niubó
     */
    int obtenerPuntuacionPalabraVertical(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra = new ArrayList<>();
        int pos_ini = x;
        while (casillaCorrecta(x, y) && tablero.getFicha(x, y).getLetra() != null) {
            --x;
        }
        while(casillaCorrecta(x, y) && tablero.getFicha(x, y).getLetra() != null) {
            SimpleEntry<String, Boolean> letra;
            if (pos_ini == x) {
                letra = new SimpleEntry<>(tablero.getFicha(x, y).getLetra(), true);
            }
            else {
                letra = new SimpleEntry<>(tablero.getFicha(x, y).getLetra(), false);
            }
            SimpleEntry<Integer, Integer> posicion = new SimpleEntry<>(x, y);
            palabra.add(new SimpleEntry<>(letra, posicion));
            ++x;
        }

        if (palabra == null) return 0;

        int puntuacion = 0;
        int multiplicadorPalabra = 1;
        for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> letra_i : palabra) {
            String letra = letra_i.getKey().getKey();
            boolean esDelAtril = letra_i.getKey().getValue();
            int pos_x = letra_i.getValue().getKey();
            int pos_y = letra_i.getValue().getValue();
            int valor_letra = tablero.getFicha(pos_x, pos_y).getPuntuacion();
            if (esDelAtril) {
                Tablero.TipoModificador mod = tablero.getTipoModificador(pos_x, pos_y);
                switch (mod) {
                    case dobleTantoDeLetra:
                        valor_letra *= 2;
                        break;
                    case tripleTantoDeLetra:
                        valor_letra *= 3;
                        break;
                    case dobleTantoDePalabra:
                        multiplicadorPalabra *= 2;
                        break;
                    case tripleTantoDePalabra:
                        multiplicadorPalabra *= 3;
                        break;
                }
            }
            puntuacion += valor_letra;
        }
        return puntuacion*multiplicadorPalabra;
    }

    /**
     * Valida si una palabra formada en una posición es aceptada por el DAWG.
     *
     * @param tablero Tablero actual del juego.
     * @param fila Fila de la posición inicial.
     * @param columna Columna de la posición inicial.
     * @param letra Letra a colocar.
     * @param dawg Estructura DAWG para validación.
     * @return true si la palabra es válida, false en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si la posición es inválida.
     * @author Albert Aulet Niubó
     */
    private boolean esPalabraValida(Tablero tablero, int fila, int columna, String letra, Dawg dawg) throws CoordenadaFueraDeRangoException {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) throw new CoordenadaFueraDeRangoException(fila, columna);
        int filaIni = fila;
        while (fila > 0 && tablero.getFicha(fila,columna).getLetra() != null) {
            --fila;
        }
        StringBuilder paraula = new StringBuilder(); // se podria hacer con strings pero si la palabra es larga es mas ineficiente ya que cada vez crea un nuevo string
        while (fila < FILAS && (tablero.getFicha(fila,columna).getLetra() != null || fila == filaIni)) {
            if (fila != filaIni)
                paraula.append(tablero.getFicha(fila,columna).getLetra());
            else
                paraula.append(letra);
            ++fila;
        }
        return dawg.existePalabra(paraula.toString());
    }

    /**
    * Calcula los crosschecks válidos para cada posición vacía del tablero.
    *
    * @param dawg Estructura DAWG para validación.
    * @param tablero Tablero actual del juego.
    * @param atril Fichas disponibles en el atril.
    * @throws CoordenadaFueraDeRangoException Si se accede a una posición inválida.
     * @author Albert Aulet Niubó
    */
    private void computarCrossChecks(Dawg dawg, Tablero tablero, String[] atril) throws CoordenadaFueraDeRangoException {
        for (int f = 0; f < FILAS; ++f) {
            for (int c = 0; c < COLUMNAS; ++c) {

                if (tablero.getFicha(f,c).getLetra() == null) {
                    tablero.clearAbecedario(f,c);
                    for (String letra : atril) {
                        if (esPalabraValida(tablero,f,c,letra,dawg))
                            tablero.setLetraAbecedario(letra,f,c);
                    }
                }
            }
        }
    }

    /**
     * Encuentra todas las posiciones ancla en el tablero (casillas vacías adyacentes a letras).
     *
     * @param tablero Tablero actual del juego.
     * @return Lista de posiciones (x, y) que son anclas.
     * @throws CoordenadaFueraDeRangoException Si se accede a una posición inválida.
     * @author Albert Aulet Niubó
     */
    private List<SimpleEntry<Integer, Integer>> computarAnclas(Tablero tablero) throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<Integer, Integer>> listaAnchors = new ArrayList<>() ;
        for (int f = 0; f < FILAS; ++f)
            for (int c = 0; c < COLUMNAS; ++c) {
                if (tablero.getFicha(f,c).getLetra() == null && tieneAdyacentes(tablero, f, c)) {
                    listaAnchors.add(new SimpleEntry<>(f, c));
                }
            }
        return listaAnchors;
    }

    /**
    * Verifica si una posición tiene casillas adyacentes ocupadas.
    *
    * @param tablero Tablero actual del juego.
    * @param fila Fila de la posición a verificar.
    * @param columna Columna de la posición a verificar.
    * @return true si hay al menos una casilla adyacente ocupada.
    * @throws CoordenadaFueraDeRangoException Si la posición es inválida.
     * @author Albert Aulet Niubó
    */
    private boolean tieneAdyacentes(Tablero tablero, int fila, int columna) throws CoordenadaFueraDeRangoException {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) throw new CoordenadaFueraDeRangoException(fila, columna);
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] direction : directions) {
            int newFila = direction[0] + fila;
            int newColumna = direction[1] + columna;
            if (casillaCorrecta(newFila, newColumna) && tablero.getFicha(newFila,newColumna).getLetra() != null)
                return true;
        }
        return false;
    }

    /**
     * Calcula la mejor combinación de letras del atril para formar una palabra válida en una posición ancla del tablero.
     * Utiliza backtracking para explorar todas las combinaciones posibles dentro de la longitud máxima permitida.
     *
     * @param root Nodo raíz del DAWG para validación de prefijos.
     * @param atril Array de letras disponibles en el atril del jugador.
     * @param longitud Longitud máxima permitida para la parte izquierda de la palabra.
     * @param tablero Tablero actual del juego.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @return Lista de entradas que representan la mejor palabra encontrada, con letras, origen (atril/tablero) y posiciones.
     * @throws CoordenadaFueraDeRangoException Si las coordenadas (x, y) están fuera del rango del tablero.
     * @author Arnau Miret Barrull
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarMejorPalabraDelAtril(NodoDawg root, String[] atril, int longitud, Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
        boolean[] usados = new boolean[atril.length];

        computarMejorPalabraDelAtrilAux(root,atril,usados,longitud,new ArrayList<>(),mejorPalabra,tablero,x,y);

        return mejorPalabra;
    }

    /**
     * Función auxiliar recursiva que genera todas las combinaciones válidas de letras del atril para la parte izquierda de la palabra.
     * Actualiza la mejor palabra encontrada comparando puntuaciones.
     *
     * @param nodo Nodo actual del DAWG durante la exploración.
     * @param atril Letras disponibles en el atril.
     * @param usados Array que indica qué letras del atril han sido usadas.
     * @param restantes Número de letras restantes por colocar en la parte izquierda.
     * @param camino Lista temporal con las letras y su origen (atril/tablero) en la combinación actual.
     * @param mejorPalabra Referencia a la lista que almacena la mejor palabra encontrada.
     * @param tablero Tablero actual del juego.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @throws CoordenadaFueraDeRangoException Si las coordenadas (x, y) son inválidas.
     * @author Arnau Miret Barrull
     */
    private void computarMejorPalabraDelAtrilAux(NodoDawg nodo, String[] atril, boolean[] usados, int restantes, List<SimpleEntry<String, Boolean>> camino, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
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
     * Extiende una palabra hacia la derecha desde una posición dada, validando combinaciones con el DAWG y calculando puntuaciones.
     *
     * @param tablero Tablero actual del juego.
     * @param caminoAuxConPosiciones Lista temporal con letras y posiciones de la palabra en construcción.
     * @param atril Letras disponibles en el atril.
     * @param usados Array que indica qué letras del atril han sido usadas.
     * @param nodo Nodo actual del DAWG durante la extensión.
     * @param x Fila actual durante la extensión.
     * @param y Columna actual durante la extensión.
     * @return Puntuación máxima obtenida durante la extensión.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Albert Aulet Niubó
     */
    private int extenderParteDerecha(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxConPosiciones, String[] atril, boolean[] usados, NodoDawg nodo, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        int puntuacion = 0;

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();

        int pt = extenderParteDerechaAux(tablero,caminoAuxConPosiciones,atril,usados,nodo, mejorPalabra, x, y, 0);

        return pt;
    }

    /**
     * Función auxiliar recursiva para extender una palabra hacia la derecha, evaluando letras del atril y existentes en el tablero.
     *
     * @param tablero Tablero actual del juego.
     * @param caminoAuxPos Lista temporal con letras y posiciones de la palabra en construcción.
     * @param atril Letras disponibles en el atril.
     * @param usados Array que indica qué letras del atril han sido usadas.
     * @param nodo Nodo actual del DAWG durante la extensión.
     * @param mejorPalabra Referencia a la mejor palabra encontrada durante la extensión.
     * @param x Fila actual durante la extensión.
     * @param y Columna actual durante la extensión.
     * @param puntuacion Puntuación acumulada hasta el momento.
     * @return Puntuación máxima obtenida en esta rama de exploración.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Albert Aulet Niubó
     */
    private int extenderParteDerechaAux(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxPos, String[] atril, boolean[] usados,  NodoDawg nodo, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, int x, int y, int puntuacion) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
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
        String letraTablero = tablero.getFicha(x,y).getLetra();
        if (letraTablero == null) {
            for (int i = 0; i < atril.length; i++) {
                if (!usados[i] && tablero.getAbecedario(x,y).contains(atril[i]) /*comprovar si esta en el cross check*/) {
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
                caminoAuxPos.add(new SimpleEntry<>(new SimpleEntry<>(tablero.getFicha(x,y).getLetra(), false), new SimpleEntry<>(x, y)));
                int puntuacionRec = extenderParteDerechaAux(tablero, caminoAuxPos, atril, usados, nodo.getHijos().get(tablero.getFicha(x,y).getLetra()),mejorPalabra,x,y+1,puntuacion);
                if (puntuacionRec > mejorPuntuacion) {
                    mejorPuntuacion = puntuacionRec;
                }
                caminoAuxPos.remove(caminoAuxPos.size() - 1);
            }
        }

        return mejorPuntuacion;
    }

    /**
     * Recupera las letras ya colocadas en el tablero a la izquierda de una posición ancla.
     *
     * @param tablero Tablero actual del juego.
     * @param longitud Longitud máxima de la parte izquierda a recuperar.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @return Lista de letras existentes a la izquierda del ancla, indicando que no son del atril.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Arnau Miret Barrull
     */
    private List<SimpleEntry<String, Boolean>> computarParteIzquierdaTablero(Tablero tablero, int longitud, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        List<SimpleEntry<String, Boolean>> parteIzquierda = new ArrayList<>();

        // col se coloca en el principio de la parte izquierda y va retrocediendo hasta su última casilla, justo antes del ancla
        for(int col = y - longitud; col < y; col++) {
            parteIzquierda.add(new SimpleEntry<>(tablero.getFicha(x,col).getLetra(), false));
        }

        return parteIzquierda;
    }

    /**
     * Verifica si una coordenada (x, y) está dentro de los límites del tablero.
     *
     * @param x Fila a verificar.
     * @param y Columna a verificar.
     * @return true si (x, y) es una posición válida, false en caso contrario.
     * @author Arnau Miret Barrull
     */
    private boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < FILAS && y >= 0 && y < COLUMNAS;
    }


    /**
     * Asigna posiciones del tablero a las letras de una palabra generada con el atril.
     *
     * @param palabra Lista de letras y su origen (atril/tablero).
     * @param max_long Longitud de la parte izquierda de la palabra.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @return Lista de entradas con letras, origen y posiciones asignadas.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Arnau Miret Barrull
     */
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> asignarPosiciones(List<SimpleEntry<String, Boolean>> palabra, int max_long, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
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
     * Calcula la longitud máxima posible para la parte izquierda de una palabra usando solo letras del atril.
     *
     * @param tablero Tablero actual del juego.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @return Longitud máxima permitida para la parte izquierda con letras del atril.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Arnau Miret Barrull
     */
    private int tamañoParteIzquierdaAtril(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        int size = 0;

        // Mientras las casillas a la izquierda sean correctas y no esten ocupadas, sumar uno a size
        for(int col = y - 1; casillaCorrecta(x, col); col--) {
            // Si hay una casilla ya ocupada, no vamos a empezar la palabra directamente a su derecha, sino que vamos a dejar un espacio
            if(tablero.getFicha(x,col).getLetra() != null) {
                --size;
                return size;
            }
            ++size;
        }

        return size;
    }

    /**
     * Calcula la longitud máxima posible para la parte izquierda de una palabra usando letras existentes en el tablero.
     *
     * @param tablero Tablero actual del juego.
     * @param x Fila de la posición ancla.
     * @param y Columna de la posición ancla.
     * @return Longitud máxima de letras consecutivas existentes a la izquierda del ancla.
     * @throws CoordenadaFueraDeRangoException Si (x, y) está fuera de rango.
     * @author Arnau Miret Barrull
     */
    private int tamañoParteIzquierdaTablero(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        int size = 0;

        // Mientras las casillas a la izquierda sean correctas y esten ocupadas, sumar uno a size
        for(int col = y - 1; tablero.getFicha(x,col).getLetra() != null && casillaCorrecta(x, col); col--) {
            ++size;
        }

        return size;
    }
}
