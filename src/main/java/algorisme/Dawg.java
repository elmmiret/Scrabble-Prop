package algorisme;
import ctrldomini.*;
import exceptions.CoordenadaFueraDeRangoException;

//import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * @author Arnau Miret Barrull
 */
public class Dawg {
    private static Set<String> Digrafos;
    private NodoDawg root;  // Nodo raíz del DAWG
    private Map<NodoDawg, NodoDawg> registro;   // Mapa para evitar nodos multiplicados (minimizar)
    private List<String> palabraAnterior;   // Última palabra insertada
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;

    // Funcion constructora
    public Dawg(Partida.Idioma idiomaPartida) {
        Digrafos = new HashSet<>(Arrays.asList("rr", "ny", "ll", "l·l", "ch"));
        root = new NodoDawg();
        registro = new HashMap<>();
        palabraAnterior = new ArrayList<>();

        switch (idiomaPartida) {
            case CAT:
                insertarDiccionarioCatalan();
                break;
            case CAST:
                insertarDiccionarioCastellano();
                break;
            case ENG:
                insertarDiccionarioIngles();
                break;
            default:
                break;
        }
    }

    // Inserta una nueva palabra en el DAWG

    /**
     * Función que inserta una nueva palabra String en el DAWG
     * @param palabra
     */
    public void insertar(String palabra) {
        List<String> simbolos = dividirPalabra(palabra);
        int prefijosComunes = 0;

        // Encuentra la logitud del prefijo común con la palabra anterior
        while(prefijosComunes < simbolos.size() && prefijosComunes < palabraAnterior.size() && simbolos.get(prefijosComunes).equals(palabraAnterior.get(prefijosComunes))) {
            prefijosComunes++;
        }

        // Minimiza nodos que ya no serán modificados;
        minimizar(prefijosComunes);

        // Empieza desde la raíz y avanza hasta el nodo correspondiente al prefijo común
        NodoDawg nodo = root;
        for(int i = 0; i < prefijosComunes && nodo != null; i++) {
            nodo = nodo.getHijos().get(palabraAnterior.get(i));
        }

        // Agrega nuevos nodos para los simbolos restantes
        for(int i = prefijosComunes; i < simbolos.size() && nodo != null; i++) {
            NodoDawg siguiente = new NodoDawg();
            nodo.getHijos().put(simbolos.get(i), siguiente);
            nodo = siguiente;
        }

        // Marca el último nodo como final
        if(nodo != null) nodo.setEsFinal(true);
        palabraAnterior = simbolos;
    }

    /**
     * Función que finaliza la construcción del DAWG minimizando todos los nodos restantes
     */
    public void acabar() {
        minimizar(0);
    }

    /**
     * Función que minimiza los nodos desde el último insertado hasta el índice dado
     * @param hasta
     */
    private void minimizar(int hasta) {
        NodoDawg nodo = root;
        for(int i = 0; i < palabraAnterior.size() - hasta; i++) {
            String simbolo = palabraAnterior.get(palabraAnterior.size() - 1 - i);
            NodoDawg hijo = nodo.getHijos().get(simbolo);
            if(hijo != null) {
                NodoDawg nodoregistrado = registro.get(hijo);
                if(nodoregistrado != null) {
                    // Reutiliza un nodo ya registrado
                    nodo.getHijos().put(simbolo, nodoregistrado);
                }
                else {
                    // Registra un nuevo nood como canónico
                    registro.put(hijo, hijo);
                }
                nodo = hijo;
            }
        }
    }

    // Divide una palabra en símbolos, reconociendo dígrafos de 2 y 3 letras como unidades

    /**
     * Función para dividir una palabra en String a una Lista de Strings con sus letras
     * @param palabra
     * @return
     */
    public List<String> dividirPalabra(String palabra) {
        List<String> division = new ArrayList<>();
        for(int i = 0; i < palabra.length(); ) {
            boolean haydigrafo = false;

            // Prioriza digrafos/trigrafos más largos
            for(int l = 3; l >= 2; l--) {
                if(i + l <= palabra.length()) {
                    String sub = palabra.substring(i, i + l);
                    if(Digrafos.contains(sub)) {
                        division.add(sub);
                        i += l;
                        haydigrafo = true;
                        break;
                    }
                }
            }

            // Si no se encontró digrafo, toma un solo carácter
            if(!haydigrafo) {
                division.add(String.valueOf(palabra.charAt(i)));
                i++;
            }
        }

        return division;
    }

