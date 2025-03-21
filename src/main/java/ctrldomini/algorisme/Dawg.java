package ctrldomini.algorisme;

import java.util.*;

public class Dawg {
    private NodoDawg root;
    private Map<NodoDawg, NodoDawg> nodosMinimizados; //almacena nodos minimizados

    public Dawg(){
        root = new NodoDawg();
        nodosMinimizados = new HashMap<>();
    }

    //función para insertar una palabra en el DAWG
    public void insertar(String palabra) {
        root = insertarImplementacion(root, palabra, 0);
    }

    private NodoDawg insertarImplementacion(NodoDawg nodo, String palabra, int indice) {
        if(indice == palabra.length()) {
            nodo.setEsFinal(true);
            return minimizar(nodo);
        }

        char ch = palabra.charAt(indice);
        NodoDawg hijo = nodo.getHijos().get(ch);
        if(hijo == null) {
            hijo = new NodoDawg();
            nodo.getHijos().put(ch, hijo);
        }

        nodo.getHijos().put(ch, insertarImplementacion(hijo, palabra, indice + 1));
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

    //función para buscar una palabra en el DAWG
    public boolean buscar(String palabra) {
        NodoDawg actual = root;
        for(char ch : palabra.toCharArray()) {
            actual = actual.getHijos().get(ch);
            if(actual == null) {
                return false;
            }
        }
        return actual.getEsFinal();
    }

    public boolean empiezaCon(String prefijo) {
        NodoDawg actual = root;
        for(char ch : prefijo.toCharArray()) {
            actual = actual.getHijos().get(ch);
            if(actual == null) {
                return false;
            }
        }
        return true;
    }


}