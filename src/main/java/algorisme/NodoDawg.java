package algorisme;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Arnau Miret Barrull
 */
public class NodoDawg {
    private Map<String, NodoDawg> hijos;
    private boolean esFinal;
    private NodoDawg ultimohijoanadido;

    // Funcion constructora
    public NodoDawg() {
        hijos = new HashMap<String,NodoDawg>();
        esFinal = false;
        //ultimohijoanadido = null;
    }

    // GETTERS y SETTERS

    /*
    // Función que devuleve el ultimo nodo añadido
    public NodoDawg getUltimoNodoAnadido() {
        return ultimohijoanadido;
    }

    // Función para setear el ultimo nodo añadido
    public void setUltimoNodoAnadido(NodoDawg nodo) {
        ultimohijoanadido = nodo;
    }*/

    // Indica si este nodo representa el final de una palabra
    public boolean getEsFinal(){
        return esFinal;
    }

    // Devuelve los hijos de este nodo
    public Map<String, NodoDawg> getHijos(){
        return hijos;
    }

    // Devuelve el hijo indicado
    public NodoDawg getHijo(String letra) {
        return this.hijos.get(letra);
    }

    // Marca este nodo como final de palabra
    public void setEsFinal(boolean b) {
        this.esFinal = b;
    }

    // Función que setea los hijos de un nodo
    public void setHijos(Map<String, NodoDawg> hijos) {
        this.hijos = hijos;
    }

    // Devuelve si la función tiene hijos o no
    public boolean tieneHijos() {
        return !this.hijos.isEmpty();
    }

    // Funciones que modifican los hijos

    // Función que añade un hijo a un nodo
    public void anadirHijo(String letra, NodoDawg nodo) {
        this.hijos.put(letra,nodo);
    }

    // Función que sustituye un hijo que ya existía en el nodo
    public void cambiarHijo(String letra, NodoDawg nodo) {
        this.hijos.replace(letra, nodo);
    }

    // Función que devuelve la string más grande lexicograficamente
    public Map.Entry<String, NodoDawg> getHijoMasGrande() {
        if(this.hijos.isEmpty()) return null;

        String letramasgrande = null;
        for(String letra : hijos.keySet()) {
            // siempre va a poner letramasgrande = letra ya que queremos la letra del ultimo hijo
            letramasgrande = letra;
        }

        /*String letramasgrande = null;
        for(String letra : hijos.keySet()) {
            if(letramasgrande == null || letra > letramasgrande) {
                letramasgrande = letra;
            }
        }*/

        return new AbstractMap.SimpleEntry<>(letramasgrande, hijos.get(letramasgrande));
    }

    public boolean esEquivalente(NodoDawg nodo) {
        // Si los dos nodos son aceptadores o no aceptadores
        if(this.esFinal != nodo.getEsFinal()) return false;

        // si tienen diferentes cantidades de hijos
        if(this.hijos.size() != nodo.getHijos().size()) return false;

        // si tienen los mismos hijos, son equivalentes, no hace falta hacer una busqueda recursiva
        // por cada hijo ya que al construir el DAWG incrementalmente, los hijos ya estan minimizados
        // por lo tanto, solo hace falta comprobar que los hijos son los mismos

        for(Map.Entry<String, NodoDawg> hijo : this.hijos.entrySet()) {
            String letra = hijo.getKey();
            NodoDawg hijo1 = hijo.getValue();
            NodoDawg hijo2 = nodo.getHijo(letra);
            if(hijo2 == null) {
                return false;
            }
            if(hijo1 != hijo2) {
                return false;
            }
        }
        return true;
    }

    // Necesario para comparar nodos al mizimizar el DAWG
    /*@Override
    public int hashCode() {
        return Objects.hash(esFinal, hijos.keySet());
    }*/

    @Override
    public int hashCode() {
        return Objects.hash(hijos,esFinal);
    }

    /*@Override
    public boolean equals(Object obj) {
        if(!(obj instanceof NodoDawg)) return false;
        NodoDawg otronodo = (NodoDawg) obj;
        return this.esFinal == otronodo.esFinal && this.hijos.keySet().equals(otronodo.hijos.keySet());
    }*/

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodoDawg otronodo = (NodoDawg) obj;
        //return esFinal == otronodo.esFinal && hijos.equals(otronodo.hijos);
        return Objects.equals(hijos,otronodo.hijos) && Objects.equals(esFinal,otronodo.esFinal);
    }
}