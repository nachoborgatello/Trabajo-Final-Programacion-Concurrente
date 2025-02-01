package tests;

import org.junit.Before;
import org.junit.Test;
import src.*;
import src.utils.Politica;
import src.utils.Red;
import src.utils.Segmento;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class MonitorTest {

    private Monitor monitor;

    @Before
    public void setUp() {
        try {
            monitor = new Monitor(new Log("log/log.txt"), Politica.BALANCEADA, Segmento.NINGUNO,0, Red.SIN_TIEMPOS,200);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDispararTransicionExito() {
        // Intenta disparar la transicion 0, habilitada inicialmente
        int transicionHabilitada = 0;

        // Dispara la transicion
        monitor.dispararTransicion(transicionHabilitada);

        // La cuenta de disparos que lleva el monitor deberia reflejar el cambio
        double[] disparos = monitor.getDisparos();
        assertEquals(1.0, disparos[transicionHabilitada],0.0);
    }

    @Test
    public void testQuienesEstan() throws InterruptedException {
        Thread hilo = new Thread(() -> monitor.getColas().esperar(0));

        hilo.start();

        // Simular transiciones habilitadas y bloqueadas.
        int[] sensibilizadas = {1, 0, 1, 0, 0};

        sleep(100);

        // Ejecución: Obtener transiciones bloqueadas habilitadas.
        int[] bloqueadasHabilitadas = monitor.quienesEstan(sensibilizadas);

        // Verificación: Solo la primera transición debería estar habilitada y bloqueada.
        assertArrayEquals(new int[]{1, 0, 0, 0, 0}, bloqueadasHabilitadas);
    }

    @Test
    public void testMutex() {
        // Configuración: Crear un Monitor y comprobar el estado del semáforo.
        Semaphore mutex = monitor.getMutex();
        // Ejecución: Intentar adquirir y liberar el semáforo.
        try {
            assertTrue("El mutex debería estar disponible inicialmente", mutex.tryAcquire());
            mutex.release();
        } catch (Exception e) {
            fail("No se debería lanzar ninguna excepción al manejar el semáforo");
        }
    }
}
