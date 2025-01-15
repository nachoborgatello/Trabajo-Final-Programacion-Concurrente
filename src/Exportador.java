import java.util.concurrent.TimeUnit;

public class Exportador extends Proceso {

    public Exportador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        super(nombre,transiciones,tiempo,monitor);
    }

    public void tarea() {
        while (!isStop()) {
            try {
                this.monitor.dispararTransicion(transiciones[index]);
                index = (index + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException e) {
                e.printStackTrace();
                setStop(true);
            }
        }
    }
}