package src.procesos;

import src.Monitor;
import src.exception.PInvariantesException;

import java.util.concurrent.TimeUnit;

public class Filtro extends Proceso {

    /**
     * Constructor de la clase Filtro.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Lista de transiciones que este proceso debe manejar.
     * @param tiempo       Intervalo de espera entre cada disparo de transición (en milisegundos).
     * @param monitor      Objeto Monitor utilizado para sincronizar las transiciones.
     */
    public Filtro(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        // Llama al constructor de la clase padre (Proceso) para inicializar los atributos comunes.
        super(nombre, transiciones, tiempo, monitor);

        if (transiciones.length == 0) {
            throw new IllegalArgumentException("La lista de transiciones no puede ser nula o vacía.");
        }
        if (tiempo <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser mayor a 0.");
        }
    }

    /**
     * Metodo que ejecuta el proceso en un hilo separado.
     * Este metodo es llamado automáticamente cuando se ejecuta `Thread.start()`.
     */
    @Override
    public void run() {
        while (!monitor.debeDetener()) {
            try {

                long antes = System.currentTimeMillis();
                monitor.dispararTransicion(transiciones[index]);
                long despues = System.currentTimeMillis();
                if (!monitor.debeDetener()) {
                    setCuenta(index);
                    setTimeStamp(antes,despues);
                }

                index = (index + 1) % transiciones.length;
                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException e) {
                System.err.println(getNombre() + ": Proceso interrumpido.");
                break;
            } catch (PInvariantesException e){
                System.err.println(getNombre() + ": Error durante el disparo de transición: " + e.getMessage());
                break;
            }
        }
        printStats();
    }
}
