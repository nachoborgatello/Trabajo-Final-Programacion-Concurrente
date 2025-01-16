import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private static final int CANT_MAXIMA = 10000;

    public Importador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        super(nombre,transiciones,tiempo,monitor);
    }

    public void tarea() {
        int cantImagenes = 0;
        while (cantImagenes < CANT_MAXIMA){
            try {
                this.monitor.dispararTransicion(transiciones[index]);
                index = (index + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
                cantImagenes++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (!isStop());
    }
}