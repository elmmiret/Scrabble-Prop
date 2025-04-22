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
    private final NodoDawg root;  // Nodo raíz del DAWG
    private final Map<NodoDawg, NodoDawg> registro;   // Mapa para evitar nodos multiplicados (minimizar)
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;
    private static Partida.Idioma idioma;

    // Funcion constructora
    public Dawg(Partida.Idioma idiomaPartida) {
        Digrafos = new HashSet<>(Arrays.asList("RR", "NY", "LL", "L·L", "CH"));
        root = new NodoDawg();
        registro = new HashMap<>();
        idioma = idiomaPartida;

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

    private List<String> getPrefijoComun(String palabra) {
        List<String> simbolos = dividirPalabra(palabra);
        List<String> prefijoComun = new ArrayList<>();
        NodoDawg nodo = root;
        for(int i = 0;  i < simbolos.size(); i++) {
            String letra = simbolos.get(i);
            if(nodo.getHijo(letra) != null) {
                prefijoComun.add(letra);
                nodo = nodo.getHijo(letra);
            }
            else break;
        }
        return prefijoComun;
    }

    public NodoDawg getNodo(List<String> palabra) {
        NodoDawg nodo = root;
        for(int i = 0; i < palabra.size(); i++) {
            String letra = palabra.get(i);
            if(nodo.getHijo(letra) != null) {
                nodo = nodo.getHijo(letra);
            }
            else return null;
        }
        return nodo;
    }

    private void anadirSufijo(NodoDawg ultimonodo, List<String> sufijoactual) {
        NodoDawg nodo = ultimonodo;
        for(int i = 0; i < sufijoactual.size(); i++) {
            String letra = sufijoactual.get(i);
            NodoDawg nuevonodo = new NodoDawg();
            nodo.anadirHijo(letra,nuevonodo);
            nodo = nuevonodo;
        }
        nodo.setEsFinal(true);
    }

    private void minimizar(NodoDawg nodo) {
        Map.Entry<String, NodoDawg> hijomasgrande = nodo.getHijoMasGrande();
        if(hijomasgrande == null) return;

        NodoDawg hijo = hijomasgrande.getValue();
        // Si tiene hijos, minimizamos recursivamente
        if(hijo.tieneHijos()) {
            minimizar(hijo);
        }

        // Si el hijo esta en el registro, el hijo se puede intercambiar por el nodo
        NodoDawg existente = registro.get(hijo);
        if(existente != null) {
            nodo.anadirHijo(hijomasgrande.getKey(),existente);
        }
        else {
            registro.put(hijo,hijo);
        }
    }

    /**
     * Función que inserta una nueva palabra String en el DAWG
     * @param palabra
     */

    public void insertar(String palabra) {
        List<String> palabradividida = dividirPalabra(palabra);
        List<String> prefijocomun = getPrefijoComun(palabra);
        NodoDawg ultimonodo = getNodo(prefijocomun);

        // Este if va a saltar si la nueva palabra tiene un sufijo diferente al de las palabras añadidas previamente
        if(ultimonodo.tieneHijos()) {
            minimizar(ultimonodo);
        }

        // Se crea una nueva rama con el sufijo que es diferente de las palabras anteriores
        // Esta rama se minimizara cuando entre una palabra con sufijo diferente a este
        List<String> sufijo = new ArrayList<>();
        for(int i = prefijocomun.size(); i < palabradividida.size(); i++) {
            sufijo.add(palabradividida.get(i));
        }
        anadirSufijo(ultimonodo,sufijo);
    }

    /**
     * Función que finaliza la construcción del DAWG minimizando todos los nodos restantes
     */
    public void acabar() {
        minimizar(root);
    }

    /**
     * Función para dividir una palabra en String a una Lista de Strings con sus letras
     * @param palabra
     * @return
     */
    public List<String> dividirPalabra(String palabra) {
        List<String> division = new ArrayList<>();
        if(idioma != Partida.Idioma.ENG) {
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
        }
        else{
            for(int i = 0; i < palabra.length(); i++) {
                division.add(String.valueOf(palabra.charAt(i)));
            }
        }
        return division;
    }

    /**
     * Función para obtener el nodo raíz del DAWG
     * @return
     */
    public NodoDawg getRoot() {
        return root;
    }

    // Imprime todas las palabras representadas en el DAWG (para comprovar que funciona la implementación)
    /*public void imprimir(NodoDawg nodo, String prefijo) {
        if(nodo.getEsFinal()) System.out.println(prefijo);
        for(Map.Entry<String, NodoDawg> hijo : nodo.getHijos().entrySet()) {
            imprimir(hijo.getValue(), prefijo + hijo.getKey());
        }
    }*/

    /**
     * True o false si existe el prefijo indicado en el DAWG
     * @param prefijo
     * @return
     */

    public boolean existePrefijo(String prefijo) {
        List<String> simbolos = dividirPalabra(prefijo);
        NodoDawg nodo = root;
        for (String simbolo : simbolos) {
            nodo = nodo.getHijo(simbolo);
            if (nodo == null) return false;
        }
        return true; // El prefijo existe si se recorren todos los símbolos
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
    public boolean comprobarPalabra(Tablero tablero, String palabra, int x, int y, String modo, boolean esPrimerTurno) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        List<String> division = dividirPalabra(palabra);
        int size = division.size();
        if(!existePalabra(palabra))
        {
            System.out.println("PALABRA NO EXISTE\n");
            return false;
        }

        // Si no hay ficha colocada en la casilla, la palabra se empieza desde ahi
        if(tablero.getFicha(x,y) == null) {
            if("horizontal".equals(modo)) {
                if(!cabePalabraHorizontal(tablero, division,x,y, esPrimerTurno)) return false;

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
                if(!cabePalabraVertical(tablero, division,x,y, esPrimerTurno)) return false;

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
                if(!cabePalabraHorizontal(tablero, division,x,y, esPrimerTurno)) return false;
                NodoDawg nodo = getRoot();
                int pos_division = 0;

                // Recorre la semi palabra del tablero y acabamos teniendo el nodo de la ultima casilla de esta
                for(int col = y; tablero.getFicha(x,col) != null && casillaCorrecta(x,col); col++) {
                    String letraTablero = tablero.getFicha(x, col).getLetra();
                    if (!letraTablero.equals(division.get(pos_division))) return false; // si no es la misma letra
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
                if(!cabePalabraVertical(tablero, division,x,y, esPrimerTurno)) return false;
                NodoDawg nodo = getRoot();
                int pos_division = 0;

                // Recorre la semi palabra del tablero y acabamos teniendo el nodo de la ultima casilla de esta
                for(int fil = x; tablero.getFicha(fil,y) != null && casillaCorrecta(fil,y); fil++) {
                    String letraTablero = tablero.getFicha(fil, y).getLetra();
                    if (!letraTablero.equals(division.get(pos_division))) return false; // si no es la misma letra
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
        StringBuilder palabra = new StringBuilder();
        int fila = x;

        // Recorre hacia arriba
        int f = fila - 1;
        while (f >= 0 && tablero.getFicha(f, y) != null) {
            palabra.insert(0, tablero.getFicha(f, y).getLetra());
            f--;
        }

        // Añade la letra que se quiere colocar
        palabra.append(letra);

        // Recorre hacia abajo
        f = fila + 1;
        while (f < FILAS && tablero.getFicha(f, y) != null) {
            palabra.append(tablero.getFicha(f, y).getLetra());
            f++;
        }

        // Si se forma una palabra de más de una letra, comprobar si es válida
        if (palabra.length() > 1) {
            System.out.println("Palabra formada en vertical (por horizontal): " + palabra);
            return existePalabra(palabra.toString());
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
        StringBuilder palabra = new StringBuilder();
        int columna = y;

        // Recorre hacia la izquierda
        int c = columna - 1;
        while (c >= 0 && tablero.getFicha(x, c) != null) {
            palabra.insert(0, tablero.getFicha(x, c).getLetra());
            c--;
        }

        // Añade la letra que se quiere colocar
        palabra.append(letra);

        // Recorre hacia la derecha
        c = columna + 1;
        while (c < COLUMNAS && tablero.getFicha(x, c) != null) {
            palabra.append(tablero.getFicha(x, c).getLetra());
            c++;
        }

        // Si se forma una palabra de más de una letra, comprobar si es válida
        if (palabra.length() > 1) {
            System.out.println("Palabra formada en horizontal (por vertical): " + palabra);
            return existePalabra(palabra.toString());
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

    private boolean cabePalabraVertical(Tablero tablero, List<String> divisiones, int x, int y, boolean esPrimerTurno) throws CoordenadaFueraDeRangoException{
        int size = divisiones.size();
        Integer[] X = {1, 1, 0, 0};
        Integer[] Y = {0, 0, 1, 1};
        boolean adyacente_a_algo = false;

        for (int fil = x; fil < x + size; fil++) {  // recorrer desde x hasta x + tamaño de palabra
            if (!casillaCorrecta(fil, y)) return false;

            if (tablero.getFicha(fil, y) != null) adyacente_a_algo = true;

            for (int i = 0; i < 4 && !adyacente_a_algo; i++) {
                if (casillaCorrecta(fil + X[i], y + Y[i])) {
                    if (tablero.getFicha(fil + X[i], y + Y[i]) != null) adyacente_a_algo = true;
                }
            }
        }

        return esPrimerTurno || adyacente_a_algo;

    }

    private boolean cabePalabraHorizontal(Tablero tablero, List<String> divisiones, int x, int y, boolean esPrimerTurno) throws CoordenadaFueraDeRangoException{
        int size = divisiones.size();
        Integer[] X = {1, 1, 0, 0};
        Integer[] Y = {0, 0, 1, 1};
        boolean adyacente_a_algo = false;

        for (int col = y; col < y + size; col++) {  // recorrer desde y hasta y + tamaño de palabra
            if (!casillaCorrecta(x, col)) return false;

            if (tablero.getFicha(x, col) != null) adyacente_a_algo = true;

            for (int i = 0; i < 4 && !adyacente_a_algo; i++) {
                if (casillaCorrecta(x + X[i], col + Y[i])) {
                    if (tablero.getFicha(x + X[i], col + Y[i]) != null) adyacente_a_algo = true;
                }
            }
        }

        return esPrimerTurno || adyacente_a_algo;

    }

}