    // Devuelve el nodo raíz del DAWG

    /**
     * Función para obtener el nodo raíz del DAWG
     * @return
     */
    public NodoDawg getRoot() {
        return root;
    }

    // Imprime todas las palabras representadas en el DAWG (para comprovar que funciona la implementación)
    public void imprimir(NodoDawg nodo, String prefijo) {
        if(nodo.getEsFinal()) System.out.println(prefijo);
        for(Map.Entry<String, NodoDawg> hijo : nodo.getHijos().entrySet()) {
            imprimir(hijo.getValue(), prefijo + hijo.getKey());
        }
    }

    /**
     * True o false si existe el prefijo indicado en el DAWG
     * @param prefijo
     * @return
     */
    public boolean existePrefijo(String prefijo) {
        List<String> simbolos = dividirPalabra(prefijo);
        NodoDawg nodo = root;

        for(String simbolo : simbolos) {
            if(!nodo.getHijos().containsKey(simbolo)) {
                return false;
            }
            nodo = nodo.getHijos().get(simbolo);
        }
        return true;
    }

    /**
     * True o false si existe la palabra indicada en el Dawg
     * @param palabra
     * @return
     */
    public boolean existePalabra(String palabra) {
        List<String> simbolos = dividirPalabra(palabra);
        NodoDawg nodo = root;

        for(String simbolo : simbolos) {
            if(nodo.getHijos().get(simbolo) == null) {
                return false;
            }
            nodo = nodo.getHijos().get(simbolo);
        }
        return nodo.getEsFinal();
    }

    public void insertarDiccionarioCatalan() {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/catalan.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                insertar(linea);
            }
            acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void insertarDiccionarioCastellano() {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/castellano.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                insertar(linea);
            }
            acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void insertarDiccionarioIngles() {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/ingles.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                insertar(linea);
            }
            acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    /**
     * Función que comprueba que la palabra que se quiere colocar en el tablero sea correcta
     * @param tablero
     * @param palabra
     * @param x
     * @param y
     * @param modo
     * @return
     * @throws CoordenadaFueraDeRangoException
     * @author Arnau Miret Barrull
     */
    public boolean comprobarPalabra(Tablero tablero, String palabra, int x, int y, String modo) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        List<String> division = dividirPalabra(palabra);
        int size = division.size();
        if(!existePalabra(palabra)) return false;

        // Si no hay ficha colocada en la casilla, la palabra se empieza desde ahi
        if(tablero.getFicha(x,y) == null) {
            if("horizontal".equals(modo)) {
                if(!cabePalabraHorizontal(division,x,y)) return false;

                // Desde la posición y ir poniendo las letras en el tablero, teniendo en cuenta que algunas letras pueden estar ya en el tablero
                NodoDawg nodo = getRoot();
                int pos_division = 0;

                for(int col = y; col < y + size && casillaCorrecta(x,col); col++) {
                    // Si vamos a una posición que tiene una ficha ya colocada
                    if(tablero.getFicha(x,col) != null) {
                        if (!tablero.getFicha(x,col).getLetra().equals(division.get(pos_division))) {
                            return false;
                        }
                        nodo = nodo.getHijos().get(division.get(pos_division));
                        if(nodo == null) return false;
                        ++pos_division;
                    }

                    // Si vamos a una posición que no tiene una ficha colocada
                    else {
                        if(!mirarNuevasPalabrasHorizontal(tablero, division.get(pos_division),x,col)) return false;
                        ++pos_division;
                    }
                }
            }
            else if("vertical".equals(modo)) {
                if(!cabePalabraVertical(division,x,y)) return false;

                // Desde esa posición ir poniendo las letras en el tablero, teniendo en cuenta que algunas letras pueden estar ya en el tablero
                NodoDawg nodo = getRoot();
                int pos_division = 0;

            for(int fil = x; fil < x + size && casillaCorrecta(fil,y); fil++) {
                // Si vamos a una posición que tiene una ficha ya colocada
                if(tablero.getFicha(fil,y) != null) {
                    if (!tablero.getFicha(fil,y).getLetra().equals(division.get(pos_division))) {
                        return false;
                    }
                    nodo = nodo.getHijos().get(division.get(pos_division));
                    if(nodo == null) return false;
                    ++pos_division;
                }


                    // Si vamos a una posición que no tiene una ficha colocada
                    else {
                        if(!mirarNuevasPalabrasVertical(tablero, division.get(pos_division),fil,y)) return false;
                        ++pos_division;
                    }
                }
            }
        }

