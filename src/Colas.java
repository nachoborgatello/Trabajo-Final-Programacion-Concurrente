import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Colas {

    private final int nColas;               // Cantidad de colas
    private int[] listaBloqueadas;          // Array que representa si hay hilos en alguna cola
    private ArrayList<Semaphore> colas;     // Lista de colas

    /**
     * Constructor.
     * @param n cantidad de colas a inicializar.
     */
    public Colas(int n) {
        this.nColas = n;
        this.colas = new ArrayList<Semaphore>(n);
        this.listaBloqueadas = new int[n];
        for (int i = 0; i < this.nColas; i++) {
            this.colas.add(new Semaphore(0));
        }
        Arrays.fill(this.listaBloqueadas, 0);
    }

    /**
     * Manda un hilo a esperar a una cola
     * @param transicion numero de la transicion
     */
    public void acquire(int transicion) {
        // System.out.println("En la transicion " + transicion + " se bloqueo un hilo.");
        this.listaBloqueadas[transicion] = 1;
        try {
            this.colas.get(transicion).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Libera un hilo de una cola de condición.
     * @param transicion numero de la transición/cola de condición.
     */
    public void release(int transicion) {
        // System.out.println("En la transicion " + transicion + " se libero un hilo.");
        this.colas.get(transicion).release();
        this.listaBloqueadas[transicion] = 0;
    }

    /**
     * Devuelve el array de colas ocupadas.
     * @return array de colas oucpadas.
     */
    public int[] getListaBloqueadas() {
        return this.listaBloqueadas;
    }
}
