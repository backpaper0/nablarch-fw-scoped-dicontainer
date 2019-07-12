package nablarch.fw.dicontainer;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

public final class RequestScope implements Scope {

    private final ThreadLocal<RequestContext> requestContexts = new ThreadLocal<>();
    private final RequestContextFactory factory;

    public RequestScope(final RequestContextFactory factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    public void runInScope(final Object request, final Runnable r) {
        final RequestContext requestContext = factory.create(request);
        requestContexts.set(requestContext);
        try {
            r.run();
        } finally {
            requestContexts.remove();
        }
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final Set<DestroyMethod> destroyMethods) {
        final RequestContext requestContext = requestContexts.get();
        if (requestContext == null) {
            //TODO error
            throw new RuntimeException();
        }
        return requestContext.get(key, provider, destroyMethods);
    }
}
