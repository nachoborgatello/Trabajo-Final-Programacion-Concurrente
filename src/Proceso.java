public abstract class Proceso implements Runnable {

    private final String nombre;            // Nombre del proceso.
    protected boolean stop;                 // Variable que indica si el proceso debe detenerse.
    protected final long tiempo;            // Tiempo que el proceso debe esperar al realizar la tarea (en milisegundos).
    protected final int[] transiciones;     // Arreglo de transiciones que el proceso debe manejar.
    protected int index;                    // Índice que indica la transición actual que el proceso disparará.
    protected final Monitor monitor;        // Objeto Monitor que sincroniza las transiciones.

    /**
     * Constructor de la clase Proceso.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Arreglo de transiciones que el proceso debe manejar.
     * @param tiempo       Tiempo de espera entre tareas (en milisegundos).
     * @param monitor      Objeto Monitor para coordinar las transiciones.
     */
    public Proceso(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        this.nombre = nombre;
        this.stop = false;
        this.transiciones = transiciones;
        this.index = 0;
        this.tiempo = tiempo;
        this.monitor = monitor;
    }

    /**
     * Metodo para establecer el estado de detención del proceso.
     */
    protected void setStop() {
        this.stop = true;
    }

    /**
     * Metodo que verifica si el proceso está detenido.
     * @return `true` si el proceso debe detenerse, `false` en caso contrario.
     */
    protected boolean getStop() {
        return this.stop;
    }
}
