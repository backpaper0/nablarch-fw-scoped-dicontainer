package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.RequestContextSupplier;
import nablarch.fw.dicontainer.web.context.SessionContext;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;
import nablarch.fw.dicontainer.web.exception.WebContextException;

public final class NablarchWebContextHandler
        implements Handler<Object, Object>, RequestContextSupplier, SessionContextSupplier {

    private final ThreadLocal<NablarchWebContext> contexts = new ThreadLocal<>();

    @Override
    public Object handle(final Object data, final ExecutionContext context) {
        if (contexts.get() != null) {
            throw new WebContextException(
                    "Method [" + getClass().getName() + "#handle] must not be nested.");
        }
        final NablarchWebContext ctx = new NablarchWebContext(context);
        contexts.set(ctx);
        try {
            return context.handleNext(data);
        } finally {
            contexts.remove();
        }
    }

    @Override
    public RequestContext getRequestContext() {
        return contexts.get();
    }

    @Override
    public SessionContext getSessionContext() {
        return contexts.get();
    }
}
