package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Provider;

public final class SingletonScope implements Scope {

    private final ConcurrentMap<ComponentKey<?>, InstanceHolder> instances = new ConcurrentHashMap<>();

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final Set<DestroyMethod> destroyMethods) {
        InstanceHolder instanceHolder = instances.get(key);
        if (instanceHolder == null) {
            instanceHolder = new InstanceHolder(destroyMethods);
            final InstanceHolder previous = instances.putIfAbsent(key, instanceHolder);
            if (previous != null && instanceHolder != previous) {
                instanceHolder = previous;
            }
        }
        return instanceHolder.get(provider);
    }

    @Observes
    public void destroy(final ContainerDestroy event) {
        for (final InstanceHolder instance : instances.values()) {
            instance.destroy();
        }
    }

    private static class InstanceHolder {

        Object instance;
        final Lock lock = new ReentrantLock();
        private final Set<DestroyMethod> destroyMethods;

        InstanceHolder(final Set<DestroyMethod> destroyMethods) {
            this.destroyMethods = Objects.requireNonNull(destroyMethods);
        }

        void destroy() {
            lock.lock();
            try {
                if (instance != null) {
                    for (final DestroyMethod destroyMethod : destroyMethods) {
                        destroyMethod.invoke(instance);
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        <T> T get(final Provider<T> provider) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = provider.get();
                }
                return (T) instance;
            } finally {
                lock.unlock();
            }
        }
    }
}
