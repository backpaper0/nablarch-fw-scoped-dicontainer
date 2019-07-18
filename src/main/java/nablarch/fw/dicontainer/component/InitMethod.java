package nablarch.fw.dicontainer.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public interface InitMethod {

    void invoke(final Object component);

    static InitMethod noop() {
        return component -> {
        };
    }

    final class InitMethodImpl implements InitMethod {

        private final Method method;

        public InitMethodImpl(final Method method) {
            this.method = Objects.requireNonNull(method);
        }

        @Override
        public void invoke(final Object component) {
            if (method.isAccessible() == false) {
                method.setAccessible(true);
            }
            try {
                method.invoke(component);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        }
    }
}
