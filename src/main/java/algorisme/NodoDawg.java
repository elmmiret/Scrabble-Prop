package algorisme;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * La clase NodoDawg representa un nodo en un DAWG (Directed Acyclic Word Graph),
 * una estructura eficiente para almacenar y buscar palabras de forma compacta.
 *
 * <p>Cada nodo contiene un conjunto de hijos que representan los siguientes caracteres posibles,
 * y puede marcar el final de una palabra válida.</p>
 *
 * @author Arnau Miret Barrull
 */
public class NodoDawg {

    /**
     * Mapa que almacena los hijos del nodo, donde la clave es una letra
     * y el valor es el nodo hijo correspondiente.
     */
    private Map<String, NodoDawg> hijos;

    /**
     * Indica si este nodo marca el final de una palabra válida.
     */
    private boolean esFinal;

    /**
     * Constructor que inicializa un nodo vacío.
     * Crea un mapa de hijos vacío y establece el estado inicial de 'esFinal' como falso.
     */
    public NodoDawg() {
        hijos = new HashMap<String,NodoDawg>();
        esFinal = false;
    }

    /**
     * Comprueba si el nodo marca el final de una palabra.
     * @return true si es final de palabra, false en caso contrario
     */
    public boolean getEsFinal(){
        return esFinal;
    }

    /**
     * Obtiene todos los hijos del nodo.
     * @return Mapa con los hijos donde la clave es la letra y el valor el nodo hijo
     */
    public Map<String, NodoDawg> getHijos(){
        return hijos;
    }

    /**
     * Obtiene un hijo específico del nodo.
     * @param letra Letra que identifica al hijo buscado
     * @return Nodo hijo correspondiente a la letra, o {@code null} si no existe
     */
    public NodoDawg getHijo(String letra) {
        return this.hijos.get(letra);
    }

    /**
     * Establece si este nodo marca el final de una palabra.
     * @param b Valor booleano para indicar final de palabra
     */
    public void setEsFinal(boolean b) {
        this.esFinal = b;
    }

    /**
     * Reemplaza todos los hijos del nodo.
     * @param hijos Nuevo mapa de hijos a establecer
     */
    public void setHijos(Map<String, NodoDawg> hijos) {
        this.hijos = hijos;
    }

    /**
     * Verifica si el nodo tiene hijos.
     * @return true si tiene al menos un hijo, false en caso contrario
     */
    public boolean tieneHijos() {
        return !this.hijos.isEmpty();
    }

    // Funciones que modifican los hijos

    /**
     * Añade un nuevo hijo al nodo.
     * @param letra Letra que identifica al nuevo hijo
     * @param nodo Nodo hijo a añadir
     */
    public void anadirHijo(String letra, NodoDawg nodo) {
        this.hijos.put(letra,nodo);
    }

    /**
     * Reemplaza un hijo existente en el nodo.
     * @param letra Letra del hijo a reemplazar
     * @param nodo Nuevo nodo hijo
     */
    public void cambiarHijo(String letra, NodoDawg nodo) {
        this.hijos.replace(letra, nodo);
    }

    /**
     * Encuentra el hijo con la letra lexicográficamente más grande.
     * @return Entrada con la letra y nodo del hijo más grande, o {@code null} si no hay hijos
     */
    public Map.Entry<String, NodoDawg> getHijoMasGrande() {
        if (this.hijos.isEmpty()) return null;

        String letramasgrande = null;
        for (String letra : hijos.keySet()) {
            if (letramasgrande == null || letra.compareTo(letramasgrande) > 0) {
                letramasgrande = letra;
            }
        }
        return new AbstractMap.SimpleEntry<>(letramasgrande, hijos.get(letramasgrande));
    }

    /**
     * Compara la equivalencia estructural con otro nodo.
     * Dos nodos son equivalentes si:
     * <ul>
     *   <li>Tienen el mismo estado de final de palabra</li>
     *   <li>Tienen la misma cantidad de hijos</li>
     *   <li>Todos sus hijos correspondientes son recursivamente equivalentes</li>
     * </ul>
     * @param nodo Nodo a comparar
     * @return true si son estructuralmente equivalentes, false en caso contrario
     */
    public boolean esEquivalente(NodoDawg nodo) {
        if (this.esFinal != nodo.getEsFinal()) return false;
        if (this.hijos.size() != nodo.getHijos().size()) return false;
        for (Map.Entry<String, NodoDawg> entry : this.hijos.entrySet()) {
            String letra = entry.getKey();
            NodoDawg miHijo = entry.getValue();
            NodoDawg otroHijo = nodo.getHijo(letra);
            if (otroHijo == null || !miHijo.esEquivalente(otroHijo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula el código hash basado en los hijos y el estado de final de palabra.
     * @return Código hash para el nodo
     */
    @Override
    public int hashCode() {
        return Objects.hash(hijos,esFinal);
    }

    /**
     * Compara la igualdad profunda entre nodos.
     * @param obj Objeto a comparar
     * @return true si son estructuralmente iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodoDawg otronodo = (NodoDawg) obj;
        //return esFinal == otronodo.esFinal && hijos.equals(otronodo.hijos);
        return Objects.equals(hijos,otronodo.hijos) && Objects.equals(esFinal,otronodo.esFinal);
    }
}