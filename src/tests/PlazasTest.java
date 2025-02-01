package tests;

import src.petriNet.Plaza;

import org.junit.Test;
import static org.junit.Assert.*;


public class PlazasTest {
    @Test
    public void testConstructorYGetters() {
        Plaza p = new Plaza(1, 5);

        assertEquals("P1", p.getName());
        assertEquals(5, p.getTokens());
    }

    @Test
    public void testSetTokensValido() {
        Plaza p = new Plaza(2, 3);
        p.setTokens(10);

        assertEquals(10, p.getTokens());
    }

    @Test
    public void testSetTokensInvalido() {
        Plaza p = new Plaza(3, 5);

        assertThrows(IllegalArgumentException.class, () -> p.setTokens(-1));
    }
}
