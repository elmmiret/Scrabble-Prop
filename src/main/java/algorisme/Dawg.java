package algorisme;
import exceptions.CoordenadaFueraDeRangoException;
import gestordepartida.Partida;
import gestordepartida.Tablero;

//import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Clase que implementa un DAWG (Directed Acyclic Word Graph) para almacenar
 * y validar palabras de forma eficiente. Soporta múltiples idiomas y maneja
 * digrafos/trigrafos en la división de palabras.
 *
 * @author Arnau Miret Barrull
 */
public class Dawg {

    /**
     * Conjunto de digrafos/trigrafos para idiomas no ingleses
     */
    private static Set<String> Digrafos;

    /**
     * Nodo raíz del grafo DAWG
     */
    private final NodoDawg root;  // Nodo raíz del DAWG

    /**
     * Registro para minimización de nodos duplicados
     */
    private final Map<NodoDawg, NodoDawg> registro;   // Mapa para evitar nodos multiplicados (minimizar)

    /**
     * Número de filas del tablero
     */
    private static final int FILAS = 15;

    /**
     * Número de columnas del tablero
     */
    private static final int COLUMNAS = 15;

    /**
     * Idioma actual para procesamiento de palabras
     */
    private static Partida.Idioma idioma;

    /**
     * Constructor que inicializa el DAWG para un idioma específico
     * @param idiomaPartida Idioma para cargar el diccionario correspondiente
     */
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

    /**
     * Obtiene el prefijo común más largo entre una palabra y las existentes en el DAWG
     * @param palabra Palabra a analizar
     * @return Lista de símbolos del prefijo común
     */
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

    /**
     * Obtiene el nodo correspondiente a una secuencia de símbolos
     * @param palabra Lista de símbolos a buscar
     * @return Nodo final de la secuencia o null si no existe
     */
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

    /**
     * Añade un sufijo a partir de un nodo dado
     * @param ultimonodo Nodo donde comenzar a añadir el sufijo
     * @param sufijoactual Secuencia de símbolos a añadir
     */
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

    /**
     * Minimiza la estructura del DAWG eliminando nodos redundantes
     * @param nodo Nodo desde el que comenzar la minimización
     */
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
     * Inserta una palabra en el DAWG
     * @param palabra Palabra a insertar
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
     * Finaliza la construcción del DAWG aplicando minimización completa
     */
    public void acabar() {
        minimizar(root);
    }

    /**
     * Divide una palabra en símbolos según las reglas del idioma
     * @param palabra Palabra a dividir
     * @return Lista de símbolos resultantes
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
     * Obtiene el nodo raíz del DAWG
     * @return Nodo raíz del grafo
     */
    public NodoDawg getRoot() {
        return root;
    }


    /**
     * Verifica la existencia de un prefijo en el DAWG
     * @param prefijo Prefijo a comprobar
     * @return true si el prefijo existe, false en caso contrario
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
     * Verifica la existencia de una palabra completa en el DAWG
     * @param palabra Palabra a comprobar
     * @return true si la palabra existe, false en caso contrario
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

    /**
     * Carga el diccionario catalán desde archivo
     */
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

    /**
     * Carga el diccionario castellano desde archivo
     */
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

    /**
     * Carga el diccionario inglés desde archivo
     */
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
     * Valida la colocación de una palabra en el tablero
     * @param tablero Tablero de juego
     * @param palabra Palabra a colocar
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     * @param modo Dirección de colocación ("horizontal"/"vertical")
     * @param esPrimerTurno Indica si es el primer turno del juego
     * @return true si la colocación es válida, false en caso contrario
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
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
     * Verifica que la colocación horizontal de una letra no forme palabras inválidas en vertical
     * @param tablero Tablero de juego
     * @param letra Letra a colocar
     * @param x Coordenada X de la posición a verificar
     * @param y Coordenada Y de la posición a verificar
     * @return true si la colocación es válida, false si forma palabras inválidas
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
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
     * Verifica que la colocación vertical de una letra no forme palabras inválidas en horizontal
     * @param tablero Tablero de juego
     * @param letra Letra a colocar
     * @param x Coordenada X de la posición a verificar
     * @param y Coordenada Y de la posición a verificar
     * @return true si la colocación es válida, false si forma palabras inválidas
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
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
     * Verifica si una posición está dentro de los límites del tablero
     * @param x Coordenada X a comprobar
     * @param y Coordenada Y a comprobar
     * @return true si la posición es válida, false en caso contrario
     */
    public boolean casillaCorrecta(Integer x, Integer y) {
        return x >= 0 && x < FILAS && y >= 0 && y < COLUMNAS;
    }



    /**
     * Valida una palabra formada horizontalmente en el tablero
     * @param tablero Tablero de juego
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     * @param yletra Posición Y de la letra recién colocada
     * @param letra Letra colocada
     * @return true si la palabra es válida, false en caso contrario
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
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

    /**
     * Comprueba si una palabra cabe verticalmente y cumple las reglas de adyacencia
     * @param tablero Tablero de juego
     * @param divisiones Lista de símbolos de la palabra
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     * @param esPrimerTurno Indica si es el primer turno del juego
     * @return true si la palabra cabe y cumple las reglas, false en caso contrario
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
     */
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

    /**
     * Comprueba si una palabra cabe horizontalmente y cumple las reglas de adyacencia
     * @param tablero Tablero de juego
     * @param divisiones Lista de símbolos de la palabra
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     * @param esPrimerTurno Indica si es el primer turno del juego
     * @return true si la palabra cabe y cumple las reglas, false en caso contrario
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del tablero
     */
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