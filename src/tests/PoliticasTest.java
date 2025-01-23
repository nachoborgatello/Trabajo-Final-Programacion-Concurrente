package tests;

import org.junit.Test;
import src.*;

import static org.junit.Assert.*;

public class PoliticasTest {

    @Test
    public void constructorTest() {
        // Caso 1: Política válida, segmento válido, prioridad válida
        Politicas politicas = new Politicas(5, "BALANCEADA", "DERECHA", 0.7);
        assertEquals("BALANCEADA", politicas.getTipo());
        assertEquals("DERECHA", politicas.getSegmento());
        assertEquals(0.7, politicas.getPrioridad(), 0.001);

        // Caso 2: Política inválida, segmento válido, prioridad válida
        politicas = new Politicas(5, "INVALIDA", "IZQUIERDA", 0.5);
        assertEquals("BALANCEADA", politicas.getTipo());
        assertEquals("IZQUIERDA", politicas.getSegmento());
        assertEquals(0.5, politicas.getPrioridad(), 0.001);

        // Caso 3: Política válida, segmento inválido, prioridad válida
        politicas = new Politicas(5, "PRIORITARIA", "INVALIDO", 0.3);
        assertEquals("PRIORITARIA", politicas.getTipo());
        assertEquals("", politicas.getSegmento());
        assertEquals(0.3, politicas.getPrioridad(), 0.001);

        // Caso 4: Política válida, segmento válido, prioridad fuera de rango
        politicas = new Politicas(5, "PRIORITARIA", "DERECHA", 1.5);
        assertEquals("PRIORITARIA", politicas.getTipo());
        assertEquals("DERECHA", politicas.getSegmento());
        assertEquals(0, politicas.getPrioridad(), 0.001);
    }

    @Test
    public void seleccionarTransicionBalanceadaTest() {
        int[] transicionesHabilitadas = {1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] disparos = {5, 4, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Configuración: Política BALANCEADA
        Politicas politicas = new Politicas(5, "BALANCEADA", "", 0);

        // Debe seleccionar la transición habilitada con menos disparos (índice 4)
        int seleccion = politicas.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(4, seleccion);
    }

    @Test
    public void seleccionarTransicionPrioritariaIzquierdaTest() {
        int[] transicionesHabilitadas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0};
        double[] disparos = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 0, 0, 0};

        // Configuración: Política PRIORITARIA, segmento IZQUIERDA
        Politicas politicas = new Politicas(13, "PRIORITARIA", "IZQUIERDA", 0.8);

        // Transición 12 tiene relación prioridad < 1-prioridad (se cumple la condición para segmento IZQUIERDA)
        int seleccion = politicas.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(12, seleccion);
    }

    @Test
    public void seleccionarTransicionPrioritariaDerechaTest() {
        int[] transicionesHabilitadas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0};
        double[] disparos = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 10, 0, 0, 0, 0};

        // Configuración: Política PRIORITARIA, segmento DERECHA
        Politicas politicas = new Politicas(13, "PRIORITARIA", "DERECHA", 0.7);

        // Transición 11 tiene relación prioridad < 1-prioridad (se cumple la condición para segmento DERECHA)
        int seleccion = politicas.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(11, seleccion);
    }

    @Test
    public void seleccionarTransicionSinHabilitadasTest() {
        int[] transicionesHabilitadas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] disparos = {5, 4, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Configuración: Política BALANCEADA
        Politicas politicas = new Politicas(5, "BALANCEADA", "", 0);

        // Sin transiciones habilitadas, debe devolver -1
        int seleccion = politicas.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(-1, seleccion);
    }
}
