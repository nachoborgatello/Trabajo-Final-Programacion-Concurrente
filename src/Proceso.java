public abstract class Proceso implements Runnable {

    private String nombre;
    protected boolean stop;
    protected final long tiempo;
    protected final int[] transiciones;
    protected int index;
    protected final Monitor monitor;

    /**
     * Constructor.
     * @param nombre tarea a realizar
     * @param transiciones transiciones disparadas por la tarea
     * @param tiempo tiempo que demora en realizar la tarea
     * @param monitor monitor de la ejecución
     */
    public Proceso(String nombre, int[] transiciones, long tiempo ,Monitor monitor) {
        this.nombre = nombre;
        this.stop = false;
        this.transiciones = transiciones;
        this.index = 0;
        this.tiempo = tiempo;
        this.monitor = monitor;
    }

    /**
     * Tarea a realizar.
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
     * Metodo para verificar si la tarea debe detenerse.
     * @return True si la tarea debe detenerse.
     */
    protected boolean isStop() {
        return this.stop;
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