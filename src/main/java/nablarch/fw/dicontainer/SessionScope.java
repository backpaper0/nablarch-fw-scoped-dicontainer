package nablarch.fw.dicontainer;

import java.util.Objects;

import javax.inject.Provider;

public final class SessionScope implements Scope {

    private final SessionContextSupplier supplier;

    public SessionScope(final SessionContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final SessionContext context = supplier.getSessionContext();
        if (context == null) {
            //TODO error
            throw new RuntimeException();
        }
        return context.getSessionComponent(key, provider, destroyMethod);
    }
}