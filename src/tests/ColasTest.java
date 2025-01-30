package tests;

import org.junit.Before;
import org.junit.Test;
import src.Colas;

import static org.junit.Assert.*;

public class ColasTest {

    private Colas colas;

    @Before
    public void setUp() {
        colas = new Colas(5);
    }

    @Test
    public void testEsperar() {
        // Lanza un hilo que llama a esperar en la transición 0.
        Thread hilo = new Thread(() -> colas.esperar(0));

        hilo.start();

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(100);

            int size = colas.getColas().getFirst().getQueueLength();
            assertEquals(1,size);

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

            Thread.sleep(100);

            // Verifica que la lista de bloqueadas indique que la transición 1 no está bloqueada.
            int size = colas.getColas().getFirst().getQueueLength();
            assertEquals(0,size);

            // Espera que el hilo termine.
            hilo.join();
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }
    }
}
