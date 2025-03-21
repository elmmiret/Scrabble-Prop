package ctrldomini.algorisme;

import java.util.*;

public class NodoDawg {
    private Map<Character, NodoDawg> hijos = new HashMap<>();
    private boolean esFinal = false;


    //funciones get y set
    public boolean getEsFinal(){
        return esFinal;
    }

    public Map<Character, NodoDawg> getHijos(){
        return hijos;
    }

    public void setEsFinal(boolean b) {
        this.esFinal = b;
    }

    // para la minimización, necesitamos comparar nodos
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        NodoDawg otro = (NodoDawg) obj;
        return esFinal == otro.getEsFinal() && hijos.equals(otro.getHijos());
    }

    //genera el codigo binario de cuantas letras tiene la palabra hasta la final
    @Override
    public int hashCode() {
        return hijos.hashCode() + (esFinal ? 1 : 0);
    }

}