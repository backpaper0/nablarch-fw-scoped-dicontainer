package nablarch.fw.dicontainer.web.servlet;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

import nablarch.fw.dicontainer.component.DestroyMethod;

public interface SerializedDestroyMethod extends Serializable {

    DestroyMethod deserialize();

    static SerializedDestroyMethod noop() {
        return () -> DestroyMethod.noop();
    }

    static SerializedDestroyMethod fromMethod(final Method method) {
        return new SerializedDestroyMethodImpl(method.getDeclaringClass(), method.getName());
    }

    final class SerializedDestroyMethodImpl implements SerializedDestroyMethod {

        private final Class<?> declaringClass;
        private final String methodName;

        public SerializedDestroyMethodImpl(final Class<?> declaringClass, final String methodName) {
            this.declaringClass = Objects.requireNonNull(declaringClass);
            this.methodName = Objects.requireNonNull(methodName);
        }

        @Override
        public DestroyMethod deserialize() {
            try {
                final Method method = declaringClass.getDeclaredMethod(methodName);
                return DestroyMethod.fromMethod(method);
            } catch (final NoSuchMethodException e) {
                //TODO error
                throw new RuntimeException(e);
            }
        }
    }
}
