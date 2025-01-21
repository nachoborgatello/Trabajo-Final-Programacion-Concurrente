import java.util.concurrent.TimeUnit;

public class Importador extends Proceso {

    private final int cantMaxima;
    private int cuenta;

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
        this.cuenta = 0;
    }

    /**
     * Metodo que ejecuta el proceso en un hilo separado.
     * Este metodo es llamado automáticamente cuando se ejecuta `Thread.start()`.
     */
    @Override
    public void run() {
        while (this.cuenta<this.cantMaxima) {
            // Dispara la transición correspondiente al índice actual en el arreglo de transiciones.
            this.monitor.dispararTransicion(transiciones[index]);

            // Actualiza el índice para avanzar a la siguiente transición de forma cíclica.
            index = (index + 1) % transiciones.length;

            // Aumenta la cuenta de imagenes importadas al sistema.
            this.cuenta++;

            try {
                TimeUnit.MILLISECONDS.sleep(tiempo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
