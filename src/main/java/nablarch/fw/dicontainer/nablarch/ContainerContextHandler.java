package nablarch.fw.dicontainer.nablarch;

import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.dicontainer.web.servlet.ServletAPIContextSupplier;
import nablarch.fw.web.servlet.ServletExecutionContext;

public final class ContainerContextHandler implements Handler<Object, Object> {

    private ServletAPIContextSupplier contextSupplier;

    @Override
    public Object handle(final Object data, final ExecutionContext context) {
        final ServletExecutionContext sec = (ServletExecutionContext) context;
        return contextSupplier.doWithContext(sec.getServletRequest(),
                () -> context.handleNext(data));
    }

    public void setContextSupplier(final ServletAPIContextSupplier contextSupplier) {
        this.contextSupplier = contextSupplier;
    }
}
