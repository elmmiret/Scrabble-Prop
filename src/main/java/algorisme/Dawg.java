package algorisme;

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

    // Funcion constructora
    public Dawg() {
        Digrafos = new HashSet<>(Arrays.asList("rr", "ny", "ll", "l·l", "ch"));
        root = new NodoDawg();
        registro = new HashMap<>();
        palabraAnterior = new ArrayList<>();
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

    public void insertarDiccionarioCatalan(Dawg dawg) {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/catalan.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                dawg.insertar(linea);
            }
            dawg.acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void insertarDiccionarioCastellano(Dawg dawg) {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/castellano.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                dawg.insertar(linea);
            }
            dawg.acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void insertarDiccionarioIngles(Dawg dawg) {
        try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/ingles.txt"))) {
            String linea;
            while((linea = entrada.readLine()) != null) {
                dawg.insertar(linea);
            }
            dawg.acabar();
            //dawg.imprimir(dawg.getRoot(), "");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }
}