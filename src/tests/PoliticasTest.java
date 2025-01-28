package tests;

import org.junit.Before;
import org.junit.Test;

import src.Politicas;
import src.Politica;
import src.Segmento;

import static org.junit.Assert.*;

public class PoliticasTest {

    private Politicas politicaBalanceada;
    private Politicas politicaPrioritaria;

    @Before
    public void setUp() {
        politicaBalanceada = new Politicas(17, Politica.BALANCEADA, Segmento.NINGUNO, 0);
        politicaPrioritaria = new Politicas(17, Politica.PRIORITARIA, Segmento.IZQUIERDA, 0.8);
    }

    @Test
    public void seleccionarTransicionBalanceadaTest() {
        int[] transicionesHabilitadas = {1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] disparos = {5, 4, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Debe seleccionar la transición habilitada con menos disparos (índice 4)
        int seleccion = politicaBalanceada.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(4, seleccion);
    }

    @Test
    public void seleccionarTransicionPrioritariaTest() {
        int[] transicionesHabilitadas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0};
        double[] disparos = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 1, 0, 0, 0, 0};

        // Transición 12 tiene relación prioridad < 1-prioridad (se cumple la condición para segmento IZQUIERDA)
        int seleccion = politicaPrioritaria.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(12, seleccion);
    }

    @Test
    public void seleccionarTransicionSinHabilitadasTest() {
        int[] transicionesHabilitadas = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        double[] disparos = {5, 4, 1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Sin transiciones habilitadas, debe devolver -1
        int seleccion = politicaBalanceada.seleccionarTransicion(transicionesHabilitadas, disparos);
        assertEquals(-1, seleccion);
    }
}
