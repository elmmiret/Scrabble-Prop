package algorisme;

import gestordepartida.Tablero;
import gestordepartida.Ficha;
import exceptions.CoordenadaFueraDeRangoException;

import java.util.*;

/**
 * Clase que implementa el algoritmo para generar movimientos válidos en un juego de palabras.
 * Utiliza una estructura DAWG (Directed Acyclic Word Graph) para verificar palabras válidas
 * y genera posibles movimientos basados en el estado actual del tablero y las fichas disponibles.
 *
 * @author Arnau Miret
 */

public class Algorithm {
    /** El DAWG que contiene el diccionario de palabras válidas */
    private final Dawg dawg;

    /** El tablero actual de juego */
    private Tablero tablero;

    /** Mapa que representa las letras disponibles en el atril y sus cantidades */
    private Map<String, Integer> atril;  // atril de Strings (letras)

    /** Matriz que indica las posiciones ancla en el tablero */
    private boolean[][] anchors;

    /** Mapa de conjuntos que contiene las letras válidas para cada posición (cross-checks) */
    private Map<Integer, Set<String>> crossChecks;  // Usamos un mapa de conjuntos para cross-checks

    /**
     * Constructor que inicializa el algoritmo con un DAWG específico.
     *
     * @param dawg El DAWG que servirá como diccionario de palabras válidas
     * @throws IllegalArgumentException Si el DAWG proporcionado es nulo
     */
    public Algorithm(Dawg dawg) {
        this.dawg = dawg;
    }

    /**
     * Prepara el algoritmo para generar movimientos con un tablero y atril específicos.
     * Calcula las posiciones ancla y los cross-checks necesarios para la generación de movimientos.
     *
     * @param tablero El tablero actual del juego
     * @param atril Mapa de fichas disponibles en el atril y sus cantidades
     * @throws IllegalArgumentException Si el tablero o el atril son nulos
     */
    public void preparar(Tablero tablero, Map<Ficha, Integer> atril) {
        this.tablero = tablero;
        this.atril = contarLetras(atril);
        calcularAnclajes();
        calcularCrossChecks();
    }

