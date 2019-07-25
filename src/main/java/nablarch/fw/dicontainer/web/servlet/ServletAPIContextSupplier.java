package nablarch.fw.dicontainer.web.servlet;

import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.RequestContextSupplier;
import nablarch.fw.dicontainer.web.context.SessionContext;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;
import nablarch.fw.dicontainer.web.exception.WebContextException;

public final class ServletAPIContextSupplier
        implements RequestContextSupplier, SessionContextSupplier {

    private final ThreadLocal<ServletAPIContext> contexts = new ThreadLocal<>();

    public <T> T doWithContext(final HttpServletRequest request, final Supplier<T> action) {
        if (contexts.get() != null) {
            throw new WebContextException(
                    "Method [" + getClass().getName() + "#doWithContext] must not be nested.");
        }
        final ServletAPIContext context = new ServletAPIContext(request);
        contexts.set(context);
        try {
            return action.get();
        } finally {
            context.destroy();
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
