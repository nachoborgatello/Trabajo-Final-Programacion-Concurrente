package src;

import java.util.concurrent.TimeUnit;

public class Cargador extends Proceso {

    /**
     * Constructor de la clase Cargador.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Lista de transiciones que este proceso debe manejar.
     * @param tiempo       Intervalo de espera entre cada disparo de transición (en milisegundos).
     * @param monitor      Objeto Monitor utilizado para sincronizar las transiciones.
     */
    public Cargador(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        // Llama al constructor de la clase padre (Proceso) para inicializar los atributos comunes.
        super(nombre, transiciones, tiempo, monitor);
    }

    /**
     * Metodo que ejecuta el proceso en un hilo separado.
     * Este metodo es llamado automáticamente cuando se ejecuta `Thread.start()`.
     */
    @Override
    public void run() {
        while (!isStop()) {
            try {
                // Dispara la transición correspondiente al índice actual en el arreglo de transiciones.
                this.monitor.dispararTransicion(transiciones[index]);

                // Llevamos la cuenta de cuantos disparos se hizo en cada transicion.
                setCuenta(index);

                // Actualiza el índice para avanzar a la siguiente transición de forma cíclica.
                index = (index + 1) % transiciones.length;

                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException | RuntimeException e) {
                setStop(true);
            }
        }
        System.out.println(getNombre());
        for (int i=0;i< getCuenta().length;i++){
            System.out.printf("Transicion: %d Disparos: %d\n",transiciones[i],getCuenta()[i]);
        }
    }
}
