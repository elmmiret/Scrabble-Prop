package gestordepartida;
import algorisme.AlfabetoCAST;
import algorisme.AlfabetoCAT;
import algorisme.AlfabetoING;
import exceptions.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;

/**
 * Esta clase representa el tablero de Scrabble.
 * Gestiona la colocación de fichas, los modificadores de puntuación y la visualización del tablero.
 * El tablero es una cuadrícula de 15x15 casillas con modificadores predefinidos según las reglas del Scrabble.
 * Soporta múltiples idiomas (CAST, CAT, ENG) para la visualización de la leyenda.
 *
 * @author Paula Pérez Chia
 * @author Albert Aulet Niubó (método transponerTablero)
 */
public class Tablero {
    /**
     * Estructura principal del tablero.
     * Cada casilla almacena una ficha con su modificador (si existe) y un conjunto de letras del abecedario.
     */
    private List<List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>>> tablero;

    /** Número de filas del tablero (valor fijo 15). */
    public static final int FILAS = 15;

    /** Número de columnas del tablero (valor fijo 15). */
    public static final int COLUMNAS = 15;

    /** Idioma de la partida, que afecta a la leyenda y alfabeto utilizado. */
    private Partida.Idioma idiomaPartida;

    /** Conjunto de letras válidas según el idioma seleccionado. */
    private Set<String> letras;

    /**
     * Tipos de modificadores de puntuación para las casillas del tablero.
     * <ul>
     *   <li>dobleTantoDeLetra: Duplica el valor de la ficha en esta casilla.</li>
     *   <li>tripleTantoDeLetra: Triplica el valor de la ficha en esta casilla.</li>
     *   <li>dobleTantoDePalabra: Duplica el valor total de la palabra que pasa por esta casilla.</li>
     *   <li>tripleTantoDePalabra: Triplica el valor total de la palabra que pasa por esta casilla.</li>
     * </ul>
     */
    public enum TipoModificador {   // si esta null es que no hay ningun bonificador en esa casilla
        dobleTantoDeLetra, tripleTantoDeLetra, dobleTantoDePalabra, tripleTantoDePalabra
    }

    /**
     * Construye un tablero vacío inicializado con los modificadores según el diseño estándar del Scrabble.
     * Configura el alfabeto según el idioma proporcionado.
     *
     * @param idiomaPartida Idioma de la partida (CAST, CAT, ENG), determina las letras válidas y la leyenda.
     */
    public Tablero(Partida.Idioma idiomaPartida) {
        tablero = new ArrayList<>();
        this.idiomaPartida = idiomaPartida;
        letras = new HashSet<>();
        Map<Ficha,Integer> mapaFichas;
        switch (idiomaPartida) {
            case CAT:
                AlfabetoCAT alfabetoCat = new AlfabetoCAT();
                mapaFichas = alfabetoCat.getMapaFichas();
                setLetras(mapaFichas);
                break;
            case CAST:
                AlfabetoCAST alfabetoCast = new AlfabetoCAST();
                mapaFichas = alfabetoCast.getMapaFichas();
                setLetras(mapaFichas);
                break;
            case ENG:
                AlfabetoING alfabetoING = new AlfabetoING();
                mapaFichas = alfabetoING.getMapaFichas();
                setLetras(mapaFichas);
                break;
            default:
                break;
        }

        montarTablero();
    }

    /**
     * Inicializa el conjunto de letras válidas según el idioma.
     * @param mapaFichas Mapa de fichas del alfabeto correspondiente.
     */
    public void setLetras(Map<Ficha, Integer> mapaFichas) {
        for (Map.Entry<Ficha, Integer> entry : mapaFichas.entrySet()) {
            Ficha ficha = entry.getKey();
            String letra = ficha.getLetra();
            letras.add(letra);
        }
    }

