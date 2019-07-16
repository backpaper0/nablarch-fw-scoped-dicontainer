package nablarch.fw.dicontainer.servlet;

import javax.servlet.http.HttpServletRequest;

import nablarch.fw.dicontainer.RequestContext;
import nablarch.fw.dicontainer.RequestContextSupplier;
import nablarch.fw.dicontainer.SessionContext;
import nablarch.fw.dicontainer.SessionContextSupplier;

public final class ServletAPIContextSupplier
        implements RequestContextSupplier, SessionContextSupplier {

    private final ThreadLocal<ServletAPIContext> contexts = new ThreadLocal<>();

    public void doWithContext(final HttpServletRequest request, final Runnable action) {
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
