package nablarch.fw.dicontainer.web.servlet;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.DestroyMethod;

public final class SerializedInstanceHolder implements Serializable {

    private Object instance;
    private final Set<SerializedDestroyMethod> destroyMethods;
    private boolean destroyed;

    public SerializedInstanceHolder(final Set<SerializedDestroyMethod> destroyMethods) {
        this.destroyMethods = Objects.requireNonNull(destroyMethods);
    }

    public <T> T getComponent(final Provider<T> provider) {
        if (destroyed) {
            //TODO error
            throw new RuntimeException();
        }
        if (instance == null) {
            instance = provider.get();
        }
        return (T) instance;
    }

    public void destroy() {
        if (destroyed == false && instance != null) {
            destroyed = true;
            for (final SerializedDestroyMethod serializedDestroyMethod : destroyMethods) {
                final DestroyMethod destroyMethod = serializedDestroyMethod.deserialize();
                destroyMethod.invoke(instance);
            }
        }
    }
}
