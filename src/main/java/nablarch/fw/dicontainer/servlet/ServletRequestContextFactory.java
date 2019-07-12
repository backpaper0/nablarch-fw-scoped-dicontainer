package nablarch.fw.dicontainer.servlet;

import javax.servlet.ServletRequest;

import nablarch.fw.dicontainer.RequestContext;
import nablarch.fw.dicontainer.RequestContextFactory;

public final class ServletRequestContextFactory implements RequestContextFactory {

    @Override
    public RequestContext create(final Object request) {
        if (request instanceof ServletRequest == false) {
            //TODO error
            throw new RuntimeException();
        }
        return new ServletRequestContext((ServletRequest) request);
    }
}
