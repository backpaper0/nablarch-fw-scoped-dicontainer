package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Provider;

public final class SingletonScope implements Scope {

    private final ConcurrentMap<ComponentKey<?>, Instance> instances = new ConcurrentHashMap<>();

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final Set<DestroyMethod> destroyMethods) {
        Instance instance = instances.get(key);
        if (instance == null) {
            instance = new Instance(destroyMethods);
            final Instance previous = instances.putIfAbsent(key, instance);
            if (previous != null && instance != previous) {
                instance = previous;
            }
        }
        return instance.get(provider);
    }

    @Observes
    public void destroy(final ContainerDestroy event) {
        for (final Instance instance : instances.values()) {
            instance.destroy();
        }
    }

    private static class Instance {

        Object instance;
        final Lock lock = new ReentrantLock();
        private final Set<DestroyMethod> destroyMethods;

        Instance(final Set<DestroyMethod> destroyMethods) {
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
