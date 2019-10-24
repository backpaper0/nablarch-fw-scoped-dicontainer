package nablarch.fw.dicontainer.component;

import org.junit.Test;

import static org.junit.Assert.*;

public class ComponentIdTest {
    private ComponentId id1 = ComponentId.generate();
    private ComponentId id2 = ComponentId.generate();

    @Test
    public void testEquals() {
        assertTrue(id1.equals(id1));
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(id1, id2);
    }

    @Test
    public void testDifferentClass() {
        assertNotEquals(id1, "this is String class.");
    }
    @Test
    public void testNull() {
        assertFalse(id1.equals(null));
    }
}