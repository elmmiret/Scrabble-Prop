package ctrldomini;
public class Ficha {
    private char letra;
    private int puntuacion;

    // Creadora
    public Ficha(char letra, int puntuacion) {
        this.letra = letra;
        this.puntuacion = puntuacion;
    }

    // Métodos
    public char getLetra() {
        return letra;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
}

// cambiar en un futuro a otra implementacion