        // Si hay ficha colocada en la casilla, ir hasta el final de la palabra para ver si se puede extender
        else {
            if(modo.equals("horizontal")){
                if(!cabePalabraHorizontal(division,x,y)) return false;
                NodoDawg nodo = getRoot();
                int pos_division = 0;

                // Recorre la semi palabra del tablero y acabamos teniendo el nodo de la ultima casilla de esta
                for(int col = y; tablero.getFicha(x,col) != null && casillaCorrecta(x,col); col++) {
                    nodo = nodo.getHijos().get(division.get(pos_division));
                    pos_division++;
                }

                // Mirar por cada letra que le quede a division, que pueda extenderse
                // Si la casilla es vacia, mirar si extiende palabras desde arriba o abajo
                // Si la casilla esta ocupada, ver si esta es la misma que la letra que toca

                for(int col = y + pos_division; col < y + size && casillaCorrecta(x,col); col++) {
                    // En el caso de que no haya una ficha en esa nueva posicion
                    if(tablero.getFicha(x,col) == null) {
                        // Mirar por posibles nuevas palabras arriba y abajo
                        if(!mirarNuevasPalabrasHorizontal(tablero, division.get(pos_division),x,col)) return false;
                        pos_division++;
                    }

                    // En el caso de que ya haya una ficha en esa nueva posicion
                    else {
                        // Si la letra del tablero coincide con la letra de la palabra
                        String letra = tablero.getFicha(x,col).getLetra();
                        if(letra != division.get(pos_division)) return false;
                        else {
                            nodo = nodo.getHijos().get(division.get(pos_division));
                            pos_division++;
                        }
                    }
                }
            }

            else if(modo.equals("vertical")) {
                if(!cabePalabraVertical(division,x,y)) return false;
                NodoDawg nodo = getRoot();
                int pos_division = 0;

                // Recorre la semi palabra del tablero y acabamos teniendo el nodo de la ultima casilla de esta
                for(int fil = x; tablero.getFicha(fil,y) != null && casillaCorrecta(fil,y); fil++) {
                    nodo = nodo.getHijos().get(division.get(pos_division));
                    pos_division++;
                }

                // Mirar por cada letra que le quede a division, que pueda extenderse
                // Si la casilla es vacia, mirar si extiende palabras desde arriba o abajo
                // Si la casilla esta ocupada, ver si esta es la misma que la letra que toca

                for(int fil = x + pos_division; fil < x + size && casillaCorrecta(fil,y); fil++) {
                    // En el caso de que no haya una ficha en esa nueva posición
                    if(tablero.getFicha(fil,y) == null) {
                        // Mirar por posibles nuevas palabras arriba y abajo
                        if(!mirarNuevasPalabrasVertical(tablero, division.get(pos_division),fil,y)) return false;
                        ++pos_division;
                    }

                    // En el caso de que ya haya una ficha en esa nueva posición
                    else {
                        // Si la letra del tablero coincide con la letra de la palabra
                        String letra = tablero.getFicha(fil,y).getLetra();
                        if(letra != division.get(pos_division)) return false;
                        else {
                            nodo = nodo.getHijos().get(division.get(pos_division));
                            ++pos_division;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Función que comprueba que la colocación (en horizontal) de la letra en una posición del tablero es correcta
     * @param tablero
     * @param letra
     * @param x
     * @param y
     * @return
     * @author Arnau Miret Barrull
     */
    private boolean mirarNuevasPalabrasHorizontal(Tablero tablero, String letra, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);

        // En el caso de que la casilla de arriba esté ocupada, ver si se crea una palaba correcta
        if(casillaCorrecta(x-1,y)) {
            if(tablero.getFicha(x-1,y) != null) {
                // Ir hacia arriba hasta el principio de la palabra y mirar que sea correcta
                int fil = x;
                while(tablero.getFicha(fil-1,y) != null && casillaCorrecta(fil-1,y)) fil--;

                if(!palabraVerticalCorrecta(tablero,fil,y,x,letra)) return false;
            }
        }

        // En el caso de que solo la casilla de abajo esté ocupada, ver si crea una palabra correcta
        else if(casillaCorrecta(x+1,y)) {
            if(tablero.getFicha(x+1,y) != null) {
                // Ir hacia abajo para comprobar que la palabra existe
                if(!palabraVerticalCorrecta(tablero,x,y,x,letra)) return false;
            }
        }
        return true;
    }

    /**
     *  Función que comprueba que la colocación (en vertical) de la letra en una posición del tablero es correcta
     * @param tablero
     * @param letra
     * @param x
     * @param y
     * @return
     * @throws CoordenadaFueraDeRangoException
     * @author Arnau Miret Barrull
     */
    private boolean mirarNuevasPalabrasVertical(Tablero tablero, String letra, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);

        // En el caso de que la casilla de la izquierda esté ocupada, ver si se crea una palabra correcta
        if(casillaCorrecta(x,y-1)) {
            if(tablero.getFicha(x,y-1) != null) {
                int col = y;
                while(tablero.getFicha(x,col-1) != null && casillaCorrecta(x,col-1)) col--;

                if(!palabraHorizontalCorrecta(tablero,x,col,y,letra)) return false;
            }

        }
        else if(casillaCorrecta(x,y+1)) {
            if(tablero.getFicha(x,y+1) != null) {
                // Ir hacia la derecha para comprobar que la palabra existe
                if(!palabraHorizontalCorrecta(tablero,x,y,y,letra)) return false;
            }
        }
        return true;
    }

    /**
     *  Función que comprueba si la palabra que se forma verticalmente en el tablero con la nueva letra es correcta
     * @param tablero
     * @param x
     * @param y
     * @param xletra
     * @param letra
     * @return
     * @throws CoordenadaFueraDeRangoException
     * @author Arnau Miret Barrull
     */
    private boolean palabraVerticalCorrecta(Tablero tablero, int x, int y, int xletra, String letra) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);

        NodoDawg nodo = getRoot();
        for(int fil = x; tablero.getFicha(fil,y) != null && casillaCorrecta(fil,y); fil++) {
            if(fil == xletra) {
                nodo = nodo.getHijos().get(letra);
            }
            else nodo = nodo.getHijos().get(tablero.getFicha(fil,y).getLetra());

            if(nodo == null) return false;
        }
        return true;
    }

    /**
     *  Función que comprueba si la palabra que se forma horizontalmente en el tablero con la nueva letra es correcta
     * @param tablero
     * @param x
     * @param y
     * @param yletra
     * @param letra
     * @return
     * @throws CoordenadaFueraDeRangoException
     * @author Arnau Miret Barrull
     */
    private boolean palabraHorizontalCorrecta(Tablero tablero, int x, int y, int yletra, String letra) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);

