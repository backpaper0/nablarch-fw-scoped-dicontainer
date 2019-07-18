package nablarch.fw.dicontainer.web;

import java.util.Objects;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.Scope;
import nablarch.fw.dicontainer.exception.web.WebContextException;

public final class RequestScope implements Scope {

    private final RequestContextSupplier supplier;

    public RequestScope(final RequestContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final RequestContext context = supplier.getRequestContext();
        if (context == null) {
            throw new WebContextException();
        }
        return context.getRequestComponent(id, provider, destroyMethod);
    }

    @Override
    public int dimensions() {
        return 100;
    }
}
