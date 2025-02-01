package tests;

import org.junit.Before;
import org.junit.Test;
import src.*;
import src.procesos.Importador;
import src.utils.Politica;
import src.utils.Red;
import src.utils.Segmento;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ImportadorTest {

    private Importador importador;
    private Monitor monitor;

    @Before
    public void setUp() {
        try {
            monitor = new Monitor(new Log("log/log.txt"), Politica.BALANCEADA, Segmento.NINGUNO,0, Red.SIN_TIEMPOS,200);
            importador = new Importador("Importador-1", new int[]{0}, 1L, monitor,5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetNombre() {
        String result = importador.getNombre();
        assertEquals("El nombre del importador debería ser 'Importador-1'", "Importador-1", result);
    }

    @Test
    public void testSetAndGetCuenta() {
        importador.setCuenta(0);
        int[] cuenta = importador.getCuenta();
        assertEquals("El contador de la transición 0 debería ser 1 después de setCuenta(0)", 1, cuenta[0]);
    }

    @Test
    public void testRunExecution() throws InterruptedException {
        // Ejecuta el importador en un hilo
        Thread thread = new Thread(importador);
        thread.start();

        // Deja que el hilo se ejecute brevemente
        Thread.sleep(500);

        // Detenemos el proceso
        monitor.debeDetener();

        // Verificación
        int[] cuenta = importador.getCuenta();
        assertEquals(5,cuenta[0]);
    }

    @Test
    public void setTimeStampTest() throws InterruptedException {
        // Ejecuta el importador en un hilo
        Thread thread = new Thread(importador);

        ArrayList<Integer> timeStamp = importador.getTimeStamp();
        assertTrue("La lista debe estar vacia",timeStamp.isEmpty());

        thread.start();
        // Deja que el hilo se ejecute brevemente
        Thread.sleep(500);

        // Detenemos el proceso
        monitor.debeDetener();

        // Verificación
        timeStamp = importador.getTimeStamp();
        assertFalse("La lista no debe estar vacia",timeStamp.isEmpty());
    }
}
