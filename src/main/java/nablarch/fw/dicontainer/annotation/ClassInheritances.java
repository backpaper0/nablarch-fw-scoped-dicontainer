package nablarch.fw.dicontainer.annotation;

import java.util.Iterator;
import java.util.Objects;

public final class ClassInheritances implements Iterable<Class<?>> {

    private final Class<?> clazz;

    public ClassInheritances(final Class<?> clazz) {
        this.clazz = Objects.requireNonNull(clazz);
    }

    @Override
    public Iterator<Class<?>> iterator() {
        return new Iterator<Class<?>>() {

            private Class<?> c = clazz;

            @Override
            public boolean hasNext() {
                return c != Object.class;
            }

            @Override
            public Class<?> next() {
                final Class<?> returnValue = c;
                c = c.getSuperclass();
                return returnValue;
            }
        };
    }
}
