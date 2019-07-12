package nablarch.fw.dicontainer.servlet;

import javax.servlet.http.HttpServletRequest;

import nablarch.fw.dicontainer.SessionContext;
import nablarch.fw.dicontainer.SessionContextFactory;

public final class HttpSessionContextFactory implements SessionContextFactory {

    @Override
    public SessionContext create(final Object request) {
        if (request instanceof HttpServletRequest == false) {
            //TODO error
            throw new RuntimeException();
        }
        return new HttpSessionContext((HttpServletRequest) request);
    }
}
