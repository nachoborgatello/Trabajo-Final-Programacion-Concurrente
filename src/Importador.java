import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private static final int CANT_MAXIMA = 10000;

    public Importador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        super(nombre,transiciones,tiempo,monitor);
    }

    public void tarea() {
        int cuenta = 0;
        while (cuenta < CANT_MAXIMA){
            try {
                this.monitor.dispararTransicion(transiciones[index]);
                index = (index + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
                cuenta++;
            } catch (InterruptedException e) {
                e.printStackTrace();
                setStop(true);
            }
        }
        while (!isStop());
    }
}