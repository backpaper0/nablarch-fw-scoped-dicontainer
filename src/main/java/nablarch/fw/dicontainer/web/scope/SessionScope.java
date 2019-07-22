package nablarch.fw.dicontainer.web.scope;

import java.util.Objects;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.exception.web.WebContextException;
import nablarch.fw.dicontainer.scope.AbstractScope;
import nablarch.fw.dicontainer.web.context.SessionContext;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;

public final class SessionScope extends AbstractScope {

    private final SessionContextSupplier supplier;

    public SessionScope(final SessionContextSupplier supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider) {
        final SessionContext context = supplier.getSessionContext();
        if (context == null) {
            throw new WebContextException("SessionContext is not found.");
        }
        return context.getSessionComponent(id, provider);
    }

    @Override
    public int dimensions() {
        return 200;
    }

    public void destroyComponent(final ComponentId id, final Object component) {
        final ComponentDefinition<Object> definition = (ComponentDefinition<Object>) idToDefinition
                .get(id);
        definition.destroyComponent(component);
    }
}