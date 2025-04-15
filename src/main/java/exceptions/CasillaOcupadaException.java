package exceptions;

/**
 * @author: Paula Pérez
 */
public class CasillaOcupadaException extends ExceptionCtrDominio {
    public CasillaOcupadaException(int x, int y) {
        super("La casilla en (" + x + ", " + y + ") ya está ocupada.");
    }
}