    /**
     * Genera todos los movimientos válidos posibles con la configuración actual.
     *
     * @return Lista de movimientos válidos ordenados según algún criterio (puntuación, longitud, etc.)
     * @throws IllegalStateException Si el algoritmo no ha sido preparado previamente
     */
    public List<Movimiento> generarMovimientos() {
        System.out.println("estoy generando movimientos");
        List<Movimiento> movimientos = new ArrayList<>();
        System.out.println("Atril IA: " + atril);
        System.out.println("Anchors:");
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                if (anchors[i][j]) System.out.print("A ");
                else System.out.print(". ");
            }
            System.out.println();
        }


        // Generar movimientos horizontales
        for(int fila = 0; fila < Tablero.FILAS; fila++) {
            generarMovimientosFila(fila, movimientos);
        }

        // Generar movimientos verticales (transponiendo el tablero)
        tablero.transponerTablero();
        for(int fila = 0; fila < Tablero.FILAS; fila++) {
            generarMovimientosFila(fila, movimientos, true);
        }
        tablero.transponerTablero();

        if (movimientos == null || movimientos.isEmpty()) System.out.println("Estoy vacio");
        return movimientos;
    }

    /**
     * Genera movimientos válidos para una fila específica del tablero.
     *
     * @param fila La fila del tablero para la cual generar movimientos
     * @param movimientos La lista donde se acumularán los movimientos generados
     */
    private void generarMovimientosFila(int fila, List<Movimiento> movimientos) {
        generarMovimientosFila(fila, movimientos, false);
    }

    /**
     * Genera movimientos válidos para una fila específica, considerando la orientación.
     *
     * @param fila La fila del tablero para la cual generar movimientos
     * @param movimientos La lista donde se acumularán los movimientos generados
     * @param esVertical Indica si los movimientos son verticales (true) u horizontales (false)
     */
    private void generarMovimientosFila(int fila, List<Movimiento> movimientos, boolean esVertical) {
        for(int col = 0; col < Tablero.COLUMNAS; col++) {
            if(anchors[fila][col]) {
                // Encontrar el limite izquierdo (máxima longitud del prefijo)
                int limiteIzquierdo = 0;
                while(col - limiteIzquierdo - 1 >= 0 && !anchors[fila][col - limiteIzquierdo - 1]) limiteIzquierdo++;

                // Generar prefijos izquierdos y extender a la derecha
                generarPrefijos(fila, col, limiteIzquierdo, new ArrayList<>(), dawg.getRoot(), movimientos, esVertical);

                // Si hay una letra a la izquierda, extender desde ella
                if(col > 0) {
                    try {
                        Ficha fichaIzquierda = tablero.getFicha(fila, col-1);
                        if(fichaIzquierda != null) {
                            List<String> letraInicial = new ArrayList<>();
                            letraInicial.add(fichaIzquierda.getLetra());
                            extenderDesdePalabraExistente(fila, col, letraInicial, movimientos, esVertical);
                        }
                    } catch (CoordenadaFueraDeRangoException e) {
                        // NO deberia ocurrir ya que estamos verificando limites
                    }
                }
            }
        }
    }

    /**
     * Genera prefijos válidos para extender desde una posición ancla.
     *
     * @param fila Fila de la posición ancla
     * @param colAncla Columna de la posición ancla
     * @param limite Máxima longitud del prefijo
     * @param palabraParcial Lista de letras que forman el prefijo actual
     * @param nodo Nodo actual en el DAWG
     * @param movimientos Lista donde se acumularán los movimientos generados
     * @param esVertical Indica si la orientación es vertical
     */
    private void generarPrefijos(int fila, int colAncla, int limite, List<String> palabraParcial, NodoDawg nodo, List<Movimiento> movimientos, boolean esVertical) {
        // Exetender hacia la derecha desde el ancla
        extenderDerecha(fila, colAncla, palabraParcial, nodo, movimientos, esVertical);

        // Generar más prefijos si es posible
        if(limite > 0) {
            for(Map.Entry<String, NodoDawg> entrada : nodo.getHijos().entrySet()) {
                String letra = entrada.getKey();
                NodoDawg hijo = entrada.getValue();

                if(atril.containsKey(letra) && atril.get(letra) > 0) {
                    // Usar letra del atril
                    atril.put(letra, atril.get(letra) - 1);   // borrar letra del atril
                    List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                    nuevaPalabra.add(letra);
                    generarPrefijos(fila, colAncla, limite - 1, nuevaPalabra, hijo, movimientos, esVertical);
                    atril.put(letra, atril.get(letra) + 1);   //añadir la letra al atril
                }

                // Verificar si hay un blanco disponible
                if(atril.containsKey("#") && atril.get("#") > 0) {
                    atril.put("#", atril.get("#") - 1);
                    List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                    nuevaPalabra.add(letra);
                    generarPrefijos(fila, colAncla, limite - 1, nuevaPalabra, hijo, movimientos, esVertical);
                    atril.put("#", atril.get("#") + 1);
                }
            }
        }
    }

    /**
     * Extiende una palabra parcial hacia la derecha desde una posición dada.
     *
     * @param fila Fila actual en el tablero
     * @param col Columna actual en el tablero
     * @param palabraParcial Lista de letras que forman la palabra parcial
     * @param nodo Nodo actual en el DAWG
     * @param movimientos Lista donde se acumularán los movimientos generados
     * @param esVertical Indica si la orientación es vertical
     */
    private void extenderDerecha(int fila, int col, List<String> palabraParcial, NodoDawg nodo, List<Movimiento> movimientos, boolean esVertical) {
        try {
            // Verificar su es un movimiento válido
            if(nodo.getEsFinal() && (col >= Tablero.COLUMNAS || tablero.getFicha(fila, col) == null)) {
                // Añadir movimiento válido
                System.out.println("Posible palabra formada: " + palabraParcial);
                movimientos.add(new Movimiento(fila, col - palabraParcial.size(), palabraParcial, esVertical));
            }
             if( col < Tablero.COLUMNAS) {
                 Ficha ficha = tablero.getFicha(fila,col);

                 if(ficha == null) {
                     // Casilla vacía -- usar letra del atril con cross-check
                     for(Map.Entry<String, NodoDawg> entrada : nodo.getHijos().entrySet()) {
                         String letra = entrada.getKey();
                         NodoDawg hijo = entrada.getValue();

                         // Verificar cross-check
                         int posicion = fila * Tablero.COLUMNAS + col;
                         if((crossChecks.containsKey(posicion))) {
                             Set<String> checks = crossChecks.get(posicion);
                             if(checks.contains(letra)) {
                                 if(atril.containsKey(letra) && atril.get(letra) > 0) {
                                     atril.put(letra, atril.get(letra) - 1);
                                     List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                                     nuevaPalabra.add(letra);
                                     extenderDerecha(fila, col + 1, nuevaPalabra, hijo, movimientos, esVertical);
                                     atril.put(letra, atril.get(letra) + 1);
                                 }

                                 // Usar blanci si está disponible
                                 if(atril.containsKey("#") && atril.get("#") > 0) {
                                     atril.put("#", atril.get("#") - 1);
                                     List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                                     nuevaPalabra.add(letra);
                                     extenderDerecha(fila, col + 1, nuevaPalabra, hijo, movimientos, esVertical);
                                     atril.put("#", atril.get("#") + 1);
                                 }
                             }
                         }
                     }
                 }
                 else {
                     // Casilla ocuapada -- usar la letra que ya está allí
                     String letra = ficha.getLetra();
                     if(nodo.getHijos().containsKey(letra)) {
                         List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                         nuevaPalabra.add(letra);
                         extenderDerecha(fila, col + 1, nuevaPalabra, nodo.getHijo(letra), movimientos, esVertical);
                     }
                 }
             }
        } catch (CoordenadaFueraDeRangoException e) {
            // No deberia ocurrir ya que estamos verificando los límites
        }
    }

    /**
     * Extiende una palabra existente en el tablero para formar nuevos movimientos.
     *
     * @param fila Fila de la posición inicial
     * @param colAncla Columna de la posición inicial
     * @param palabraParcial Lista de letras que forman la palabra existente
     * @param movimientos Lista donde se acumularán los movimientos generados
     * @param esVertical Indica si la orientación es vertical
     */
    private void extenderDesdePalabraExistente(int fila, int colAncla, List<String> palabraParcial, List<Movimiento> movimientos, boolean esVertical) {
        try {
            NodoDawg nodo = dawg.getRoot();
            for(String letra : palabraParcial) {
                if(nodo.getHijos().containsKey(letra)) {
                    nodo = nodo.getHijo(letra);
                }
                else {
                    return; // si la palabra parcial no existe en el Dawg
                }
            }

            if(nodo != null) {
                extenderDerecha(fila, colAncla, palabraParcial, nodo, movimientos, esVertical);
            }

        } catch (Exception e) {
            // Manejar error si la letra no existe en el Dawg
        }
    }

    /**
     * Calcula las posiciones ancla en el tablero (casillas vacías adyacentes a fichas existentes).
     */
    private void calcularAnclajes(){
        anchors = new boolean[Tablero.FILAS][Tablero.COLUMNAS];

        try {
            for(int fila = 0; fila < Tablero.FILAS; fila++) {
                for(int col = 0; col < Tablero.COLUMNAS; col++) {
                    if(tablero.getFicha(fila, col) == null) {
                        // Verificar si está adyacente a una ficha existente
                        boolean esAncla = false;

                        // Verificar arriba
                        if(dawg.casillaCorrecta(fila -1, col) && tablero.getFicha(fila - 1, col) != null) esAncla = true;
                        // Verificar abajo
                        if(dawg.casillaCorrecta(fila +1, col) && tablero.getFicha(fila + 1, col) != null) esAncla = true;
                        // Verificar izquierda
                        if(dawg.casillaCorrecta(fila, col-1) && tablero.getFicha(fila, col - 1) != null) esAncla = true;
                        // Verificar derecha
                        if(dawg.casillaCorrecta(fila, col+1) && tablero.getFicha(fila, col + 1) != null) esAncla = true;

                        anchors[fila][col] = esAncla;
                    }
                }
            }
        } catch (CoordenadaFueraDeRangoException e) {
            // No deberia ocurrir ya que estamos verificando los limites
        }
    }

    /**
     * Calcula los cross-checks para cada posición vacía del tablero.
     * Un cross-check determina qué letras pueden colocarse en una posición sin crear palabras inválidas
     * en la dirección perpendicular.
     */
    private void calcularCrossChecks() {
        crossChecks = new HashMap<>();

        try {
            for(int fila = 0; fila < Tablero.FILAS; fila++) {
                for(int col = 0; col < Tablero.COLUMNAS; col++) {
                    if(tablero.getFicha(fila, col) == null) {
                        // Calcular cross-check para esta casilla
                        Set<String> checks = new HashSet<>();
                        int posicion = fila * Tablero.COLUMNAS + col;

                        // Verificar palabras verticales (para movimeintos horizontales)
                        List<String> palabraArriba = new ArrayList<>();
                        for(int f = fila - 1; dawg.casillaCorrecta(fila, col) && tablero.getFicha(f, col) != null; f--) {
                            palabraArriba.add(0, tablero.getFicha(f, col).getLetra());
                        }

                        List<String> palabraAbajo = new ArrayList<>();
                        for(int f = fila +  1; dawg.casillaCorrecta(fila, col) && tablero.getFicha(f, col) != null; f++) {
                            palabraAbajo.add(tablero.getFicha(f, col).getLetra());
                        }

                        // Para cada letra posible en el Dawg, verificar si forma una palabra válida
                        for(String letra : dawg.getRoot().getHijos().keySet()) {
                            List<String> palabraCompleta = new ArrayList<>(palabraAbajo);
                            palabraCompleta.add(letra);
                            palabraCompleta.addAll(palabraAbajo);

                            if(palabraCompleta.size() == 1 || dawg.existePalabra(concatenarPalabra(palabraCompleta))) {
                                checks.add(letra);
                            }
                        }

                        if(!checks.isEmpty()) {
                            crossChecks.put(posicion, checks);
                        }
                    }
                }
            }
        } catch (CoordenadaFueraDeRangoException e) {
            // No deberia ocurrir ya que estamos verificando los limites
        }
    }

    /**
     * Convierte un mapa de fichas a un mapa de letras con sus cantidades.
     *
     * @param atril Mapa original de fichas y sus cantidades
     * @return Mapa de letras y sus cantidades correspondientes
     */
    private Map<String, Integer> contarLetras(Map<Ficha, Integer> atril) {
        Map<String, Integer> count = new HashMap<>();
        for (Map.Entry<Ficha, Integer> letra : atril.entrySet()) {
            String l = letra.getKey().getLetra();
            int cantidad = letra.getValue();  // Cuántas fichas hay de esa letra
            count.put(l, count.getOrDefault(l, 0) + cantidad);
        }
        return count;
    }

    /**
     * Concatena una lista de letras para formar una palabra.
     *
     * @param letras Lista de letras a concatenar
     * @return Cadena resultante de concatenar las letras
     */
    private String concatenarPalabra(List<String> letras) {
        StringBuilder palabra = new StringBuilder();
        for(String s : letras) {
            palabra.append(s);
        }
        return palabra.toString();
    }

}
