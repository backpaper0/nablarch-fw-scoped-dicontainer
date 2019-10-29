package nablarch.fw.dicontainer.annotation;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * スーパークラスを辿ってイテレーションする{@link Iterable}実装クラス。
 *
 */
public final class ClassInheritances implements Iterable<Class<?>> {

    /**
     * 起点となるクラス
     */
    private final Class<?> clazz;

    /**
     * インスタンスを生成する。
     * 
     * @param clazz 起点となるクラス
     */
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
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                final Class<?> returnValue = c;
                c = c.getSuperclass();
                return returnValue;
            }
        };
    }
}
