package algorisme;
import exceptions.CoordenadaFueraDeRangoException;
import gestordepartida.Ficha;
import gestordepartida.Partida;
import gestordepartida.Tablero;

//import java.lang.reflect.Array;
import java.awt.color.ICC_ColorSpace;
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
            int i = 0;
            while (i < palabra.length()) {
                boolean digrafoEncontrado = false;

                // Buscar digrafos/trigrafos más largos primero (3 letras, luego 2)
                for (int l = Math.min(3, palabra.length() - i); l >= 2; l--) {
                    String sub = palabra.substring(i, i + l);
                    if (Digrafos.contains(sub)) {
                        division.add(sub);
                        i += l;
                        digrafoEncontrado = true;
                        break;
                    }
                }

                if (!digrafoEncontrado) {
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
        if(x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) {
            throw new CoordenadaFueraDeRangoException(x, y);
        }

        List<String> division = dividirPalabra(palabra);
        int size = division.size();

        // Primero verificar que la palabra existe en el diccionario
        if(!existePalabra(palabra)) {
            System.out.println("Palabra no existe en el diccionario: " + palabra);
            return false;
        }

        // Verificar que la palabra cabe en la dirección especificada
        boolean cabe;
        if(modo.equals("horizontal")) {
            cabe = cabePalabraHorizontal(tablero, division, x, y, esPrimerTurno);
        }
        else if(modo.equals("vertical")) {
            cabe = cabePalabraVertical(tablero, division, x, y, esPrimerTurno);
        }
        else {
            return false;
        }

        if(!cabe) {
            System.out.println("Palabra no cabe en la posición/dirección especificada");
            return false;
        }

        // Verificar todas las letras a colocar y palabras formadas
        for(int i = 0; i < size; i++) {
            int actualX = modo.equals("horizontal") ? x : x + i;
            int actualY = modo.equals("horizontal") ? y + i : y;

            // si la casilla está vacia, verificar palabras cruzadas
            if(tablero.getFicha(actualX, actualY) == null) {
                // verificar palabra principal en la dirección opuesta
                boolean palabraCruzadaValida;
                if(modo.equals("horizontal")) {
                    palabraCruzadaValida = mirarNuevasPalabrasVertical(tablero, division.get(i), actualX, actualY);
                }
                else {
                    palabraCruzadaValida = mirarNuevasPalabrasHorizontal(tablero, division.get(i), actualX, actualY);
                }

                if(!palabraCruzadaValida) {
                    System.out.println("Palabra cruzada inválida en (" + actualX + "," + actualY + ")");
                }
            }
            else {
                // Si la casilla no está vacia, verificar que la letra coincide
                if(!tablero.getFicha(actualX, actualY).getLetra().equals(division.get(i))) {
                    System.out.println("Letra en (" + actualX + "," + actualY + ") no coincide");
                    return false;
                }
            }
        }

        // Verificar la palabra completa en la dirección principal
        StringBuilder palabraCompleta = new StringBuilder();
        int inicio, fin;
        int longitudVerificada = 0;

        // Construir la palabra completa en la dirección principal
        if(modo.equals("horizontal")) {
            // hacia la izquierda
            inicio = y - 1;
            while(inicio >= 0 && tablero.getFicha(x, inicio) != null) {
                palabraCompleta.insert(0, tablero.getFicha(x, inicio).getLetra());
               inicio--;
               longitudVerificada++;
            }

            // Palabra que queremos colocar
            for (String letra : division) {
                palabraCompleta.append(letra);
                longitudVerificada++;
            }

            // hacia derecha
            fin = y + division.size();
            while (fin < COLUMNAS && tablero.getFicha(x, fin) != null) {
                palabraCompleta.append(tablero.getFicha(x, fin).getLetra());
                fin++;
                longitudVerificada++;
            }
        }
        else { // vertical
            // Hacia arriba
            inicio = x - 1;
            while (inicio >= 0 && tablero.getFicha(inicio, y) != null) {
                palabraCompleta.insert(0, tablero.getFicha(inicio, y).getLetra());
                inicio--;
                longitudVerificada++;
            }

            // Palabra que queremos colocar
            for (String letra : division) {
                palabraCompleta.append(letra);
                longitudVerificada++;
            }

            // Hacia abajo
            fin = x + division.size();
            while (fin < FILAS && tablero.getFicha(fin, y) != null) {
                palabraCompleta.append(tablero.getFicha(fin, y).getLetra());
                fin++;
                longitudVerificada++;
            }
        }

        // Solo verificar si se forma una palaba de más de una letra
        if(longitudVerificada > 1 && !existePalabra(palabraCompleta.toString())) {
            System.out.println("Palabra completa formada no válida: " + palabraCompleta);
            return false;
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
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) {
            throw new CoordenadaFueraDeRangoException(x, y);
        }


        // Solo verifica si hay letras a la izquierda o derecha
        boolean tieneIzq = (y > 0 && tablero.getFicha(x, y - 1) != null);
        boolean tieneDer = (y < COLUMNAS - 1 && tablero.getFicha(x, y + 1) != null);

        if (!tieneIzq && !tieneDer) {
            return true;
        }

        StringBuilder palabraHorizontal = new StringBuilder();

        // Recoger letras hacia izquierda
        int col = y - 1;
        while (col >= 0 && tablero.getFicha(x, col) != null) {
            palabraHorizontal.insert(0, tablero.getFicha(x, col).getLetra());
            col--;
        }

        // Añadir la nueva letra
        palabraHorizontal.append(letra);

        // Recoger letras hacia derecha
        col = y + 1;
        while (col < COLUMNAS && tablero.getFicha(x, col) != null) {
            palabraHorizontal.append(tablero.getFicha(x, col).getLetra());
            col++;
        }

        return palabraHorizontal.length() <= 1 || existePalabra(palabraHorizontal.toString());



        /*StringBuilder palabra = new StringBuilder();

        // Recorre hacia arriba
        int f = x - 1;
        while (f >= 0 && tablero.getFicha(f, y) != null) {
            palabra.insert(0, tablero.getFicha(f, y).getLetra());
            f--;
        }

        // Añade la letra que se quiere colocar
        palabra.append(letra);

        // Recorre hacia abajo
        f = x + 1;
        while (f < FILAS && tablero.getFicha(f, y) != null) {
            palabra.append(tablero.getFicha(f, y).getLetra());
            f++;
        }

        // Si se forma una palabra de más de una letra, comprobar si es válida
        if (palabra.length() > 1) return existePalabra(palabra.toString());
        return true;*/
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
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) {
            throw new CoordenadaFueraDeRangoException(x, y);
        }

        // Solo verificar si hay letras arriba o abajo
        boolean tieneArriba = (x > 0 && tablero.getFicha(x - 1, y) != null);
        boolean tieneAbajo = (x < FILAS - 1 && tablero.getFicha(x + 1, y) != null);

        if(!tieneArriba && !tieneAbajo) {
            return true;
        }

        StringBuilder palabraVertical = new StringBuilder();

        // Recoger letras hacia arriba
        int fila = x - 1;
        while(fila >= 0 && tablero.getFicha(fila, y) != null) {
            palabraVertical.insert(0, tablero.getFicha(fila, y).getLetra());
            fila--;
        }

        // Añadir la nueva letra
        palabraVertical.append(letra);

        // Recoger letras hacia abajo
        fila = x + 1;
        while(fila < FILAS && tablero.getFicha(fila, y) != null) {
            palabraVertical.append(tablero.getFicha(fila, y).getLetra());
            fila++;
        }

        // Solo verificar si se forma palabra de 2+ letras
        return palabraVertical.length() <= 1 || existePalabra(palabraVertical.toString());

        /*StringBuilder palabra = new StringBuilder();

        // Recorre hacia la izquierda
        int c = y - 1;
        while (c >= 0 && tablero.getFicha(x, c) != null) {
            palabra.insert(0, tablero.getFicha(x, c).getLetra());
            c--;
        }

        // Añade la letra que se quiere colocar
        palabra.append(letra);

        // Recorre hacia la derecha
        c = y + 1;
        while (c < COLUMNAS && tablero.getFicha(x, c) != null) {
            palabra.append(tablero.getFicha(x, c).getLetra());
            c++;
        }

        // Si se forma una palabra de más de una letra, comprobar si es válida
        if (palabra.length() > 1) {
            System.out.println("Palabra formada en horizontal (por vertical): " + palabra);
            return existePalabra(palabra.toString());
        }
        return true;*/

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
    private boolean cabePalabraVertical(Tablero tablero, List<String> divisiones, int x, int y, boolean esPrimerTurno) throws CoordenadaFueraDeRangoException {
        int size = divisiones.size();

        // Verificar que la palabra cabe en el tablero
        if (x < 0 || y < 0 || x + size > FILAS) {
            System.out.println("La palabra no cabe verticalmente en (" + x + "," + y + ")");
            return false;
        }

        // En primer turno debe pasar por el centro (7,7)
        if (esPrimerTurno) {
            boolean pasaPorCentro = false;
            for (int i = 0; i < size; i++) {
                if ((x + i) == FILAS/2 && y == COLUMNAS/2) {
                    pasaPorCentro = true;
                    break;
                }
            }
            if (!pasaPorCentro) {
                System.out.println("Primer turno: la palabra debe pasar por el centro");
                return false;
            }
        }

        // Verificar conexión con otras fichas (excepto en primer turno)
        if (!esPrimerTurno) {
            boolean conectada = false;

            // Verificar si conecta con fichas existentes en la misma columna
            for (int i = 0; i < size; i++) {
                // Casilla actual
                if (tablero.getFicha(x + i, y) != null) {
                    conectada = true;
                    break;
                }

                // Casillas adyacentes (izquierda y derecha)
                if ((y > 0 && tablero.getFicha(x + i, y - 1) != null) ||
                        (y < COLUMNAS - 1 && tablero.getFicha(x + i, y + 1) != null)) {
                    conectada = true;
                    break;
                }
            }

            if (!conectada) {
                System.out.println("La palabra no conecta con fichas existentes");
                return false;
            }
        }

        // Verificar que las letras existentes coinciden
        for (int i = 0; i < size; i++) {
            Ficha fichaExistente = tablero.getFicha(x + i, y);
            if (fichaExistente != null && !fichaExistente.getLetra().equals(divisiones.get(i))) {
                System.out.println("Letra existente en (" + (x + i) + "," + y + ") no coincide");
                return false;
            }
        }

        return true;
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

        // Verificar que la palabra cabe en el tablero
        if (x < 0 || y < 0 || y + size > COLUMNAS) {
            System.out.println("La palabra no cabe horizontalmente en (" + x + "," + y + ")");
            return false;
        }

        // En primer turno debe pasar por el centro (7,7)
        if (esPrimerTurno) {
            boolean pasaPorCentro = false;
            for (int i = 0; i < size; i++) {
                if (x == FILAS/2 && (y + i) == COLUMNAS/2) {
                    pasaPorCentro = true;
                    break;
                }
            }
            if (!pasaPorCentro) {
                System.out.println("Primer turno: la palabra debe pasar por el centro");
                return false;
            }
        }

        // Verificar conexión con otras fichas (excepto en primer turno)
        if (!esPrimerTurno) {
            boolean conectada = false;

            // Verificar si conecta con fichas existentes en la misma fila
            for (int i = 0; i < size; i++) {
                // Casilla actual
                if (tablero.getFicha(x, y + i) != null) {
                    conectada = true;
                    break;
                }

                // Casillas adyacentes (arriba y abajo)
                if ((x > 0 && tablero.getFicha(x - 1, y + i) != null) ||
                        (x < FILAS - 1 && tablero.getFicha(x + 1, y + i) != null)) {
                    conectada = true;
                    break;
                }
            }

            if (!conectada) {
                System.out.println("La palabra no conecta con fichas existentes");
                return false;
            }
        }

        // Verificar que las letras existentes coinciden
        for (int i = 0; i < size; i++) {
            Ficha fichaExistente = tablero.getFicha(x, y + i);
            if (fichaExistente != null && !fichaExistente.getLetra().equals(divisiones.get(i))) {
                System.out.println("Letra existente en (" + x + "," + (y+i) + ") no coincide");
                return false;
            }
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
     *
     * @param tablero
     * @param division
     * @param x
     * @param y
     * @param modo
     * @return
     * @throws CoordenadaFueraDeRangoException
     */
    public String construirPalabraCompleta(Tablero tablero, List<String> division, int x, int y, String modo)
            throws CoordenadaFueraDeRangoException {

        StringBuilder palabraCompleta = new StringBuilder();

        if (modo.equals("horizontal")) {
            // Hacia izquierda
            int col = y - 1;
            while (col >= 0 && tablero.getFicha(x, col) != null) {
                palabraCompleta.insert(0, tablero.getFicha(x, col).getLetra());
                col--;
            }

            // Palabra nueva
            for (String letra : division) {
                palabraCompleta.append(letra);
            }

            // Hacia derecha
            col = y + division.size();
            while (col < COLUMNAS && tablero.getFicha(x, col) != null) {
                palabraCompleta.append(tablero.getFicha(x, col).getLetra());
                col++;
            }
        } else { // vertical
            // Hacia arriba
            int fil = x - 1;
            while (fil >= 0 && tablero.getFicha(fil, y) != null) {
                palabraCompleta.insert(0, tablero.getFicha(fil, y).getLetra());
                fil--;
            }

            // Palabra nueva
            for (String letra : division) {
                palabraCompleta.append(letra);
            }

            // Hacia abajo
            fil = x + division.size();
            while (fil < FILAS && tablero.getFicha(fil, y) != null) {
                palabraCompleta.append(tablero.getFicha(fil, y).getLetra());
                fil++;
            }
        }

        return palabraCompleta.toString();
    }

}