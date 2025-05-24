package gestordepartida;

/**
 * Representa una ficha del juego Scrabble con su correspondiente letra y valor de puntuación.
 *
 * <p>Características principales:</p>
 * <ul>
 *   <li>Inmutabilidad: Una vez creada, sus propiedades no pueden modificarse</li>
 *   <li>Comparación completa: La igualdad considera tanto la letra como la puntuación</li>
 *   <li>Normalización: Almacena siempre la letra en mayúsculas</li>
 * </ul>
 *
 * @author Paula Pérez Chia
 */
public class Ficha {

    /** Letra representada, almacenada siempre en mayúscula */
    private String letra;

    /** Valor numérico de la puntuación de la ficha */
    private int puntuacion;

    /**
     * Construye una ficha con las características especificadas.
     *
     * @param letra Carácter que representa la ficha (se convertirá a mayúscula)
     * @param puntuacion Valor positivo que indica su puntuación en el juego
     */
    public Ficha(String letra, int puntuacion) {
        this.letra = letra.toUpperCase();
        this.puntuacion = puntuacion;
    }

    // MÉTODOS

    /**
     * Cambia la letra representada por la ficha a 'nuevaLetra'.
     *
     * @param nuevaLetra Cadena de carácteres que representa la nueva letra
     */
    public void setLetra(String nuevaLetra) {letra = nuevaLetra;}

    /**
     * Obtiene la letra representada por la ficha.
     *
     * @return Cadena de carácteres en mayúscula
     */
    public String getLetra() {
        return letra;
    }

    /**
     * Obtiene el valor de puntuación asociado a la ficha.
     *
     * @return Entero positivo con el valor de puntuación
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * Compara la igualdad completa entre dos fichas.
     *
     * @param obj Objeto a comparar
     * @return true si son ambas fichas con misma letra y puntuación, false en otro caso
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ficha other = (Ficha) obj;
        return puntuacion == other.puntuacion &&
                letra.equals(other.letra);
    }

    /**
     * Genera código hash basado en los atributos de la ficha.
     *
     * @return Código hash único para combinaciones letra-puntuación
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(letra, puntuacion);
    }



}