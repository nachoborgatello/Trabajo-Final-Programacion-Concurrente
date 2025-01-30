package src;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Colas {

    // Lista de semáforos, donde cada semáforo representa una cola.
    private final ArrayList<Semaphore> colas;

    /**
     * Constructor de la clase Colas.
     * Inicializa la cantidad de colas y crea una lista de semáforos asociada a cada transición.
     *
     * @param n Número de transiciones (y por lo tanto, número de colas).
     */
    public Colas(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("El número de transiciones debe ser mayor a 0.");
        }
        //listaBloqueadas = new int[n];
        colas = new ArrayList<>(n);
        colas.ensureCapacity(n);
        for (int i = 0; i < n; i++) {
            colas.add(new Semaphore(0));
        }
    }

    /**
     *  Bloquea un hilo en la cola asociada a una transición específica.
     *  La cola utiliza un semáforo con un contador inicial de 0, obligando al hilo a esperar hasta que se llame a `release`.
     *
     * @param transicion Índice de la transición/cola en la que se debe bloquear el hilo.
     */
    public void esperar(int transicion) {
        validarIndice(transicion);
        try {
            colas.get(transicion).acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Metodo que libera un hilo bloqueado en una transición específica.
     *
     * @param transicion Índice de la transición/cola en la que se debe liberar el hilo.
     */
    public void liberar(int transicion) {
        validarIndice(transicion);
        colas.get(transicion).release();             // Incrementa el contador del semáforo, permitiendo que un hilo continúe.
    }

    // Metodo auxiliar para validar índices.
    private void validarIndice(int transicion) {
        if (transicion < 0) {
            throw new IllegalArgumentException("Índice de transición inválido: " + transicion);
        }
    }

    public ArrayList<Semaphore> getColas() {
        return colas;
    }
}
