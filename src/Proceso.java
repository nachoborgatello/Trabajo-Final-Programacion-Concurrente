public abstract class Proceso implements Runnable {

    private String nombre;
    protected boolean stop;                 // Variable que indica que el proceso debe detenerse
    protected final long tiempo;            // Tiempo que demora en realizar la tarea
    protected final int[] transiciones;     // Transiciones que dispara el proceso
    protected int index;                    // Indica la transicion que va a disparar el proceso
    protected final Monitor monitor;

    public Proceso(String nombre, int[] transiciones, long tiempo ,Monitor monitor) {
        this.nombre = nombre;
        this.stop = false;
        this.transiciones = transiciones;
        this.index = 0;
        this.tiempo = tiempo;
        this.monitor = monitor;
    }

    protected abstract void tarea();

    protected void setStop(boolean s) {
        this.stop = s;
    }

    protected boolean isStop() {
        return this.stop;
    }

    @Override
    public void run() {
        tarea();
    }
}