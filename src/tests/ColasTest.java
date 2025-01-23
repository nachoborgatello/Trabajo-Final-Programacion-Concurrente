package tests;

import org.junit.Before;
import org.junit.Test;
import src.Colas;

import static org.junit.Assert.*;

public class ColasTest {

    private Colas colas;

    @Before
    public void setUp() {
        colas = new Colas(3); // Inicializa con 3 colas.
    }

    @Test
    public void testConstructor() {
        // Verifica que el arreglo listaBloqueadas esté inicializado con ceros.
        int[] listaBloqueadas = colas.getListaBloqueadas();
        assertArrayEquals(new int[]{0, 0, 0}, listaBloqueadas);

        // Verifica que las colas contengan tres semáforos inicializados.
        assertEquals(3, listaBloqueadas.length);
    }

    @Test
    public void testAcquire() {
        // Lanza un hilo que llama a acquire en la transición 0.
        Thread hilo = new Thread(() -> colas.acquire(0));

        hilo.start();

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(100);

            // Verifica que el estado de listaBloqueadas refleje el bloqueo.
            int[] listaBloqueadas = colas.getListaBloqueadas();
            assertEquals(1, listaBloqueadas[0]); // La transición 0 debe estar bloqueada.

            // Libera la transición 0 para que el hilo continúe.
            colas.release(0);

            // Espera que el hilo termine.
            hilo.join();
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }
    }

    @Test
    public void testRelease() {
        // Llama a release directamente para liberar una transición.
        colas.release(1);

        // Verifica que la lista de bloqueadas indique que la transición 1 no está bloqueada.
        int[] listaBloqueadas = colas.getListaBloqueadas();
        assertEquals(0, listaBloqueadas[1]);
    }

    @Test
    public void testAcquireAndRelease() throws InterruptedException {
        // Simula la adquisición y liberación de una cola en un hilo separado.
        Thread hilo = new Thread(() -> {
            colas.acquire(2);
            colas.release(2);
        });

        hilo.start();

        // Espera un momento para verificar el bloqueo.
        Thread.sleep(100);
        int[] listaBloqueadas = colas.getListaBloqueadas();
        assertEquals(1, listaBloqueadas[2]); // La transición 2 debe estar bloqueada.

        // Libera la transición para que el hilo continúe.
        colas.release(2);

        // Espera que el hilo termine.
        hilo.join();

        // Verifica que la transición 2 ya no esté bloqueada.
        listaBloqueadas = colas.getListaBloqueadas();
        assertEquals(0, listaBloqueadas[2]);
    }
}
