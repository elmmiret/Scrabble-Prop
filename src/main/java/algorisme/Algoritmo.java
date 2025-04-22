package algorisme;

import ctrldomini.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
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
    private Partida partida;

    public Algoritmo(Partida partida)
    {
        this.partida = partida;
    }


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
        boolean estaTranspuesto = false;

        // Probar orientación horizontal y vertical (transponiendo el tablero)
        for(int i = 0; i < 2; i++) {
            // Calculamos cross-checks para restringir letras posibles
            computarCrossChecks(dawg, tablero, atril);

            // Obtenemos posiciones de anclas
            List<SimpleEntry<Integer, Integer>> anclas = computarAnclas(tablero);

            System.out.println("Orientación: " + (estaTranspuesto ? "Vertical" : "Horizontal"));
            System.out.println("Número de anclas: " + anclas.size());

            // Evaluamos cada posición de ancla
            for (SimpleEntry<Integer, Integer> ancla : anclas) {
                int x = ancla.getKey();
                int y = ancla.getValue();
                if (estaTranspuesto) {
                    // Intercambiamos coordenadas si el tablero está transpuesto
                    x = ancla.getValue();
                    y = ancla.getKey();
                }
                System.out.printf("Evaluando ancla: %d,%d\n", x, y);
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabraAncla =
                        computarPalabraAncla(dawg, tablero, new SimpleEntry<>(x, y), atril, estaTranspuesto);
                if (!palabraAncla.isEmpty()) {
                    int puntuacion = obtenerPuntuacion(tablero, palabraAncla);
                    System.out.printf("Palabra encontrada con puntuación: %d\n", puntuacion);

                    if (puntuacion > mejorPuntuacion) {
                        mejorPuntuacion = puntuacion;
                        mejorPalabra = new ArrayList<>(palabraAncla);
                        System.out.println("¡Nueva mejor palabra encontrada!");
                    }
                }
            }

            // Transponemos el tablero para la siguiente iteración
            tablero.transponerTablero();
            estaTranspuesto = true;
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
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarPalabraAncla(Dawg dawg, Tablero tablero, SimpleEntry<Integer, Integer> ancla, String[] atril, boolean estaTranspuesto) throws CoordenadaFueraDeRangoException {
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
            if(tablero.getFicha(x,y-1) != null) {

                List<SimpleEntry<String, Boolean>> parteIzquierda = new ArrayList<>();


                // Computar la parte izquierda con las casillas del tablero
                int max_long = tamañoParteIzquierdaTablero(tablero,x,y);

                // Funcion que devuelve la parte izquierda ya en el tablero
                parteIzquierda = computarParteIzquierdaTablero(tablero,max_long,x,y);

                // Asignamos las posiciones del tablero a la parte izquierda
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
                mejorPalabra = asignarPosiciones(parteIzquierda,max_long,x,y, estaTranspuesto);

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
                int puntuacion = extenderParteDerecha(tablero,mejorPalabra,atril,usados,nodo,x,y, dawg);

                mejorPalabraAncla = mejorPalabra;

            }

            // Si la casilla no esta ocupada (parte izq. compuesta de letras del atril)
            else {

                // Funcion para saber la logitud maxima de la parte izquierda
                int max_long = tamañoParteIzquierdaAtril(tablero,x,y); //HECHA

                // Backtracking de las partes izquierdas posibles con las fichas del atril y tamaño indicado
                List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
                mejorPalabra = computarMejorPalabraDelAtril(dawg.getRoot(),atril,max_long,tablero,x,y, dawg, estaTranspuesto);

                mejorPalabraAncla = mejorPalabra;
            }

        }

        // en este caso la parte izquierda NO se computa
        else{

            List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
            boolean[] usados = new boolean[atril.length];

            int puntuacion = extenderParteDerecha(tablero,mejorPalabra,atril,usados,dawg.getRoot(),x,y, dawg);

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
    public int obtenerPuntuacion(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabra)
            throws CoordenadaFueraDeRangoException {

        int puntuacion = 0;
        int puntuacion_vertical = 0;
        int multiplicadorPalabra = 1;
        int fichasAtril = 0;

        if (palabra == null || palabra.isEmpty()) {
            return 0; // Si la palabra es nula o vacía entonces devolvemos 0
        }

        for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> letra_i : palabra) {
            if (letra_i == null || letra_i.getKey() == null) {
                continue; // saltamos elementos nulos (auqneu teoricamente no deberia ser null
            }

            String letra = letra_i.getKey().getKey();
            if (letra == null) {
                continue; // idem que arriba
            }
            // obtenemos la posición donde teóricamente estaría esa letra en el tablero si colocáramos la palabra
            boolean esDelAtril = letra_i.getKey().getValue();
            int pos_x = letra_i.getValue().getKey();
            int pos_y = letra_i.getValue().getValue();

            int valor_letra = 0;
            try {
                valor_letra = partida.getPuntuacionFicha(letra);
            } catch (Exception e) {
                System.out.println("Error al obtener puntuación para: " + letra);
                continue; // Continuar con la siguiente letra si hay error
            }
            // en caso de ser del atril, miramos si hay modificador ya que le afectaría
            if (esDelAtril) {
                Tablero.TipoModificador mod = tablero.getTipoModificador(pos_x, pos_y);
                if (mod != null) {
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

                try {
                    // miramos si esa letra también forma alguna palabra vertical y de ser así calculamos su puntuación
                    puntuacion_vertical += obtenerPuntuacionPalabraVertical(tablero, pos_x, pos_y);
                } catch (Exception e) {
                    System.out.println("Error al calcular puntuación vertical en: " + pos_x + "," + pos_y);
                }

                fichasAtril++;
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
    public int obtenerPuntuacionPalabraVertical(Tablero tablero, int x, int y)
            throws CoordenadaFueraDeRangoException {

        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS)
            throw new CoordenadaFueraDeRangoException(x, y);

        // Si no hay letra en esta posición o en posiciones adyacentes verticales entonces significa que no hay palabras verticales
        if ((tablero.getFicha(x, y) == null) && (x <= 0 || tablero.getFicha(x-1, y) == null) && (x >= FILAS-1 || tablero.getFicha(x+1, y) == null)) {
            return 0;
        }

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabraVertical = new ArrayList<>();
        int filaInicio = x;

        // Encontramos el inicio de la palabra vertical
        while (filaInicio > 0 && tablero.getFicha(filaInicio-1, y) != null) {
            filaInicio--;
        }

        int filaActual = filaInicio;
        boolean incluyeNuevaLetra = false;
        String letraActual = "?"; // Placeholder para la letra que estamos probando

        // Construimos la palabra vertical
        while (filaActual < FILAS) {
            Ficha ficha = tablero.getFicha(filaActual, y);

            if (ficha == null && filaActual != x) {
                break; // Fin de la palabra
            }

            boolean esNuevaLetra = (filaActual == x && tablero.getFicha(filaActual, y) == null);
            String letra;

            if (esNuevaLetra) {
                letra = letraActual; // Usar la letra que estamos considerando
                incluyeNuevaLetra = true;
            } else if (ficha != null) {
                letra = ficha.getLetra();
            } else {
                break;
            }

            palabraVertical.add(new SimpleEntry<>(new SimpleEntry<>(letra, esNuevaLetra), new SimpleEntry<>(filaActual, y)));

            filaActual++;
        }

        // Si la palabra solo tiene una letra o no incluye la nueva letra, no es válida
        if (palabraVertical.size() <= 1 || !incluyeNuevaLetra) {
            return 0;
        }
        // una vez construida la palabra obtenemos su puntuacion
        try {
            return obtenerPuntuacion(tablero, palabraVertical);
        } catch (Exception e) {
            System.out.println("Error al calcular puntuación de palabra vertical");
            return 0;
        }
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
    public boolean esPalabraValida(Tablero tablero, int fila, int columna, String letra, Dawg dawg) throws CoordenadaFueraDeRangoException {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) throw new CoordenadaFueraDeRangoException(fila, columna);
        int filaIni = fila;
        while (fila > 0 && tablero.getFicha(fila,columna) != null) {
            --fila;
        }
        StringBuilder paraula = new StringBuilder(); // se podria hacer con strings pero si la palabra es larga es mas ineficiente ya que cada vez crea un nuevo string
        while (fila < FILAS && (tablero.getFicha(fila,columna) != null || fila == filaIni)) {
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
    public void computarCrossChecks(Dawg dawg, Tablero tablero, String[] atril) throws CoordenadaFueraDeRangoException {
        for (int f = 0; f < FILAS; ++f) {
            for (int c = 0; c < COLUMNAS; ++c) {
                if (tablero.getFicha(f, c) == null) {
                    tablero.clearAbecedario(f, c);

                    // Si hay fichas adyacentes en vertical u horizontal, necesitamos validar
                    boolean tieneAdyacenteVertical = (casillaCorrecta(f-1, c) && tablero.getFicha(f-1, c) != null) || (casillaCorrecta(f+1, c) && tablero.getFicha(f+1, c) != null);
                    boolean tieneAdyacenteHorizontal = (casillaCorrecta(f, c-1) && tablero.getFicha(f, c-1) != null) || (casillaCorrecta(f, c+1) && tablero.getFicha(f, c+1) != null);

                    if (tieneAdyacenteVertical || tieneAdyacenteHorizontal) {
                        for (String letra : atril) {
                            boolean palabraHorizontalValida = true;
                            boolean palabraVerticalValida = true;

                            // Validar palabra horizontal
                            if (tieneAdyacenteHorizontal) {
                                palabraHorizontalValida = esPalabraValida(tablero, f, c, letra, dawg);
                            }

                            // Validar palabra vertical
                            if (tieneAdyacenteVertical) {
                                palabraVerticalValida = esPalabraValidaVertical(tablero, f, c, letra, dawg);
                            }

                            // Si ambas direcciones son válidas entonces se añade la letra al set de crossChecks de esa casilla del tablero (o si solo necesitamos validar una)
                            if (palabraHorizontalValida && palabraVerticalValida) {
                                tablero.setLetraAbecedario(letra, f, c);
                            }
                        }
                    } else {
                        // Si no hay adyacentes, todas las letras son válidas
                        for (String letra : atril) {
                            tablero.setLetraAbecedario(letra, f, c);
                        }
                    }
                }
            }
        }
    }

    // Método adicional para validar palabras verticales
    private boolean esPalabraValidaVertical(Tablero tablero, int fila, int columna, String letra, Dawg dawg)
            throws CoordenadaFueraDeRangoException {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS)
            throw new CoordenadaFueraDeRangoException(fila, columna);

        int filaInicial = fila;
        // Encontramos el inicio de la palabra vertical
        while (fila > 0 && tablero.getFicha(fila-1, columna) != null) {
            --fila;
        }

        StringBuilder palabra = new StringBuilder();
        // Construimos la palabra vertical
        while (fila < FILAS && (tablero.getFicha(fila, columna) != null || fila == filaInicial)) {
            if (fila != filaInicial)
                palabra.append(tablero.getFicha(fila, columna).getLetra());
            else
                palabra.append(letra);
            ++fila;
        }

        // Si la palabra es de longitud 1, no necesita validación
        if (palabra.length() <= 1) return true;

        // en caso contrario miramos si existe en el dawg
        return dawg.existePalabra(palabra.toString());
    }

    /**
     * Encuentra todas las posiciones ancla en el tablero (casillas vacías adyacentes a letras).
     *
     * @param tablero Tablero actual del juego.
     * @return Lista de posiciones (x, y) que son anclas.
     * @throws CoordenadaFueraDeRangoException Si se accede a una posición inválida.
     * @author Albert Aulet Niubó
     */
    public List<SimpleEntry<Integer, Integer>> computarAnclas(Tablero tablero) throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<Integer, Integer>> listaAnchors = new ArrayList<>();
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < COLUMNAS; c++) {
                // si la casilla es null entonces miramos si tiene casillas adyacentes ya que entonces sera ancla, tambien se añade el centro ya que para la ia, en caso de ser primer turno, necesitara una ancla para empezar
                if (tablero.getFicha(f, c) == null && (tieneAdyacentes(tablero, f, c) || (f == 7 && c == 7))) { // Incluir centro si está vacío
                    listaAnchors.add(new SimpleEntry<>(f, c));
                }
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
    public boolean tieneAdyacentes(Tablero tablero, int fila, int columna) throws CoordenadaFueraDeRangoException {
        if (fila < 0 || fila >= FILAS || columna < 0 || columna >= COLUMNAS) {
            throw new CoordenadaFueraDeRangoException(fila, columna);
        }

        // Comprobamos solo si tiene adyacente a la derecha ya que solo queremos anclas izquierdas
        int[] dir = {0, 1};

        int newFila = fila + dir[0];
        int newColumna = columna + dir[1];

        // en caso de que esa posicion este dentro de los limites del tablero y haya ficha en esa casilla signifca que es ancla izquierda
        if (casillaCorrecta(newFila, newColumna) && tablero.getFicha(newFila, newColumna) != null) {
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
    private List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> computarMejorPalabraDelAtril(NodoDawg root, String[] atril, int longitud, Tablero tablero, int x, int y, Dawg dawg, boolean estaTranspuesto) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();
        boolean[] usados = new boolean[atril.length];

        computarMejorPalabraDelAtrilAux(root,atril,usados,longitud,new ArrayList<>(),mejorPalabra,tablero,x,y, dawg, estaTranspuesto);

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
    private void computarMejorPalabraDelAtrilAux(NodoDawg nodo, String[] atril, boolean[] usados, int restantes, List<SimpleEntry<String, Boolean>> camino, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, Tablero tablero, int x, int y, Dawg dawg, boolean estaTranspuesto) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        if(!camino.isEmpty()) {
            List<SimpleEntry<String, Boolean>> caminoAux = new ArrayList<>(camino);

            List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxConPosiciones = new ArrayList<>();
            caminoAuxConPosiciones = asignarPosiciones(caminoAux,caminoAux.size(),x,y, estaTranspuesto);

            // extenderParteDerecha devuelve CaminoAuxConPosiciones extendido entero, que seria la mejor palabra con el caminoAuxConPosiciones actual, y su puntuacion
            int puntuacionCamino = extenderParteDerecha(tablero,caminoAuxConPosiciones,atril,usados,nodo,x,y, dawg);
            StringBuilder sb = new StringBuilder();
            for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> entry : caminoAuxConPosiciones) {
                sb.append(entry.getKey().getKey());
            }

            // Si mejorPalabra no esta asignada, se le asigna la primera palabra que llegue
            if(mejorPalabra.isEmpty()) {
                mejorPalabra.addAll(caminoAuxConPosiciones);;
            }

            // Si mejorPalabra ya esta asignada, comparar valores y asigna la palabra de mayor puntuación
            else {
                if(puntuacionCamino > obtenerPuntuacion(tablero,mejorPalabra)) {
                    mejorPalabra.clear();
                    mejorPalabra.addAll(caminoAuxConPosiciones);
                }
            }

        }

        // si restantes es 0 entonces no tenemos espacio para poner fichas y hace return
        if(restantes == 0) return;

        for(int i = 0; i < atril.length; i++) {
            if(!usados[i]) {
                String letra = atril[i];
                // miramos que el nodo actual tenga como un hijo la letra en la que estamos en el for, ya que de ser así significaría que existe prefijo/palabra con esa combinación
                NodoDawg siguiente = nodo.getHijos().get(letra);
                // comprobamos que realmente tiene prefijos ya que nos daba error solo con el nodo.getHijos
                if(siguiente != null &&  existePrefijoEnDawg(camino, letra, dawg)) {
                   // añadimos esa letra a camino y hacemos backtracking
                    camino.add(new SimpleEntry<>(letra, true));
                    computarMejorPalabraDelAtrilAux(siguiente,atril,usados,restantes-1,camino,mejorPalabra,tablero,x,y, dawg, estaTranspuesto);
                    if (camino.size() > 0) camino.remove(camino.size() - 1);
                    usados[i] = false;
                }
            }
        }
    }

    private boolean existePrefijoEnDawg(List<SimpleEntry<String, Boolean>> camino, String letra, Dawg dawg) {
        List<String> prefijo = new ArrayList<>();
        // por cada letra del camino la añadimos al prefijo
        for (SimpleEntry<String, Boolean> entry : camino) {
            prefijo.add(entry.getKey());
        }
        // añadimos la letra con la que queremos combinar camino y miramos si existe el prefijo en el dawg
        prefijo.add(letra);
        return dawg.existePrefijo(String.join("", prefijo));
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
    private int extenderParteDerecha(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxConPosiciones, String[] atril, boolean[] usados, NodoDawg nodo, int x, int y, Dawg dawg) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        int puntuacion = 0;

        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra = new ArrayList<>();

        int pt = extenderParteDerechaAux(tablero,caminoAuxConPosiciones,atril,usados,nodo, mejorPalabra, x, y, 0, dawg);

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
    private int extenderParteDerechaAux(Tablero tablero, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> caminoAuxPos, String[] atril, boolean[] usados,  NodoDawg nodo, List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> mejorPalabra, int x, int y, int puntuacion, Dawg dawg) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        // si el nodo es final entonces es una palabra, comprobamos si su puntuacion es mayor que la que mejorPalabra actual y si es así la cambiamos
        int mejorPuntuacion = puntuacion;

        // si no es casilla correcta significa que estamos fuera del tablero y por lo tanto devuelve la mejorPuntuacion
        if (!casillaCorrecta(x,y)) {
            return mejorPuntuacion;
        }
        // a partir de caminoAuxPos creamos un string que sea la palabra y comprobamos que exista en el dawg, ya que solo mirando si nodo.getEsFinal() devolvía true hacía que se colaran palabras que no eran palabra correctas
        List<SimpleEntry<String, Boolean>> listaLetras1 = new ArrayList<>();
        for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> entry : caminoAuxPos) {
            listaLetras1.add(entry.getKey());
        }
        StringBuilder sb = new StringBuilder();
        for (SimpleEntry<String, Boolean> entry : listaLetras1) {
            sb.append(entry.getKey());
        }
        String palabra = sb.toString();
        if (nodo.getEsFinal() && dawg.existePalabra(palabra))
        {
            int puntuacionCamino = obtenerPuntuacion(tablero, caminoAuxPos);
            if (puntuacionCamino > mejorPuntuacion)
            {
                mejorPuntuacion = puntuacionCamino;
                mejorPalabra.clear();
                mejorPalabra.addAll(new ArrayList<>(caminoAuxPos));
            }
        }

        // si el tablero con posicion x  y esta vacia probamos todas las letras y lo hacemos recursivamente
        String letraTablero = null;
        if (tablero.getFicha(x, y) != null) letraTablero =  tablero.getFicha(x,y).getLetra();
        if (letraTablero == null) {
            for (int i = 0; i < atril.length; i++) {
                // para cada letra del atril y que no haya sido usada, miramos si está en el crossCheck Set de esa posición y de ser así miramos si existe un prefijo con esa letra combinada con el caminoAuxPos actual
                if (!usados[i] && tablero.getAbecedario(x,y).contains(atril[i])) {
                    String letra = atril[i];
                    NodoDawg siguiente = nodo.getHijos().get(letra);
                    List<SimpleEntry<String, Boolean>> listaLetras = new ArrayList<>();
                    for (SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>> entry : caminoAuxPos) {
                        listaLetras.add(entry.getKey());
                    }
                    if (siguiente != null && existePrefijoEnDawg(listaLetras, letra, dawg)) {
                        usados[i] = true;
                        caminoAuxPos.add(new SimpleEntry<>(new SimpleEntry<>(letra, true), new SimpleEntry<>(x, y)));
                        if (y + 1 < 15)
                        {
                            int puntuacionRec = extenderParteDerechaAux(tablero, caminoAuxPos, atril, usados, siguiente, mejorPalabra, x, y + 1, puntuacion, dawg);
                            if (puntuacionRec > mejorPuntuacion) {
                                mejorPuntuacion = puntuacionRec;
                            }
                        }
                        if (caminoAuxPos.size() >= 1)
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
                if (y + 1 < 15)
                {
                    int puntuacionRec = extenderParteDerechaAux(tablero, caminoAuxPos, atril, usados, nodo.getHijos().get(tablero.getFicha(x,y).getLetra()),mejorPalabra,x,y+1,puntuacion, dawg);

                    if (puntuacionRec > mejorPuntuacion) {
                        mejorPuntuacion = puntuacionRec;

                    }
                }
                if (caminoAuxPos.size() >= 1)
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
    public List<SimpleEntry<String, Boolean>> computarParteIzquierdaTablero(Tablero tablero, int longitud, int x, int y) throws CoordenadaFueraDeRangoException {
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
    public boolean casillaCorrecta(Integer x, Integer y) {
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
    public List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> asignarPosiciones(List<SimpleEntry<String, Boolean>> palabra, int max_long, int x, int y, boolean estaTranspuesto) throws CoordenadaFueraDeRangoException {
        List<SimpleEntry<SimpleEntry<String, Boolean>, SimpleEntry<Integer, Integer>>> palabraFinal = new ArrayList<>();
        int posInicial = estaTranspuesto ? x - max_long : y - max_long;
        for (int i = 0; i < palabra.size(); i++) {
            int posX = estaTranspuesto ? posInicial + i : x;
            int posY = estaTranspuesto ? y : posInicial + i;
            if (!casillaCorrecta(posX, posY)) continue;
            SimpleEntry<Integer, Integer> posicion = new SimpleEntry<>(posX, posY);
            palabraFinal.add(new SimpleEntry<>(palabra.get(i), posicion));
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
    public int tamañoParteIzquierdaAtril(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        int size = 0;

        // Mientras las casillas a la izquierda sean correctas y no esten ocupadas, sumar uno a size
        for(int col = y - 1; casillaCorrecta(x, col); col--) {
            // Si hay una casilla ya ocupada, no vamos a empezar la palabra directamente a su derecha, sino que vamos a dejar un espacio
            if(tablero.getFicha(x,col) != null) {
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
    public int tamañoParteIzquierdaTablero(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        int size = 0;
        for (int col = y - 1; col >= 0 && tablero.getFicha(x, col) != null; col--) {
            size++;
        }
        return size;
    }
}
