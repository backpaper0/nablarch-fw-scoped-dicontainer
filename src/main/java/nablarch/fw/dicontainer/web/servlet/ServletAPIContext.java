package nablarch.fw.dicontainer.web.servlet;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Provider;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.nablarch.ContainerImplementers;
import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.SessionContext;

public final class ServletAPIContext implements RequestContext, SessionContext {

    private final HttpServletRequest request;
    private final Map<String, ComponentHolder> gotSessionComponentsPerRequest = new HashMap<>();

    public ServletAPIContext(final HttpServletRequest request) {
        this.request = Objects.requireNonNull(request);
    }

    public void destroy() {
        final HttpSession session = request.getSession();
        gotSessionComponentsPerRequest.forEach(session::setAttribute);
    }

    @Override
    public <T> T getRequestComponent(final ComponentId id, final Provider<T> provider) {
        final String name = getKeyPrefix() + id;
        ComponentHolder holder = (ComponentHolder) request.getAttribute(name);
        if (holder == null) {
            final T component = provider.get();
            holder = new ComponentHolder(id, component);
            request.setAttribute(name, holder);
        }
        return (T) holder.getComponent();
    }

    public static void destroyRequestComponents(final ServletRequest request) {
        final ContainerImplementer container = ContainerImplementers.get();
        final String keyPrefix = getKeyPrefix();
        final Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            if (name.startsWith(keyPrefix)) {
                final ComponentHolder holder = (ComponentHolder) request
                        .getAttribute(name);
                holder.destroy(container);
                request.removeAttribute(name);
            }
        }
    }

    @Override
    public <T> T getSessionComponent(final ComponentId id, final Provider<T> provider) {
        final HttpSession session = request.getSession();
        synchronized (session.getId().intern()) {
            final String name = getKeyPrefix() + id;
            ComponentHolder holder = (ComponentHolder) session.getAttribute(name);
            if (holder == null) {
                final T component = provider.get();
                holder = new ComponentHolder(id, component);
                session.setAttribute(name, holder);
            }
            gotSessionComponentsPerRequest.put(name, holder);
            return (T) holder.getComponent();
        }
    }

    public static void destroySessionComponents(final HttpSession session) {
        synchronized (session.getId().intern()) {
            final ContainerImplementer container = ContainerImplementers.get();
            final String keyPrefix = getKeyPrefix();
            final Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                if (name.startsWith(keyPrefix)) {
                    final ComponentHolder holder = (ComponentHolder) session
                            .getAttribute(name);
                    holder.destroy(container);
                    session.removeAttribute(name);
                }
            }
        }
    }

    private static String getKeyPrefix() {
        return "components:";
    }

    private static final class ComponentHolder implements Serializable {

        private final ComponentId id;
        private final Object component;

        public ComponentHolder(final ComponentId id, final Object component) {
            this.id = Objects.requireNonNull(id);
            this.component = Objects.requireNonNull(component);
        }

        public void destroy(final ContainerImplementer container) {
            final ComponentDefinition<Object> definition = container.getComponentDefinition(id);
            definition.destroyComponent(component);
        }

        public Object getComponent() {
            return component;
        }
    }
}
