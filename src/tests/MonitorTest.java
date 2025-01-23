package tests;

import org.junit.Test;
import src.Monitor;
import src.Log;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import static org.junit.Assert.*;

public class MonitorTest {

    @Test
    public void testConstructor() throws IOException {
        // Configuración: Crear un objeto Monitor con un archivo de log.
        Log log = new Log("log/log.txt");
        Monitor monitor = new Monitor(log);

        // Verificación: Comprobar que los componentes principales no sean nulos.
        assertNotNull("El semáforo no debería ser nulo", monitor.getMutex());
        assertNotNull("La red de Petri no debería ser nula", monitor.getPetriNet());
        assertNotNull("Las colas no deberían ser nulas", monitor.getColas());
        assertNotNull("Las políticas no deberían ser nulas", monitor.getPoliticas());
    }

    @Test
    public void testDispararTransicionExito() throws IOException {
        // Configuración: Crear un Monitor y simular una transición habilitada.
        Log log = new Log("log/log.txt");
        Monitor monitor = new Monitor(log);

        // Habilitar una transición ficticia en la red de Petri.
        // Debe ser la 0 para no corromper los invariantes de plaza.
        int transicionHabilitada = 0;
        monitor.getPetriNet().habilitarTransicion(transicionHabilitada);

        // Ejecución: Intentar disparar la transición habilitada.
        monitor.dispararTransicion(transicionHabilitada);

        // Verificación: Comprobar que el contador de disparos se incrementó.
        double[] disparos = monitor.getDisparos();
        assertEquals("La transición debería haberse disparado una vez", 1.0, disparos[transicionHabilitada], 0.0);
    }

    @Test
    public void testDispararTransicionNoHabilitada() throws IOException {
        // Configuración: Crear un Monitor sin transiciones habilitadas.
        Log log = new Log("log/log.txt");
        Monitor monitor = new Monitor(log);

        // Ejecución y verificación: Intentar disparar una transición no habilitada debe fallar.
        int transicionNoHabilitada = 10;
        monitor.getPetriNet().habilitarTransicion(transicionNoHabilitada);

        try {
            monitor.dispararTransicion(transicionNoHabilitada);
        } catch (RuntimeException e){
            assertEquals("Se corrompieron los invariantes de plaza.",e.getMessage());
        }
    }

    @Test
    public void testTransicionesBloqueadasHabilitadas() throws IOException {
        // Configuración: Crear un Monitor y simular transiciones habilitadas y bloqueadas.
        Log log = new Log("log/log.txt");
        Monitor monitor = new Monitor(log);

        // Simular transiciones habilitadas y bloqueadas.
        int[] transicionesHabilitadas = {1, 0, 1};
        int[] listaBloqueadas = {1, 0, 0};

        // Ejecución: Obtener transiciones bloqueadas habilitadas.
        int[] bloqueadasHabilitadas = monitor.getTransicionesBloqueadasHabilitadas(transicionesHabilitadas, listaBloqueadas);

        // Verificación: Solo la primera transición debería estar habilitada y bloqueada.
        assertArrayEquals("La primera transición debería estar habilitada y bloqueada",
                new int[]{1, 0, 0}, bloqueadasHabilitadas);
    }

    @Test
    public void testMutex() throws IOException {
        // Configuración: Crear un Monitor y comprobar el estado del semáforo.
        Log log = new Log("log/log.txt");
        Monitor monitor = new Monitor(log);

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
