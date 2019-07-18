package nablarch.fw.dicontainer.web;

import java.util.Objects;

import javax.inject.Provider;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.Scope;
import nablarch.fw.dicontainer.exception.web.WebContextException;

public final class SessionScope implements Scope {

    private final SessionContextSupplier supplier;

    public SessionScope(final SessionContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final SessionContext context = supplier.getSessionContext();
        if (context == null) {
            throw new WebContextException();
        }
        return context.getSessionComponent(id, provider, destroyMethod);
    }

    @Override
    public int dimensions() {
        return 200;
    }
}