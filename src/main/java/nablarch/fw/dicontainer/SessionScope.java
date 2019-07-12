package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

public final class SessionScope implements Scope {

    private final ThreadLocal<SessionContext> contexts = new ThreadLocal<>();
    private final SessionContextFactory factory;

    public SessionScope(final SessionContextFactory factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public void runInScope(final Object request, final Runnable r) {
        final SessionContext context = factory.create(request);
        contexts.set(context);
        try {
            r.run();
        } finally {
            contexts.remove();
        }
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final Set<DestroyMethod> destroyMethods) {
        final SessionContext context = contexts.get();
        if (context == null) {
            //TODO error
            throw new RuntimeException();
        }
        return context.get(key, provider, destroyMethods);
    }
}