    /**
     * Configura la estructura del tablero con los modificadores en sus posiciones estándar.
     * Distribución:
     * <ul>
     *   <li>Triple palabra: Esquinas y centro de los bordes.</li>
     *   <li>Doble palabra: Diagonales excepto centro.</li>
     *   <li>Triple letra: Posiciones específicas (ej: (5,1), (1,5)).</li>
     *   <li>Doble letra: Dispersas simétricamente.</li>
     * </ul>
     */
    private void montarTablero () {
        TipoModificador m;
        for (int i = 0; i < FILAS; i++) {
            List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                m = null;
                // triple palabra
                if ((i == 0 || i == 7 || i == 14) && (j == 0 || j == 7 || j == 14) && !(i == 7 && j == 7)) {
                    m = TipoModificador.tripleTantoDePalabra;
                }
                // doble palabra
                else if ((i == j || i + j == 14) && i != 0 && i != 7 && i != 14) {
                    m = TipoModificador.dobleTantoDePalabra;
                }
                // triple letra
                else if ((i == 5 || i == 9) && (j == 1 || j == 5 || j == 9 || j == 13) ||
                        (i == 1 || i == 5 || i == 9 || i == 13) && (j == 5 || j == 9)) {
                    m = TipoModificador.tripleTantoDeLetra;
                }
                // doble letra
                else if ((i == 0 && (j == 3 || j == 11)) ||
                        (i == 2 && (j == 6 || j == 8)) ||
                        (i == 3 && (j == 0 || j == 7 || j == 14)) ||
                        (i == 6 && (j == 2 || j == 6 || j == 8 || j == 12)) ||
                        (i == 7 && (j == 3 || j == 11)) ||
                        (i == 8 && (j == 2 || j == 6 || j == 8 || j == 12)) ||
                        (i == 11 && (j == 0 || j == 7 || j == 14)) ||
                        (i == 12 && (j == 6 || j == 8)) ||
                        (i == 14 && (j == 3 || j == 11))) {
                    m = TipoModificador.dobleTantoDeLetra;
                }

                SimpleEntry<Ficha, TipoModificador> fichaYModificador = new SimpleEntry<>(null, m);
                Set<String> abecedario = new HashSet<>();
                fila.add(new SimpleEntry<>(fichaYModificador, abecedario));
            }
            tablero.add(fila);
        }
    }

    // MÉTODOS

    /**
     * Obtiene la ficha en la posición (x, y).
     *
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @return Ficha en la posición o {@code null} si está vacía.
     * @throws CoordenadaFueraDeRangoException Si x o y están fuera del rango 0-14.
     */
    public Ficha setLetras( int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getKey();
    }


    /**
     * Obtiene el modificador de puntuación de la casilla especificada.
     *
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @return Tipo de modificador de la casilla, o {@code null} si no tiene.
     * @throws CoordenadaFueraDeRangoException Si las coordenadas están fuera del rango válido.
     */
    public TipoModificador getTipoModificador ( int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getKey().getValue();
    }

    /**
     * Obtiene el conjunto de letras asociado a la casilla (x, y).
     * Este conjunto representa caracteres válidos para restricciones de juego.
     *
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @return Set de letras (nunca {@code null}, puede estar vacío).
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas.
     */
    public Set<String> getAbecedario ( int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y).getValue();
    }

    /**
     * Coloca una ficha en la posición (x, y).
     * La casilla debe estar vacía.
     *
     * @param f Ficha a colocar.
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @throws CasillaOcupadaException Si la casilla ya contiene una ficha.
     * @throws CoordenadaFueraDeRangoException Si x o y están fuera de rango.
     */
    public void setFicha (Ficha f, int x, int y) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        if (getFicha(x, y) != null) throw new CasillaOcupadaException(x, y);
        SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>> actual = tablero.get(x).get(y);
        tablero.get(x).set(y, new SimpleEntry<>(new SimpleEntry<>(f, actual.getKey().getValue()), actual.getValue()));
    }

    /**
     * Coloca una letra en el conjunto de caracteres permitidos de una casilla.
     * Si la letra ya existe, no se realiza ninguna acción.
     *
     * @param letra Letra a añadir.
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas.
     */
    public void setLetraAbecedario(String letra, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        Set<String> abecedario = tablero.get(x).get(y).getValue();
        abecedario.add(letra);
    }

    /**
     * Elimina una letra del conjunto de caracteres permitidos de una casilla.
     * Si la letra no existe, no se realiza ninguna acción.
     *
     * @param letra Letra a eliminar.
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas.
     */
    public void borrarLetraAbecedario(String letra, int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        Set<String> abecedario = tablero.get(x).get(y).getValue();
        abecedario.remove(letra);
    }

    /**
     * Vacía el conjunto de letras permitidas en una casilla.
     *
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas.
     */
    public void clearAbecedario(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        Set<String> abecedario = tablero.get(x).get(y).getValue();
        abecedario.clear();
    }

    /**
     * Obtiene toda la información de una casilla, incluyendo ficha, modificador y letras permitidas.
     *
     * @param x Fila (0-14).
     * @param y Columna (0-14).
     * @return Entrada que contiene:
     *         - Ficha y modificador (pueden ser {@code null}).
     *         - Set de letras permitidas (nunca {@code null}).
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas.
     */
    public SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>> getCasilla(int x, int y) throws CoordenadaFueraDeRangoException {
        if (x < 0 || x >= FILAS || y < 0 || y >= COLUMNAS) throw new CoordenadaFueraDeRangoException(x, y);
        return tablero.get(x).get(y);
    }

    /**
     * Verifica si el tablero está completamente vacío.
     * Se considera vacío si no hay fichas colocadas y todos los conjuntos de letras están vacíos.
     *
     * @return {@code true} si el tablero está vacío, {@code false} en caso contrario.
     * @throws CoordenadaFueraDeRangoException Si hay un error al acceder a las coordenadas (improbable).
     */
    public boolean estaVacio() throws CoordenadaFueraDeRangoException {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                if (getFicha(i,j) != null) return false;
                if (getAbecedario(i,j) != null && !getAbecedario(i,j).isEmpty()) return false;
            }
        }
        return true;
    }

    /**
     * Transpone el tablero intercambiando filas por columnas, convirtiendo la posición (x, y) en (y, x).
     * Este método modifica la estructura interna del tablero para reorganizar las casillas.
     *
     */
    public void transponerTablero() {
        List<List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>>> nuevo = new ArrayList<>(COLUMNAS);
        for (int y = 0; y < COLUMNAS; y++) {
            List<SimpleEntry<SimpleEntry<Ficha, TipoModificador>, Set<String>>> fila = new ArrayList<>(FILAS);
            for (int x = 0; x < FILAS; x++) {
                fila.add(tablero.get(x).get(y));
            }
            nuevo.add(fila);
        }
        this.tablero = nuevo;
    }

    /**
     * Crea y devuelve una copia del tablero actual.
     * <p>
     * Este método genera una nueva instancia de Tablero utilizando el mismo idioma
     * que el tablero original, y copia cada ficha existente en las mismas posiciones.
     * Si ocurre una excepción durante la clonación, se imprime la traza del error en la salida estándar.
     * </p>
     *
     * @return una nueva instancia de Tablero que contiene copias de las fichas del tablero original.
     */
    public Tablero clonar() {
        Tablero copia = new Tablero(this.idiomaPartida);
        try {
            for (int i = 0; i < FILAS; i++) {
                for (int j = 0; j < COLUMNAS; j++) {
                    Ficha f = this.getFicha(i, j);
                    if (f != null) {
                        copia.setFicha(f, i, j); // Copia las fichas
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copia;
    }

    /**
     * Muestra una representación visual del tablero en la consola.
     * Incluye colores ANSI para los modificadores y una leyenda según el idioma.
     * Formato:
     * <ul>
     *   <li>Casillas vacías: "   " (3 espacios).</li>
     *   <li>Fichas: Letra centrada (ej: " A ").</li>
     *   <li>Colores: Indican el tipo de modificador (ver leyenda).</li>
     * </ul>
     * @throws CoordenadaFueraDeRangoException Si hay un error al acceder a las coordenadas.
     */
    public void imprimirTablero() throws CoordenadaFueraDeRangoException {
        // todo el texto tiene que ser de tres espacios para garantizar la representacion de todas las letras, ej: L·L
        String color, texto;
        TipoModificador m;
        Ficha f;
        // codigo ANSI para colorear el tablero
        // para los colores, si queremos cambiar el background, la secuencia que hay que seguir es: \u001B[48;2;R;G;Bm
        // el color de la letra a negro es 30 en vez de 48 y 000
        String rojo = "\u001B[48;2;255;0;0m\u001B[30m";
        String azul = "\u001B[48;2;0;0;255m\u001B[30m";
        String cielo = "\u001B[48;2;106;174;234m\u001B[30m";
        String naranja = "\u001B[48;2;245;135;15m\u001B[30m";
        String dorado = "\u001B[48;2;234;200;106m\u001B[30m"; // para el centro

        System.out.println("     1   2   3   4   5   6   7   8   9  10  11  12  13  14  15");
        for (int i = 0; i < FILAS; i++) {
            System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
            System.out.printf("%2s ", (char)('A' + i));
            System.out.print("|");
            for (int j = 0; j < COLUMNAS; j++) {
                color = "";
                texto = "   ";
                f = getFicha(i, j);
                if (f != null) texto = f.getLetra();

                if (texto.length() == 1) texto = " "+texto+" ";
                else if (texto.length() == 2) texto = texto+" ";

                m = getTipoModificador(i, j);
                if (m != null) {
                    switch (m) {
                        case dobleTantoDeLetra:
                            color = cielo;
                            break;
                        case tripleTantoDeLetra:
                            color = azul;
                            break;
                        case dobleTantoDePalabra:
                            color = naranja;
                            break;
                        case tripleTantoDePalabra:
                            color = rojo;
                            break;
                    }
                }
                if (i == FILAS/2 && j == COLUMNAS/2) {
                    color = dorado;
                }
                System.out.print(color + texto + "\u001B[0m");
                System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");

        switch (idiomaPartida) {
            case CAST:
                System.out.println();
                System.out.println("           LEYENDA");
                System.out.print(rojo + "   " + "\u001B[0m");
                System.out.println(" TRIPLE TANTO DE PALABRA");
                System.out.print(naranja + "   " + "\u001B[0m");
                System.out.println(" DOBLE  TANTO DE PALABRA");
                System.out.print(azul + "   " + "\u001B[0m");
                System.out.println(" TRIPLE TANTO DE  LETRA");
                System.out.print(cielo + "   " + "\u001B[0m");
                System.out.println(" DOBLE  TANTO DE  LETRA");
                System.out.print(dorado + "   " + "\u001B[0m");
                System.out.println(" CENTRO");
                break;
            case CAT:
                System.out.println();
                System.out.println("           LLEGENDA");
                System.out.print(rojo + "   " + "\u001B[0m");
                System.out.println(" TRIPLE TANT DE PARAULA");
                System.out.print(naranja + "   " + "\u001B[0m");
                System.out.println(" DOBLE  TANT DE PARAULA");
                System.out.print(azul + "   " + "\u001B[0m");
                System.out.println(" TRIPLE TANT DE  LLETRA");
                System.out.print(cielo + "   " + "\u001B[0m");
                System.out.println(" DOBLE  TANT DE  LLETRA");
                System.out.print(dorado + "   " + "\u001B[0m");
                System.out.println(" CENTRE");
                break;
            case ENG:
                System.out.println();
                System.out.println("        LEGEND");
                System.out.print(rojo + "   " + "\u001B[0m");
                System.out.println(" TRIPLE WORD VALUE");
                System.out.print(naranja + "   " + "\u001B[0m");
                System.out.println(" DOUBLE WORD VALUE");
                System.out.print(azul + "   " + "\u001B[0m");
                System.out.println(" TRIPLE LETTER VALUE");
                System.out.print(cielo + "   " + "\u001B[0m");
                System.out.println(" DOUBLE LETTER VALUE");
                System.out.print(dorado + "   " + "\u001B[0m");
                System.out.println(" CENTER");
                break;
            default:
                break;
        }
    }
}
