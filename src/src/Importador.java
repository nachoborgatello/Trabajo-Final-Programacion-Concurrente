package src;

import src.exception.BetaException;
import src.exception.PInvariantesException;

import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private final int cantidadMaxima;

    /**
     * Constructor de la clase Importador.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Lista de transiciones que este proceso debe manejar.
     * @param tiempo       Intervalo de espera entre cada disparo de transición (en milisegundos).
     * @param monitor      Objeto Monitor utilizado para sincronizar las transiciones.
     * @param cantMaxima   Cantidad maxima de imagenes a importar dentro del sistema.
     */
    public Importador(String nombre, int[] transiciones, long tiempo, Monitor monitor, int cantMaxima) {
        // Llama al constructor de la clase padre (Proceso) para inicializar los atributos comunes.
        super(nombre, transiciones, tiempo, monitor);

        if (transiciones.length == 0) {
            throw new IllegalArgumentException("La lista de transiciones no puede ser nula o vacía.");
        }
        if (tiempo <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser mayor a 0.");
        }
        this.cantidadMaxima = cantMaxima;
    }

    /**
     * Metodo que ejecuta el proceso en un hilo separado.
     * Este metodo es llamado automáticamente cuando se ejecuta `Thread.start()`.
     */
    @Override
    public void run() {
        while (getCuenta()[0]<this.cantidadMaxima) {
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
            } catch (BetaException e){
                monitor.getMutex().release();
                break;
            }
        }
        // Se mantiene esperando hasta que se cumplan la cantidad maxima de invariantes de transicion.
        while (!monitor.debeDetener()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("La espera fue interrumpida", e);
            }
        }
        /*
            Importador es el encargado de comenzar a detener el programa.
            Intenta realizar un ultimo disparo, pero lo que hace es comenzar a despertar a los hilos de las colas de transicion.
            Al no estar mas habilitados por tokens, se quedan indefinidamente esperando.
         */
        monitor.dispararTransicion(0);
        printStats();
    }
}

