package src;

public abstract class Proceso implements Runnable {

    private final String nombre;            // Nombre del proceso.
    protected boolean stop;                 // Variable que indica si el proceso debe detenerse.
    protected final long tiempo;            // Tiempo que el proceso debe esperar al realizar la tarea (en milisegundos).
    protected final int[] transiciones;     // Arreglo de transiciones que el proceso debe manejar.
    protected int index;                    // Índice que indica la transición actual que el proceso disparará.
    private final int[] cuenta;             // Cuenta de disparos de cada transicion.
    protected final Monitor monitor;        // Objeto src.Monitor que sincroniza las transiciones.

    /**
     * Constructor de la clase src.Proceso.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Arreglo de transiciones que el proceso debe manejar.
     * @param tiempo       Tiempo de espera entre tareas (en milisegundos).
     * @param monitor      Objeto src.Monitor para coordinar las transiciones.
     */
    public Proceso(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        this.nombre = nombre;
        this.stop = false;
        this.transiciones = transiciones;
        this.index = 0;
        this.tiempo = tiempo;
        this.cuenta = new int[transiciones.length];
        this.monitor = monitor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    public int[] getCuenta() {
        return cuenta;
    }

    public void setCuenta(int transicion) {
        this.cuenta[transicion]++;
    }
}
