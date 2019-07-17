package nablarch.fw.dicontainer.web;

import java.util.Objects;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Scope;
import nablarch.fw.dicontainer.config.DestroyMethod;

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

    @Override
    public int dimensions() {
        return 100;
    }
}
