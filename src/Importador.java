import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private static final int MAX_IMAGENES = 10; // Definir el límite de imágenes, si es necesario.

    /**
     * Constructor.
     * @param nombre tarea a realizar.
     * @param transiciones transiciones disparadas por la tarea.
     * @param monitor monitor de la ejecución.
     */
    public Importador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        super(nombre,transiciones,tiempo,monitor);
    }

    public void tarea() {
        int cuenta = 0;
        int i = 0;
        while (cuenta < MAX_IMAGENES){
            try {
                this.monitor.dispararTransicion(transiciones[i]);
                i = (i + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
                cuenta++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                setStop(true);
            }
        }
        while (!stop);
    }
}