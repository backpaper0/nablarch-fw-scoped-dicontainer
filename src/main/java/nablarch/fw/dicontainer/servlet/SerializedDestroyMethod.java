package nablarch.fw.dicontainer.servlet;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

import nablarch.fw.dicontainer.DestroyMethod;

public final class SerializedDestroyMethod implements Serializable {

    private final Class<?> declaringClass;
    private final String methodName;

    public SerializedDestroyMethod(final Class<?> declaringClass, final String methodName) {
        this.declaringClass = Objects.requireNonNull(declaringClass);
        this.methodName = Objects.requireNonNull(methodName);
    }

    public DestroyMethod deserialize() {
        try {
            final Method method = declaringClass.getDeclaredMethod(methodName);
            return new DestroyMethod(method);
        } catch (final NoSuchMethodException e) {
            //TODO error
            throw new RuntimeException(e);
        }
    }
}
