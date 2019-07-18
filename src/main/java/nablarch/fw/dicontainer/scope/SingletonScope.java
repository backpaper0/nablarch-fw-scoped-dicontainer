package nablarch.fw.dicontainer.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Provider;

import nablarch.fw.dicontainer.Observes;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.event.ContainerDestroy;

public final class SingletonScope extends AbstractScope {

    private final ConcurrentMap<ComponentId, InstanceHolder> instances = new ConcurrentHashMap<>();

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        InstanceHolder instanceHolder = instances.get(id);
        if (instanceHolder == null) {
            instanceHolder = new InstanceHolder();
            final InstanceHolder previous = instances.putIfAbsent(id, instanceHolder);
            if (previous != null && instanceHolder != previous) {
                instanceHolder = previous;
            }
        }
        return instanceHolder.get(provider);
    }

    @Observes
    public void destroy(final ContainerDestroy event) {
        idToDefinition.forEach((id, definition) -> {
            final InstanceHolder holder = instances.get(id);
            if (holder != null) {
                holder.destroy((ComponentDefinition<Object>) definition);
            }
        });
    }

    @Override
    public int dimensions() {
        return Integer.MAX_VALUE;
    }

    private static class InstanceHolder {

        Object instance;
        final Lock lock = new ReentrantLock();

        void destroy(final ComponentDefinition<Object> definition) {
            lock.lock();
            try {
                if (instance != null) {
                    definition.destroyComponent(instance);
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
