package algorisme;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import exceptions.CoordenadaFueraDeRangoException;
import gestordepartida.Ficha;
import gestordepartida.Partida;
import gestordepartida.Tablero;

/**
 * Clase que implementa algoritmos para encontrar el mejor movimiento en un juego de Scrabble.
 * Utiliza estructuras DAWG para validación de palabras y estrategias de backtracking para optimizar la búsqueda.
 * La clase gestiona la lógica principal para evaluar movimientos posibles, calcular puntuaciones y validar palabras.
 *
 * @author Albert Aulet Niubó
 * @author Arnau Miret Barrull
 */
public class Algoritmo {
    /** Número de filas del tablero de Scrabble. Valor constante: 15. */
    private static final int FILAS = 15;
    /** Número de columnas del tablero de Scrabble. Valor constante: 15. */
    private static final int COLUMNAS = 15;
    /** Conjunto de dígrafos válidos en el idioma del juego (ej: "RR", "NY"). */
    private static final Set<String> Digrafos = new HashSet<>(Arrays.asList("RR", "NY", "LL", "L·L", "CH"));
    /** Instancia de la partida actual que gestiona las reglas y estado del juego. */
    private Partida partida;

    /**
     * Constructor que inicializa el algoritmo con una partida específica.
     *
     * @param partida Objeto Partida que contiene el estado actual del juego.
     */
    public Algoritmo(Partida partida)
    {
        this.partida = partida;
    }


    /**
     * Calcula la mejor palabra para colocar en el tablero considerando direcciones horizontales y verticales.
     * Evalúa todas las posiciones ancla y utiliza backtracking para encontrar combinaciones óptimas.
     *
     * @param dawg Estructura DAWG para validación de palabras.
     * @param tablero Estado actual del tablero.
     * @param atril Array con las fichas disponibles en el atril del jugador.
     * @return Lista de entradas con la mejor palabra encontrada, indicando letras, posiciones y si son del atril.
     * @throws CoordenadaFueraDeRangoException Si se accede a una posición inválida del tablero.
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
     * Calcula las palabras válidas para una posición ancla específica combinando letras del atril y del tablero.
     *
     * @param dawg Estructura DAWG para validación.
     * @param tablero Estado actual del tablero.
     * @param ancla Posición (x,y) que actúa como punto de anclaje para la palabra.
     * @param atril Fichas disponibles en el atril.
     * @param estaTranspuesto Indica si el tablero está transpuesto (para evaluar vertical/horizontal).
     * @return Lista de posibles palabras válidas para el ancla especificado.
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
     * Calcula la puntuación total de una palabra considerando modificadores del tablero y bonificaciones.
     *
     * @param tablero Tablero actual del juego.
     * @param palabra Lista de entradas con letras, posiciones y origen (atril/tablero).
     * @return Puntuación total calculada. Retorna 0 si la palabra es inválida.
     * @throws CoordenadaFueraDeRangoException Si alguna posición de la palabra es inválida.
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
     * Calcula la puntuación de palabras verticales formadas al colocar una letra en una posición específica.
     *
     * @param tablero Tablero actual del juego.
     * @param x Fila de la posición evaluada.
     * @param y Columna de la posición evaluada.
     * @return Puntuación de la palabra vertical formada. Retorna 0 si no es válida.
     * @throws CoordenadaFueraDeRangoException Si (x,y) está fuera de rango.
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
     * Valida si una letra colocada en una posición forma una palabra horizontal válida según el DAWG.
     *
     * @param tablero Tablero actual del juego.
     * @param fila Fila de la posición evaluada.
     * @param columna Columna de la posición evaluada.
     * @param letra Letra a validar.
     * @param dawg Estructura DAWG para verificación.
     * @return true si la palabra horizontal es válida, false en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si (fila,columna) es inválida.
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
     * Calcula los cross-checks para cada posición vacía del tablero, determinando letras válidas.
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

    /**
     * Valida si una letra colocada en una posición forma una palabra vertical válida según el DAWG.
     *
     * @param tablero Tablero actual del juego.
     * @param fila Fila de la posición evaluada.
     * @param columna Columna de la posición evaluada.
     * @param letra Letra a validar.
     * @param dawg Estructura DAWG para verificación.
     * @return true si la palabra vertical es válida, false en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si (fila,columna) es inválida.
     * @author Albert Aulet Niubó
     */
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
     * Identifica todas las posiciones ancla en el tablero (casillas vacías adyacentes a letras existentes).
     *
     * @param tablero Tablero actual del juego.
     * @return Lista de posiciones (x,y) que actúan como anclas.
     * @throws CoordenadaFueraDeRangoException Si se detecta una posición inválida durante el proceso.
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
     * Verifica si una posición del tablero tiene casillas adyacentes ocupadas.
     *
     * @param tablero Tablero actual del juego.
     * @param fila Fila de la posición evaluada.
     * @param columna Columna de la posición evaluada.
     * @return true si la casilla adyacente a su derecha está ocupada, false en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si (fila,columna) es inválida.
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
     * Genera la mejor combinación de letras del atril para formar una palabra válida desde una posición ancla.
     * Combina backtracking con verificación en DAWG para optimizar la búsqueda de movimientos válidos.
     *
     * @param root Nodo raíz del DAWG para validación de prefijos
     * @param atril Array con fichas disponibles en el atril
     * @param longitud Máxima extensión permitida hacia la izquierda del ancla
     * @param tablero Estado actual del tablero
     * @param x Coordenada X (fila) de la posición ancla
     * @param y Coordenada Y (columna) de la posición ancla
     * @param dawg Instancia del DAWG para validación léxica
     * @param estaTranspuesto Indica si el tablero está en orientación vertical
     * @return Lista de entradas con estructura: ((Letra, EsDelAtril), (PosX, PosY))
     * @throws CoordenadaFueraDeRangoException Si las coordenadas exceden los límites del tablero
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
     * Método auxiliar recursivo que genera combinaciones válidas para la parte izquierda de la palabra.
     * Evalúa todas las permutaciones posibles del atril actualizando continuamente la mejor solución encontrada.
     *
     * @param nodo Nodo actual en el DAWG durante la exploración
     * @param atril Conjunto de fichas disponibles
     * @param usados Array que rastrea las fichas del atril utilizadas
     * @param restantes Número de posiciones restantes por rellenar
     * @param camino Lista temporal con la combinación actual de letras
     * @param mejorPalabra Referencia mutable a la mejor solución encontrada
     * @param tablero Estado actualizado del tablero
     * @param x Coordenada X del ancla
     * @param y Coordenada Y del ancla
     * @param dawg Instancia del DAWG para validaciones
     * @param estaTranspuesto Estado de orientación del tablero
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

