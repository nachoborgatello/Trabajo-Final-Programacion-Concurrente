import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private int cantMaxima;
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
        this.cantMaxima = cantMaxima;
    }

    /**
     * Metodo que ejecuta el proceso en un hilo separado.
     * Este metodo es llamado automáticamente cuando se ejecuta `Thread.start()`.
     */
    @Override
    public void run() {
        int cuenta = 0;
        while (cuenta < this.cantMaxima) {
            try {
                // Dispara la transición correspondiente al índice actual en el arreglo de transiciones.
                this.monitor.dispararTransicion(transiciones[index]);

                // Actualiza el índice para avanzar a la siguiente transición de forma cíclica.
                index = (index + 1) % transiciones.length;

                // Aumenta la cuenta de imagenes importadas al sistema.
                cuenta++;

                // Pausa la ejecución del hilo durante el intervalo especificado (en milisegundos).
                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException e) {
                // Maneja la excepción si el hilo es interrumpido durante la pausa.
                // Imprime la traza de la excepción para depuración.
                e.printStackTrace();
            }
        }
        while(!getStop());
    }
}
