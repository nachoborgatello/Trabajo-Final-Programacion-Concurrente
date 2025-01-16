import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Colas {

    private final int NUM_TRANSICIONES;     // Tengo tantas colas como transiciones existen
    private int[] listaBloqueadas;          // Array que representa si hay hilos en alguna cola
    private ArrayList<Semaphore> colas;     // Lista de colas

    public Colas(int n) {
        this.NUM_TRANSICIONES = n;
        this.colas = new ArrayList<Semaphore>(n);
        this.listaBloqueadas = new int[n];
        for (int i = 0; i < this.NUM_TRANSICIONES; i++) {
            this.colas.add(new Semaphore(0));
        }
        for (int j = 0; j < this.listaBloqueadas.length; j++) {
            this.listaBloqueadas[j] = 0;
        }
    }

    public void acquire(int transicion) {
        // System.out.println("En la transicion " + transicion + " se bloqueo un hilo.");
        this.listaBloqueadas[transicion] = 1;
        try {
            this.colas.get(transicion).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void release(int transicion) {
        // System.out.println("En la transicion " + transicion + " se libero un hilo.");
        this.colas.get(transicion).release();
        this.listaBloqueadas[transicion] = 0;
    }

    public int[] getListaBloqueadas() {
        return this.listaBloqueadas;
    }
}
