package nablarch.fw.dicontainer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Provider;

public final class SingletonScope implements Scope {

    private final ConcurrentMap<ComponentKey<?>, Instance> instances = new ConcurrentHashMap<>();

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider) {
        Instance instance = instances.get(key);
        if (instance == null) {
            instance = new Instance();
            final Instance previous = instances.putIfAbsent(key, instance);
            if (previous != null && instance != previous) {
                instance = previous;
            }
        }
        return instance.get(provider);
    }

    private static class Instance {

        Object instance;
        final Lock lock = new ReentrantLock();

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