    /**
     * Verifica si la combinación de letras actual más una nueva letra forman un prefijo válido en el DAWG.
     *
     * @param camino Lista de letras actualmente seleccionadas
     * @param letra Nueva letra a añadir al prefijo
     * @param dawg Instancia del DAWG para consulta
     * @return true si el prefijo combinado existe en el DAWG
     * @author Albert Aulet Niubó
     */
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
     * Extiende una palabra hacia la derecha desde la posición actual, evaluando posibles combinaciones.
     * Considera tanto fichas existentes en el tablero como nuevas fichas del atril.
     *
     * @param tablero Estado del tablero
     * @param caminoAuxConPosiciones Lista temporal con letras y posiciones
     * @param atril Fichas disponibles
     * @param usados Fichas del atril ya utilizadas
     * @param nodo Nodo actual en el DAWG
     * @param x Posición X actual
     * @param y Posición Y actual
     * @return Máxima puntuación obtenida en esta rama de exploración
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
     * Método auxiliar recursivo para la extensión derecha de palabras.
     * Evalúa sistemáticamente todas las posibilidades de extensión y actualiza la mejor solución.
     *
     * @param tablero Estado actual del tablero
     * @param caminoAuxPos Lista temporal con la palabra en construcción
     * @param atril Fichas disponibles
     * @param usados Fichas del atril utilizadas
     * @param nodo Nodo DAWG actual
     * @param mejorPalabra Referencia a la mejor solución encontrada
     * @param x Coordenada X actual
     * @param y Coordenada Y actual
     * @param puntuacion Puntuación acumulada
     * @return Puntuación máxima alcanzada en esta rama
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
     * Obtiene la secuencia de letras existentes a la izquierda de una posición ancla en el tablero.
     *
     * @param tablero Estado del tablero
     * @param longitud Máxima longitud a recuperar
     * @param x Fila del ancla
     * @param y Columna del ancla
     * @return Lista de letras preexistentes con indicador de no ser del atril
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
     * Valida si una posición específica se encuentra dentro de los límites del tablero.
     *
     * @param x Coordenada X (fila) a validar
     * @param y Coordenada Y (columna) a validar
     * @return true si la posición es válida (0 ≤ x,y < 15)
     */
    public boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < FILAS && y >= 0 && y < COLUMNAS;
    }


    /**
     * Asigna coordenadas del tablero a cada letra de una palabra generada.
     *
     * @param palabra Lista de letras con indicador de origen
     * @param max_long Longitud de la parte izquierda
     * @param x Fila base del ancla
     * @param y Columna base del ancla
     * @param estaTranspuesto Indica orientación del tablero
     * @return Lista de letras con sus posiciones asignadas
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
     * Calcula la máxima extensión posible hacia la izquierda usando solo el atril.
     *
     * @param tablero Estado del tablero
     * @param x Fila del ancla
     * @param y Columna del ancla
     * @return Número máximo de posiciones disponibles
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
     * Determina la longitud de la secuencia existente de letras a la izquierda del ancla.
     *
     * @param tablero Estado del tablero
     * @param x Fila del ancla
     * @param y Columna del ancla
     * @return Cantidad de letras consecutivas existentes
     */
    public int tamañoParteIzquierdaTablero(Tablero tablero, int x, int y) throws CoordenadaFueraDeRangoException {
        int size = 0;
        for (int col = y - 1; col >= 0 && tablero.getFicha(x, col) != null; col--) {
            size++;
        }
        return size;
    }
}
