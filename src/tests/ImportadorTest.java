package tests;

import org.junit.Test;
import src.Importador;
import src.Log;
import src.Monitor;

import java.io.IOException;

import static org.junit.Assert.*;

public class ImportadorTest {

    @Test
    public void testGetNombre() throws IOException {
        // Configuración
        Importador importador = new Importador("Importador-1", new int[]{0}, 100L, new Monitor(new Log("log/log.txt")),200);

        // Ejecución
        String result = importador.getNombre();

        // Verificación
        assertEquals("El nombre del cargador debería ser 'Importador-1'", "Importador-1", result);
    }

    @Test
    public void testIsStop() throws IOException {
        // Configuración
        Importador importador = new Importador("Importador-1", new int[]{0}, 100L, new Monitor(new Log("log/log.txt")),200);

        // Verificación inicial
        assertFalse("Por defecto, el estado 'stop' debería ser false", importador.isStop());

        // Cambia el estado y verifica
        importador.setStop(true);
        assertTrue("Después de setStop(true), el estado debería ser true", importador.isStop());
    }

    @Test
    public void testSetAndGetCuenta() throws IOException {
        // Configuración
        Importador importador = new Importador("Importador-1", new int[]{0}, 100L, new Monitor(new Log("log/log.txt")),200);

        // Incrementa el contador de la transición 0
        importador.setCuenta(0);
        int[] cuenta = importador.getCuenta();

        // Verificación
        assertEquals("El contador de la transición 0 debería ser 1 después de setCuenta(0)", 1, cuenta[0]);
    }

    @Test
    public void testRunExecution() throws IOException, InterruptedException {
        // Configuración
        Importador importador = new Importador("Importador-1", new int[]{0}, 100L, new Monitor(new Log("log/log.txt")),200);

        // Ejecuta el cargador en un hilo
        Thread thread = new Thread(importador);
        thread.start();

        // Deja que el hilo se ejecute brevemente
        Thread.sleep(500);

        // Detenemos el proceso
        importador.setStop(true);
        thread.join(); // Espera a que el hilo termine

        // Verificación
        int[] cuenta = importador.getCuenta();
        assertTrue("El importador debería haber ejecutado al menos una transición", cuenta[0] > 0 || cuenta[1] > 0);
    }
}
