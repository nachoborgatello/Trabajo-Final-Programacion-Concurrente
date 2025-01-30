package tests;

import org.junit.Before;
import org.junit.Test;
import src.*;

import java.io.IOException;

import static org.junit.Assert.*;

public class PetriNetTest {

    private PetriNet petriNetSinTiempo;
    private PetriNet petriNetConTiempo;
    private Importador importador;
    private Monitor monitor;

    @Before
    public void setUp() throws IOException {
        petriNetSinTiempo = new PetriNet(Red.SIN_TIEMPOS);
        monitor = new Monitor(new Log("log/log.txt"), Politica.BALANCEADA, Segmento.NINGUNO,0,Red.TEMPORAL);
        importador = new Importador("Importador-1", new int[]{0}, 1L, monitor,1);
        petriNetConTiempo = monitor.getPetriNet();
    }

    @Test
    public void testDispararSinTiempos() {
        // Se intenta disparar la transición 0
        boolean resultado = petriNetSinTiempo.disparar(0);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        // Verificamos que el marcado ha cambiado correctamente después del disparo
        int[] marcado = petriNetSinTiempo.getMarcado();
        int[] esperado = new int[]{1, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        assertArrayEquals(esperado,marcado);

        // Se intenta disparar la transición 1
        resultado = petriNetSinTiempo.disparar(1);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        // Verificamos que el marcado ha cambiado correctamente después del disparo
        marcado = petriNetSinTiempo.getMarcado();
        esperado = new int[]{0, 0, 1, 2, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        assertArrayEquals(esperado,marcado);
    }

    @Test
    public void testDispararConTiempos() {

        Thread hilo = new Thread(importador);

        hilo.start();

        // Verificamos que el marcado ha cambiado correctamente después del disparo
        int[] esperado = new int[]{1, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        try {
            // Espera un momento para asegurarse de que el hilo haya llamado a acquire.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            fail("La prueba fue interrumpida inesperadamente.");
        }

        int[] marcado = petriNetConTiempo.getMarcado();

        assertArrayEquals(esperado,marcado);
    }

    @Test
    public void testDispararTransicionNoHabilitada() {
        // Intentamos disparar una transición no habilitada
        boolean resultado = petriNetSinTiempo.disparar(16);

        // Verificamos que el disparo no se haya realizado
        assertFalse(resultado);
    }

    @Test
    public void testActualizarHabilitadas() {
        // Intentamos disparar una transición habilitada
        boolean resultado = petriNetSinTiempo.disparar(0);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        int[] t_habilitadas = petriNetSinTiempo.getTransicionesHabilitadas();
        int[] esperado = new int[]{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        assertArrayEquals(esperado,t_habilitadas);
    }
}