package nablarch.fw.dicontainer.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ContainerLifecycleServletListener implements ServletRequestListener {

    @Override
    public void requestInitialized(final ServletRequestEvent sre) {
        //noop
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent sre) {
        final ServletRequest request = sre.getServletRequest();
        ServletRequestContext.destroy(request);
    }
}
