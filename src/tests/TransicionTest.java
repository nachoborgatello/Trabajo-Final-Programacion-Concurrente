package tests;

import org.junit.Test;
import src.petriNet.Transicion;

import static org.junit.Assert.*;

public class TransicionTest {

    @Test
    public void testConstructorYGetters() {
        Transicion t = new Transicion(1, 100, 200);

        assertEquals("T1", t.getNombre());
        assertEquals(100, t.getAlfa());
        assertEquals(200, t.getBeta());
        assertFalse(t.esInmediata());
        assertEquals(0, t.estaHabilitada());
    }

    @Test
    public void testEsInmediata() {
        Transicion t1 = new Transicion(2, 0, 150);
        Transicion t2 = new Transicion(3, 50, 150);

        assertTrue(t1.esInmediata());
        assertFalse(t2.esInmediata());
    }

    @Test
    public void testHabilitarYDeshabilitar() {
        Transicion t = new Transicion(4, 100, 200);

        assertEquals(0, t.estaHabilitada());

        t.habilitar();
        assertEquals(1, t.estaHabilitada());

        t.deshabilitar();
        assertEquals(0, t.estaHabilitada());
    }
}
