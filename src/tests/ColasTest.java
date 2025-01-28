package tests;

import org.junit.Before;
import org.junit.Test;
import src.Colas;

import static org.junit.Assert.*;

public class ColasTest {

    private Colas colas;

    @Before
    public void setUp() {
        colas = new Colas(5); // Inicializa con 10 colas.
    }

    @Test
    public void testConstructor() {
        // Verifica que el arreglo listaBloqueadas esté inicializado con ceros.
        int[] listaBloqueadas = colas.getListaBloqueadas();
        assertArrayEquals(new int[]{0, 0, 0, 0, 0}, listaBloqueadas);

        // Verifica que las colas contengan cinco semáforos inicializados.
        assertEquals(5, listaBloqueadas.length);
    }

    @Test
    public void testEsperar() {
        // Lanza un hilo que llama a esperar en la transición 0.
        Thread hilo = new Thread(() -> colas.esperar(0));

        hilo.start();

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(100);

            // Verifica que el estado de listaBloqueadas refleje el bloqueo.
            int[] listaBloqueadas = colas.getListaBloqueadas();
            assertEquals(1, listaBloqueadas[0]); // La transición 0 debe estar bloqueada.

            // Libera la transición 0 para que el hilo continúe.
            colas.liberar(0);

            // Espera que el hilo termine.
            hilo.join();
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }
    }

    @Test
    public void testLiberar() {
        // Lanza un hilo que llama a esperar en la transición 0.
        Thread hilo = new Thread(() -> colas.esperar(0));

        hilo.start();

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(100);

            // Libera la transición 0 para que el hilo continúe.
            colas.liberar(0);

            // Verifica que la lista de bloqueadas indique que la transición 1 no está bloqueada.
            int[] listaBloqueadas = colas.getListaBloqueadas();
            assertEquals(0, listaBloqueadas[0]);

            // Espera que el hilo termine.
            hilo.join();
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }
    }

    @Test
    public void testEsperaryLiberar() {

        // Lanza un hilo que llama a esperar y liberar en la transición 2.
        Thread hilo = new Thread(() -> {
            colas.esperar(2);
            colas.liberar(2);
        });

        hilo.start();

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(100);

            int[] listaBloqueadas = colas.getListaBloqueadas();
            assertEquals(1, listaBloqueadas[2]); // La transición 2 debe estar bloqueada.

            // Libera la transición 2 para que el hilo continúe.
            colas.liberar(2);

            // Verifica que la lista de bloqueadas indique que la transición 1 no está bloqueada.
            listaBloqueadas = colas.getListaBloqueadas();
            assertEquals(0, listaBloqueadas[2]);

            // Espera que el hilo termine.
            hilo.join();
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }
    }
}
