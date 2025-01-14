public abstract class Proceso implements Runnable {

    private String nombre;
    protected boolean stop;
    protected final long tiempo;
    protected final int[] transiciones;
    protected final Monitor monitor;

    /**
     * Constructor.
     * @param nombre tarea a realizar.
     * @param transitions transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecución.
     */
    public Proceso(String nombre, int[] transitions, long tiempo, Monitor monitor) {
        this.nombre = nombre;
        this.stop = false;
        this.transiciones = transitions;
        this.tiempo = tiempo;
        this.monitor = monitor;
    }

    /**
     * Tarea realizada.
     */
    protected abstract void tarea();


    /**
     * Detiene la ejecucion de la tarea.
     * @param s true si se quiere detener la ejecución.
     */
    protected void setStop(boolean s) {
        this.stop = s;
    }

    /**
     * Devuelve el tipo de tarea.
     * @return nombre/tipo de la tarea.
     */
    public String getNombre() {
        return this.nombre;
    }

    @Override
    public void run() {
        tarea();
    }
}