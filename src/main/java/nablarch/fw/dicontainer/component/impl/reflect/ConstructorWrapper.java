package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

public final class ConstructorWrapper {

    private final Constructor<?> constructor;

    public ConstructorWrapper(final Constructor<?> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    public Object newInstance(final Object... initargs) {
        if (constructor.isAccessible() == false) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance(initargs);
        } catch (final InstantiationException e) {
            throw new ReflectionException(e);
        } catch (final IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (final IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (final InvocationTargetException e) {
            throw new ReflectionException(e.getTargetException());
        }
    }
}
