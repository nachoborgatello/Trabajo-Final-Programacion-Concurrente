package src.exception;

public class PInvariantesException extends RuntimeException {
    public PInvariantesException() {
        super("Se corrompieron los invariantes de plaza.");
    }
}
