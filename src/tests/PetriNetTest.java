package tests;

import org.junit.Test;

import static org.junit.Assert.*;
import src.PetriNet;

public class PetriNetTest {

    @Test
    public void testDisparar() {
        PetriNet petriNet = new PetriNet();

        // Se intenta disparar la transición 0
        boolean resultado = petriNet.disparar(0);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        // Verificamos que el marcado ha cambiado correctamente después del disparo
        int[] marcado = petriNet.getMarcado();
        int[] esperado = new int[]{1, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        assertArrayEquals(esperado,marcado);

        // Se intenta disparar la transición 1
        resultado = petriNet.disparar(1);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        // Verificamos que el marcado ha cambiado correctamente después del disparo
        marcado = petriNet.getMarcado();
        esperado = new int[]{0, 0, 1, 2, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        assertArrayEquals(esperado,marcado);
    }

    @Test
    public void testDispararTransicionNoHabilitada() {
        PetriNet petriNet = new PetriNet();

        // Intentamos disparar una transición no habilitada
        boolean resultado = petriNet.disparar(1);

        // Verificamos que el disparo no se haya realizado
        assertFalse(resultado);
    }

    @Test
    public void testActualizarHabilitadas() {
        PetriNet petriNet = new PetriNet();

        // Intentamos disparar una transición habilitada
        boolean resultado = petriNet.disparar(0);

        // Verificamos si la transición se disparó correctamente
        assertTrue(resultado);

        int[] t_habilitadas = petriNet.getTransicionesHabilitadas();
        int[] esperado = new int[]{1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        assertArrayEquals(esperado,t_habilitadas);
    }
}