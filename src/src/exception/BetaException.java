package src.exception;

public class BetaException extends RuntimeException {
    public BetaException() {
        super("Fuera de la ventana de tiempos.");
    }
}
