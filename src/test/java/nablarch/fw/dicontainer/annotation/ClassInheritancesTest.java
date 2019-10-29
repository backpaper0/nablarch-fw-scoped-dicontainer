package nablarch.fw.dicontainer.annotation;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class ClassInheritancesTest {

    @Test
    public void iterateClasses() throws Exception {
        final ClassInheritances classInheritances = new ClassInheritances(Aaa3.class);

        final Iterator<Class<?>> it = classInheritances.iterator();
        assertTrue(it.hasNext());
        assertEquals(Aaa3.class, it.next());
        assertTrue(it.hasNext());
        assertEquals(Aaa2.class, it.next());
        assertTrue(it.hasNext());
        assertEquals(Aaa1.class, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void iterateOneClass() throws Exception {
        final ClassInheritances classInheritances = new ClassInheritances(Aaa1.class);

        final Iterator<Class<?>> it = classInheritances.iterator();
        assertTrue(it.hasNext());
        assertEquals(Aaa1.class, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    public void notIterateWithObjectClass() throws Exception {
        final ClassInheritances classInheritances = new ClassInheritances(Object.class);

        final Iterator<Class<?>> it = classInheritances.iterator();
        assertFalse(it.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void throwNoSuchElementException() {
        final ClassInheritances classInheritances = new ClassInheritances(Aaa1.class);
        final Iterator<Class<?>> it = classInheritances.iterator();
        try {
            it.next();
        } catch (RuntimeException e) {
            throw new AssertionError("exception must not be thrown.", e);
        }
        it.next();  // should throw exception.
    }

    private static class Aaa1 {
    }

    private static class Aaa2 extends Aaa1 {
    }

    private static class Aaa3 extends Aaa2 {
    }
}
