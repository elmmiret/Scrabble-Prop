package algorisme;

import java.util.*;

/**
 * @author Arnau Miret Barrull
 */
public class NodoDawg {
    private Map<String, NodoDawg> hijos;
    private boolean esFinal;

    // Funcion constructora
    public NodoDawg() {
        hijos = new HashMap<>();
        esFinal = false;
    }

    // GETTERS y SETTERS

    // Indica si este nodo representa el final de una palabra
    public boolean getEsFinal(){
        return esFinal;
    }

    // Devuelve los hijos de este nodo
    public Map<String, NodoDawg> getHijos(){
        return hijos;
    }

    // Marca este nodo como final de palabra
    public void setEsFinal(boolean b) {
        this.esFinal = b;
    }

    // Necesario para comparar nodos al mizimizar el DAWG
    @Override
    public int hashCode() {
        return Objects.hash(esFinal, hijos.keySet());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NodoDawg)) return false;
        NodoDawg otronodo = (NodoDawg) obj;
        return this.esFinal == otronodo.esFinal && this.hijos.keySet().equals(otronodo.hijos.keySet());
    }
}