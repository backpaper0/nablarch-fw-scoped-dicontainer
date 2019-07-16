package nablarch.fw.dicontainer;

import java.util.Objects;

import javax.inject.Provider;

public final class RequestScope implements Scope {

    private final RequestContextSupplier supplier;

    public RequestScope(final RequestContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final RequestContext context = supplier.getRequestContext();
        if (context == null) {
            //TODO error
            throw new RuntimeException();
        }
        return context.getRequestComponent(key, provider, destroyMethod);
    }
}
