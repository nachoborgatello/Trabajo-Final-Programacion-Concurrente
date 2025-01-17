import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Colas {

    private final int cantColas;            // Cantidad de colas, equivalente al número de transiciones.
    private int[] listaBloqueadas;          // Arreglo que indica si hay hilos bloqueados en cada cola (1 = bloqueado, 0 = no bloqueado).
    private ArrayList<Semaphore> colas;     // Lista de semáforos, donde cada semáforo representa una cola.

    /**
     * Constructor de la clase Colas.
     * Inicializa la cantidad de colas y crea una lista de semáforos asociada a cada transición.
     *
     * @param n Número de transiciones (y por lo tanto, número de colas).
     */
    public Colas(int n) {
        this.cantColas = n;
        this.colas = new ArrayList<Semaphore>(n);
        this.listaBloqueadas = new int[n];

        // Inicializa los semáforos para cada cola con el valor inicial en 0.
        for (int i = 0; i < this.cantColas; i++) {
            this.colas.add(new Semaphore(0));
        }

        // Inicializa la lista de bloqueadas con todos los valores en 0 (ningún hilo bloqueado inicialmente).
        for (int j = 0; j < this.listaBloqueadas.length; j++) {
            this.listaBloqueadas[j] = 0;
        }
    }

    /**
     * Metodo que bloquea un hilo en una transición específica.
     *
     * @param transicion Índice de la transición/cola en la que se debe bloquear el hilo.
     */
    public void acquire(int transicion) {
        // Marca la transición como bloqueada en el arreglo de bloqueos.
        this.listaBloqueadas[transicion] = 1;

        try {
            // El hilo se bloquea esperando un permiso del semáforo asociado a la transición.
            this.colas.get(transicion).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que libera un hilo bloqueado en una transición específica.
     *
     * @param transicion Índice de la transición/cola en la que se debe liberar el hilo.
     */
    public void release(int transicion) {
        // Libera un permiso en el semáforo asociado a la transición.
        this.colas.get(transicion).release();             // Incrementa el contador del semáforo, permitiendo que un hilo continúe.
        this.listaBloqueadas[transicion] = 0;             // Marca la transición como no bloqueada en el arreglo de bloqueos.
    }

    /**
     * Metodo que devuelve el arreglo de transiciones bloqueadas.
     *
     * @return Arreglo que indica el estado de bloqueo de cada transición.
     */
    public int[] getListaBloqueadas() {
        return this.listaBloqueadas;
    }
}
