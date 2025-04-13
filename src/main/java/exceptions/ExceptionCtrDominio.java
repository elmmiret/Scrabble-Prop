package exceptions;

public class ExceptionCtrDominio extends Exception {
    public ExceptionCtrDominio() {
        super();
    }

    public ExceptionCtrDominio(String mensaje) {
        super(mensaje);
    }
}
