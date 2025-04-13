package exceptions;

public class CoordenadaFueraDeRangoException extends ExceptionCtrDominio {
    public CoordenadaFueraDeRangoException(int x, int y) {
        super("Coordenadas fuera de rango: (" + x + ", " + y + ")");
    }
}
