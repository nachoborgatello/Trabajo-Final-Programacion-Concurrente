package tests;

import org.junit.Before;
import org.junit.Test;
import src.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class ImportadorTest {

    private Importador importador;
    private Monitor monitor;

    @Before
    public void setUp() {
        try {
            monitor = new Monitor(new Log("log/log.txt"), Politica.BALANCEADA, Segmento.NINGUNO,0,Red.SIN_TIEMPOS);
            importador = new Importador("Importador-1", new int[]{0}, 1L, monitor,5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetNombre() throws IOException {
        String result = importador.getNombre();
        assertEquals("El nombre del cargador debería ser 'Importador-1'", "Importador-1", result);
    }

    @Test
    public void testSetAndGetCuenta() throws IOException {
        importador.setCuenta(0);
        int[] cuenta = importador.getCuenta();
        assertEquals("El contador de la transición 0 debería ser 1 después de setCuenta(0)", 1, cuenta[0]);
    }

    @Test
    public void testRunExecution() throws InterruptedException {
        // Ejecuta el cargador en un hilo
        Thread thread = new Thread(importador);
        thread.start();

        // Deja que el hilo se ejecute brevemente
        thread.sleep(500);

        // Detenemos el proceso
        monitor.debeDetener();

        // Verificación
        int[] cuenta = importador.getCuenta();
        assertEquals(5,cuenta[0]);
    }
}
