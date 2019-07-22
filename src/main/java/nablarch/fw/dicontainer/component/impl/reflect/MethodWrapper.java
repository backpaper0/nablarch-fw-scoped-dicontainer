package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

public final class MethodWrapper {

    private final Method method;

    public MethodWrapper(final Method method) {
        this.method = Objects.requireNonNull(method);
    }

    public Object invoke(final Object obj, final Object... args) {
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(obj, args);
        } catch (final IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (final IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (final InvocationTargetException e) {
            throw new ReflectionException(e.getTargetException());
        }
    }

    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    public int getParameterCount() {
        return method.getParameterCount();
    }

    public Class<?> getParameterType(final int index) {
        return method.getParameterTypes()[index];
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
