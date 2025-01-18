import java.lang.Thread.UncaughtExceptionHandler;

// Clase que implementa el manejador de excepciones no capturadas en hilos
public class ExceptionHandler implements UncaughtExceptionHandler {

    /**
     * Metodo que se invoca automáticamente cuando un hilo encuentra una excepción no capturada.
     * @param t El hilo donde ocurrió la excepción.
     * @param e La excepción lanzada.
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Imprimir un mensaje indicando que se capturó una excepción
        System.out.println("Se ha capturado una excepción");

        // Imprimir el nombre del hilo donde ocurrió la excepción
        System.out.printf("Hilo: %s\n", t.getName());

        // Imprimir el tipo de la excepción y su mensaje
        System.out.printf("Excepción: %s: %s\n", e.getClass().getName(), e.getMessage());

        // Imprimir la traza de la pila para diagnosticar el error
        System.out.println("Traza de la pila:");
        e.printStackTrace(System.out);

        // Imprimir el estado actual del hilo donde ocurrió la excepción
        System.out.printf("Estado del hilo: %s\n", t.getState());
    }
}
