package nablarch.fw.dicontainer.nablarch;

import javax.inject.Provider;

import nablarch.common.web.session.SessionUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.SessionContext;

public final class NablarchWebContext implements RequestContext, SessionContext {

    private static final String NAME_PREFIX = "components:";
    private final ExecutionContext ctx;

    public NablarchWebContext(final ExecutionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public <T> T getRequestComponent(final ComponentId id, final Provider<T> provider) {
        final String name = NAME_PREFIX + id;
        Object component = ctx.getRequestScopedVar(name);
        if (component == null) {
            component = provider.get();
            ctx.setRequestScopedVar(name, component);
        }
        return (T) component;
    }

    @Override
    public <T> T getSessionComponent(final ComponentId id, final Provider<T> provider) {
        final String name = NAME_PREFIX + id;
        Object component = SessionUtil.get(ctx, name);
        if (component == null) {
            component = provider.get();
            SessionUtil.put(ctx, name, component);
        }
        return (T) component;
    }
}
