package ctrldomini;

/**
 * Esta clase representa una Ficha de Scrabble.
 * Contiene la letra a la que representa y su puntuación.
 *
 * @author: Paula Pérez
 */
public class Ficha {
    private String letra;
    private int puntuacion;

    // CREADORA
    
    /**
     * Crea una instancia de Ficha.
     *
     * @param letra Letra que representa la ficha
     * @param puntuacion Puntuación de la letra
     */
    public Ficha(String letra, int puntuacion) {
        this.letra = letra.toUpperCase();
        this.puntuacion = puntuacion;
    }

    // MÉTODOS

    /**
     * Devuelve la letra de la ficha.
     *
     * @return la letra de la ficha
     */
    public String getLetra() {
        return letra;
    }

    /**
     * Devuelve la puntuación de la ficha.
     *
     * @return la puntuación de la ficha
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ficha other = (Ficha) obj;
        return puntuacion == other.puntuacion &&
                letra.equals(other.letra);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(letra, puntuacion);
    }



}