package ctrldomini;
public class Ficha {
    private String letra;
    private int puntuacion;

    // Creadora
    public Ficha(String letra, int puntuacion) {
        this.letra = letra;
        this.puntuacion = puntuacion;
    }

    // Métodos
    public String getLetra() {
        return letra;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
}

// cambiar en un futuro a otra implementacion