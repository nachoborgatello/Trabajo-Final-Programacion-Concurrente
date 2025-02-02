package src.exception;

public class TemporalException extends RuntimeException {
    public TemporalException() {
      super("Se intento disparar una transicion fuera de la ventana de tiempos.");
    }
}

