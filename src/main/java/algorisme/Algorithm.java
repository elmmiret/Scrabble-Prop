package algorisme;

import gestordepartida.Tablero;
import gestordepartida.Partida;
import gestordepartida.Ficha;
import exceptions.CoordenadaFueraDeRangoException;

import java.util.*;

public class Algorithm {
    private final Dawg dawg;
    private final Partida.Idioma idioma;
    private Map<Ficha, Integer> mapaFichas;
    private Tablero tablero;
    private Map<String, Integer> atrilString;  // atril de Strings (letras)
    private Map<Ficha, Integer> atrilFicha;
    private boolean[][] anchors;
    private Map<Integer, Set<String>> crossChecks;  // Usamos un mapa de conjuntos para cross-checks

    public Algorithm(Dawg dawg, Partida.Idioma idiomaPartida) {
        this.dawg = dawg;
        this.idioma = idiomaPartida;
        setBolsa();
    }

    /**
     * Prepara el algoritmo para generar movimientos en un tablero dado con un atril especifico
     * @param tablero El tablero actual de juego
     * @param atril Las letras disponibles en el atril
     */
    private void preparar(Tablero tablero, Map<Ficha, Integer> atril) {
        this.tablero = tablero;
        this.atrilFicha = atril;
        this.atrilString = atrilDeStrings(atril);
        calcularAnclajes();
        calcularCrossChecks();
    }

    private void setBolsa() {
        switch (idioma) {
            case CAT:
                AlfabetoCAT alfabetoCat = new AlfabetoCAT();
                mapaFichas = alfabetoCat.getMapaFichas();
                break;
            case CAST:
                AlfabetoCAST alfabetoCast = new AlfabetoCAST();
                mapaFichas = alfabetoCast.getMapaFichas();
                break;
            case ENG:
                AlfabetoING alfabetoING = new AlfabetoING();
                mapaFichas = alfabetoING.getMapaFichas();
                break;
            default:
                break;
        }
    }

    public Movimiento mejorMovimiento(Tablero tablero, List<String> atril) {
        preparar();
    }

    /**
     * Genera todos los movimientos válidos para la configuración actual
     * @return Lista de movimientos válidos
     */
    private List<Movimiento> generarMovimientos() {
        List<Movimiento> movimientos = new ArrayList<>();

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

        return movimientos;
    }

    private void generarMovimientosFila(int fila, List<Movimiento> movimientos) {
        generarMovimientosFila(fila, movimientos, false);
    }

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

    private void generarPrefijos(int fila, int colAncla, int limite, List<String> palabraParcial, NodoDawg nodo, List<Movimiento> movimientos, boolean esVertical) {
        // Exetender hacia la derecha desde el ancla
        extenderDerecha(fila, colAncla, palabraParcial, nodo, movimientos, esVertical);

        // Generar más prefijos si es posible
        if(limite > 0) {
            for(Map.Entry<String, NodoDawg> entrada : nodo.getHijos().entrySet()) {
                String letra = entrada.getKey();
                NodoDawg hijo = entrada.getValue();

                if(atrilString.containsKey(letra) && atrilString.get(letra) > 0) {
                    // Usar letra del atril
                    atrilString.put(letra, atrilString.get(letra) - 1);   // borrar letra del atril
                    List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                    nuevaPalabra.add(letra);
                    generarPrefijos(fila, colAncla, limite - 1, nuevaPalabra, hijo, movimientos, esVertical);
                    atrilString.put(letra, atrilString.get(letra) + 1);   //añadir la letra al atril
                }

                // Verificar si hay un blanco disponible
                if(atrilString.containsKey("#") && atrilString.get("#") > 0) {
                    atrilString.put("#", atrilString.get("#") - 1);
                    List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                    nuevaPalabra.add(letra);
                    generarPrefijos(fila, colAncla, limite - 1, nuevaPalabra, hijo, movimientos, esVertical);
                    atrilString.put("#", atrilString.get("#") + 1);
                }
            }
        }
    }

    private void extenderDerecha(int fila, int col, List<String> palabraParcial, NodoDawg nodo, List<Movimiento> movimientos, boolean esVertical) {
        try {
            // Verificar su es un movimiento válido
            if(nodo.getEsFinal() && (col >= Tablero.COLUMNAS || tablero.getFicha(fila, col) == null)) {
                // Añadir movimiento válido
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
                                 if(atrilString.containsKey(letra) && atrilString.get(letra) > 0) {
                                     atrilString.put(letra, atrilString.get(letra) - 1);
                                     List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                                     nuevaPalabra.add(letra);
                                     extenderDerecha(fila, col + 1, nuevaPalabra, hijo, movimientos, esVertical);
                                     atrilString.put(letra, atrilString.get(letra) + 1);
                                 }

                                 // Usar blanci si está disponible
                                 if(atrilString.containsKey("#") && atrilString.get("#") > 0) {
                                     atrilString.put("#", atrilString.get("#") - 1);
                                     List<String> nuevaPalabra = new ArrayList<>(palabraParcial);
                                     nuevaPalabra.add(letra);
                                     extenderDerecha(fila, col + 1, nuevaPalabra, hijo, movimientos, esVertical);
                                     atrilString.put("#", atrilString.get("#") + 1);
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

    private void calcularAnclajes(){
        anchors = new boolean[Tablero.FILAS][Tablero.COLUMNAS];

        try {
            for(int fila = 0; fila < Tablero.FILAS; fila++) {
                for(int col = 0; col < Tablero.COLUMNAS; col++) {
                    if(tablero.getFicha(fila, col) == null) {
                        // Verificar si está adyacente a una ficha existente
                        boolean esAncla = false;

                        // Verificar arriba
                        if(dawg.casillaCorrecta(fila, col) && tablero.getFicha(fila - 1, col) != null) esAncla = true;
                        // Verificar abajo
                        if(dawg.casillaCorrecta(fila, col) && tablero.getFicha(fila + 1, col) != null) esAncla = true;
                        // Verificar izquierda
                        if(dawg.casillaCorrecta(fila, col) && tablero.getFicha(fila, col - 1) != null) esAncla = true;
                        // Verificar derecha
                        if(dawg.casillaCorrecta(fila, col) && tablero.getFicha(fila, col + 1) != null) esAncla = true;

                        anchors[fila][col] = esAncla;
                    }
                }
            }
        } catch (CoordenadaFueraDeRangoException e) {
            // No deberia ocurrir ya que estamos verificando los limites
        }
    }

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

    /*
    private Map<String, Integer> contarLetras(List<String> atril) {
        Map<String, Integer> count = new HashMap<>();
        for(String letra : atril) {
            count.put(letra, count.getOrDefault(letra, 0) + 1);
        }
        return count;
    }*/

    private Map<String, Integer> atrilDeStrings(Map<Ficha, Integer> atril) {

    }

    private String concatenarPalabra(List<String> letras) {
        StringBuilder palabra = new StringBuilder();
        for(String s : letras) {
            palabra.append(s);
        }
        return palabra.toString();
    }

}
