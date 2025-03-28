package algorisme;

import java.lang.reflect.Array;
import java.util.*;

public class Dawg {
    private NodoDawg root;
    private Map<NodoDawg, NodoDawg> nodosMinimizados; //almacena nodos minimizados
    private static final List<String> Digrafos = Arrays.asList("rr", "ny", "ll", "l·l", "ch");

    public Dawg(){
        root = new NodoDawg();
        nodosMinimizados = new HashMap<>();
    }

    //función para insertar una palabra en el DAWG
    public void insertar(String palabra) {
        List<String> tokens = dividirDigrafos(palabra);
        root = insertarImplementacion(root, tokens, 0);
    }

    private NodoDawg insertarImplementacion(NodoDawg nodo, List<String> tokens, int indice) {
        if(indice == tokens.size()) {
            nodo.setEsFinal(true);
            return minimizar(nodo);
        }

        String token = tokens.get(indice);
        NodoDawg hijo = nodo.getHijos().get(token);
        if(hijo == null) {
            hijo = new NodoDawg();
            nodo.getHijos().put(token, hijo);
        }

        nodo.getHijos().put(token, insertarImplementacion(hijo, tokens, indice + 1));
        return minimizar(nodo);
    }

    //minimiza un nodo fusionando nodos equivalentes
    private NodoDawg minimizar(NodoDawg nodo) {
        NodoDawg nodoEquivalente = encuentraNodoEquivalente(nodo);
        if(nodoEquivalente != null) {
            return nodoEquivalente;
        }
        nodosMinimizados.put(nodo, nodo);
        return nodo;
    }

    //verifica si un prefijo existente en el DAWG
    public NodoDawg encuentraNodoEquivalente(NodoDawg nodo) {
        for(NodoDawg nodoExistente : nodosMinimizados.keySet()) {
            if(nodoExistente.equals(nodo)){
                return nodoExistente;
            }
        }
        return null;
    }

    //función para saber si existe una palabra en el DAWG
    public boolean buscar(String palabra) {
        List<String> tokens = dividirDigrafos(palabra);
        NodoDawg actual = root;
        for(String token : tokens) {
            actual = actual.getHijos().get(token);
            if(actual == null) {
                return false;
            }
        }
        return actual.getEsFinal();
    }

    // función para saber si existe el prefijo en el DAWG
    public boolean empiezaCon(String prefijo) {
        List<String> tokens = dividirDigrafos(prefijo);
        NodoDawg actual = root;
        for(String token : tokens) {
            actual = actual.getHijos().get(token);
            if(actual == null) {
                return false;
            }
        }
        return true;
    }

    //función para dividir la palabra insertada en strings, teniendo en cuenta los digrafos
    private List<String> dividirDigrafos(String palabra) {
        List<String> res = new ArrayList<>();
        //se recorre la palabra caracter por caracter
        for (int i = 0; i < palabra.length(); i++) {

            //si la palabra puede contener un digrafo de dos caracteres
            if (i < palabra.length() - 1) {
                String digrafo = palabra.substring(i, i + 2);
                if (Digrafos.contains(digrafo)) {
                    res.add(digrafo);
                    i++;
                    continue;
                }
            }

            //si la palabra puede contener un digrafo de tres caracteres
            if (i < palabra.length() - 2) {
                String digrafo = palabra.substring(i, i + 3);
                if (Digrafos.contains(digrafo)) {
                    res.add(digrafo);
                    i += 2;
                    continue;
                }
            }

            res.add(String.valueOf(palabra.charAt(i)));

        }
        return res;
    }
}