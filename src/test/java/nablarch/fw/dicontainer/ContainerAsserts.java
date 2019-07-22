package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.Iterator;

import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;

public final class ContainerAsserts {

    @SafeVarargs
    public static void assertContainerException(final ContainerCreationException e,
            final Class<? extends ContainerException>... expecteds) {
        for (final ContainerException exception : e.getExceptions()) {
            System.out.printf("%s: %s%n", exception.getClass().getName(), exception.getMessage());
        }
        final Iterator<ContainerException> it = e.getExceptions().iterator();
        for (final Class<? extends ContainerException> expected : expecteds) {
            assertTrue(it.hasNext());
            assertEquals(expected, it.next().getClass());
        }
        assertFalse(it.hasNext());
    }
}
