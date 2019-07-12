package nablarch.fw.dicontainer.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;
import javax.servlet.ServletRequest;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.DestroyMethod;
import nablarch.fw.dicontainer.RequestContext;

public final class ServletRequestContext implements RequestContext {

    private final ServletRequest request;

    public ServletRequestContext(final ServletRequest request) {
        this.request = Objects.requireNonNull(request);
    }

    @Override
    public <T> T get(final ComponentKey<T> key, final Provider<T> provider, final Set<DestroyMethod> destroyMethods) {
        Repository repository = Repository.extract(request);
        if (repository == null) {
            repository = Repository.initialize(request);
        }
        final InstanceHolder instanceHolder = repository.get(key, destroyMethods);
        return instanceHolder.getInstance(provider);
    }

    public static void destroy(final ServletRequest request) {
        final Repository repository = Repository.extract(request);
        if (repository != null) {
            repository.destroy();
            Repository.remove(request);
        }
    }

    private static final class Repository {

        private final Map<ComponentKey<?>, InstanceHolder> instanceHolders = new HashMap<>();

        public InstanceHolder get(final ComponentKey<?> key, final Set<DestroyMethod> destroyMethods) {
            InstanceHolder instanceHolder = instanceHolders.get(key);
            if (instanceHolder == null) {
                instanceHolder = new InstanceHolder(destroyMethods);
                instanceHolders.put(key, instanceHolder);
            }
            return instanceHolder;
        }

        public void destroy() {
            for (final InstanceHolder instanceHolder : instanceHolders.values()) {
                instanceHolder.destroy();
            }
        }

        public static Repository initialize(final ServletRequest request) {
            final Repository repository = new Repository();
            request.setAttribute(ServletRequestContext.class.getName(), repository);
            return repository;
        }

        public static Repository extract(final ServletRequest request) {
            return (Repository) request.getAttribute(ServletRequestContext.class.getName());
        }

        public static void remove(final ServletRequest request) {
            request.removeAttribute(ServletRequestContext.class.getName());
        }
    }

    private static final class InstanceHolder {

        private Object instance;
        private final Set<DestroyMethod> destroyMethods;
        private boolean destroyed;

        public InstanceHolder(final Set<DestroyMethod> destroyMethods) {
            this.destroyMethods = Objects.requireNonNull(destroyMethods);
        }

        public <T> T getInstance(final Provider<T> provider) {
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
                for (final DestroyMethod destroyMethod : destroyMethods) {
                    destroyMethod.invoke(instance);
                }
                destroyed = true;
            }
        }
    }
}
