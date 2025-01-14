import java.util.concurrent.TimeUnit;

public class Redimensionador extends Proceso {

    /**
     * Constructor.
     * @param nombre tarea a realizar.
     * @param transiciones transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecución.
     */
    public Redimensionador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        super(nombre,transiciones,tiempo,monitor);
    }

    public void tarea() {
        int i = 0;
        while (!stop) {
            try {
                this.monitor.dispararTransicion(transiciones[i]);
                i = (i + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                setStop(true);
            }
        }
    }
}