        NodoDawg nodo = getRoot();
        for(int col = y; tablero.getFicha(x,col) != null && casillaCorrecta(x,col); col++) {
            if(col == yletra) {
                nodo = nodo.getHijos().get(letra);
            }
            else nodo = nodo.getHijos().get(tablero.getFicha(x,col).getLetra());

            if(nodo == null) return false;
        }
        return true;
    }

    private boolean cabePalabraVertical(List<String> divisiones, int x, int y) {
        int size = divisiones.size();
        for(int fil = x; fil < x + size; fil++) {
            if(!casillaCorrecta(fil,y)) return false;
        }
        return true;
    }

    private boolean cabePalabraHorizontal(List<String> divisiones, int x, int y) {
        int size = divisiones.size();
        for(int col = y; col < y + size; col++) {
            if(!casillaCorrecta(x,col)) return false;
        }
        return true;
    }

    /**
     * Función para saber si una casilla está dentro del tablero o no
     * @param x
     * @param y
     * @return
     */
    private boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < FILAS && y >= 0 && y < COLUMNAS;
    }

    /**
     *
     * @return
     * @author Albert Aulet Niubó
     */
    public int getNumeroNodes() {
        Set<NodoDawg> visitados = new HashSet<>();
        Deque<NodoDawg> pila = new ArrayDeque<>();
        pila.push(getRoot());
        int contador = 0;

        while (!pila.isEmpty()) {
            NodoDawg nodo = pila.pop();
            if (visitados.add(nodo)) {
                contador++;
                for (NodoDawg hijo : nodo.getHijos().values()) {
                    pila.push(hijo);
                }
            }
        }

        return contador;
    }
}