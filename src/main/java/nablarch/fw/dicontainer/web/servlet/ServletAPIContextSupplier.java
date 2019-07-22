package nablarch.fw.dicontainer.web.servlet;

import javax.servlet.http.HttpServletRequest;

import nablarch.fw.dicontainer.exception.web.WebContextException;
import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.RequestContextSupplier;
import nablarch.fw.dicontainer.web.context.SessionContext;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;

public final class ServletAPIContextSupplier
        implements RequestContextSupplier, SessionContextSupplier {

    private final ThreadLocal<ServletAPIContext> contexts = new ThreadLocal<>();

    public void doWithContext(final HttpServletRequest request, final Runnable action) {
        if (contexts.get() != null) {
            throw new WebContextException(
                    "Method [" + getClass().getName() + "#doWithContext] must not be nested.");
        }
        contexts.set(new ServletAPIContext(request));
        try {
            action.run();
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
