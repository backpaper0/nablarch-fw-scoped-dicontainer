package nablarch.fw.dicontainer.web.servlet;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Objects;

import javax.inject.Provider;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.web.RequestContext;
import nablarch.fw.dicontainer.web.SessionContext;

public class ServletAPIContext implements RequestContext, SessionContext {

    private final HttpServletRequest request;

    public ServletAPIContext(final HttpServletRequest request) {
        this.request = Objects.requireNonNull(request);
    }

    @Override
    public <T> T getRequestComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final String name = getKeyPrefix() + id;
        RequestComponentHolder holder = (RequestComponentHolder) request.getAttribute(name);
        if (holder == null) {
            holder = new RequestComponentHolder(destroyMethod);
            request.setAttribute(name, holder);
        }
        return holder.getComponent(provider);
    }

    public static void destroyRequestComponents(final ServletRequest request) {
        final String keyPrefix = getKeyPrefix();
        final Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            if (name.startsWith(keyPrefix)) {
                final RequestComponentHolder holder = (RequestComponentHolder) request
                        .getAttribute(name);
                if (holder != null) {
                    holder.destroy();
                }
                request.removeAttribute(name);
            }
        }
    }

    @Override
    public <T> T getSessionComponent(final ComponentId id, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final HttpSession session = request.getSession();
        synchronized (session.getId().intern()) {
            final String name = getKeyPrefix() + id;
            SessionComponentHolder holder = (SessionComponentHolder) session.getAttribute(name);
            if (holder == null) {
                holder = new SessionComponentHolder(destroyMethod.serialize());
                session.setAttribute(name, holder);
            }
            return holder.getComponent(provider);
        }
    }

    public static void destroySessionComponents(final HttpSession session) {
        synchronized (session.getId().intern()) {
            final String keyPrefix = getKeyPrefix();
            final Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                if (name.startsWith(keyPrefix)) {
                    final SessionComponentHolder holder = (SessionComponentHolder) session
                            .getAttribute(name);
                    if (holder != null) {
                        holder.destroy();
                    }
                    session.removeAttribute(name);
                }
            }
        }
    }

    private static String getKeyPrefix() {
        return "components:";
    }

    private static final class RequestComponentHolder {

        private Object instance;
        private final DestroyMethod destroyMethod;

        public RequestComponentHolder(final DestroyMethod destroyMethod) {
            this.destroyMethod = Objects.requireNonNull(destroyMethod);
        }

        public <T> T getComponent(final Provider<T> provider) {
            if (instance == null) {
                instance = provider.get();
            }
            return (T) instance;
        }

        public void destroy() {
            if (instance != null) {
                destroyMethod.invoke(instance);
            }
        }
    }

    private static final class SessionComponentHolder implements Serializable {

        private Object instance;
        private final SerializedDestroyMethod destroyMethod;

        public SessionComponentHolder(final SerializedDestroyMethod destroyMethod) {
            this.destroyMethod = Objects.requireNonNull(destroyMethod);
        }

        public <T> T getComponent(final Provider<T> provider) {
            if (instance == null) {
                instance = provider.get();
            }
            return (T) instance;
        }

        public void destroy() {
            if (instance != null) {
                destroyMethod.deserialize().invoke(instance);
            }
        }
    }
}
