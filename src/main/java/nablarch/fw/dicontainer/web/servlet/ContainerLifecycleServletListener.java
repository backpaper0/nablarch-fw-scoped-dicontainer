package nablarch.fw.dicontainer.web.servlet;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class ContainerLifecycleServletListener
        implements ServletRequestListener, HttpSessionListener {

    @Override
    public void requestInitialized(final ServletRequestEvent sre) {
        //noop
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent sre) {
        final ServletRequest request = sre.getServletRequest();
        ServletAPIContext.destroyRequestComponents(request);
    }

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        //noop
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        ServletAPIContext.destroySessionComponents(session);
    }
}
