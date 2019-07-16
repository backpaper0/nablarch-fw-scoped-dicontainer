package nablarch.fw.dicontainer.servlet;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Provider;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.DestroyMethod;
import nablarch.fw.dicontainer.RequestContext;
import nablarch.fw.dicontainer.SessionContext;

public class ServletAPIContext implements RequestContext, SessionContext {

    private final HttpServletRequest request;

    public ServletAPIContext(final HttpServletRequest request) {
        this.request = Objects.requireNonNull(request);
    }

    @Override
    public <T> T getRequestComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final String name = getKeyPrefix() + key.getFullyQualifiedClassName();
        RequestComponentsHolder holder = (RequestComponentsHolder) request.getAttribute(name);
        if (holder == null) {
            holder = new RequestComponentsHolder();
            request.setAttribute(name, holder);
        }
        final T component = holder.getComponent(key, provider, destroyMethod);
        return component;
    }

    public static void destroyRequestComponents(final ServletRequest request) {
        final String keyPrefix = getKeyPrefix();
        final Enumeration<String> names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            if (name.startsWith(keyPrefix)) {
                final RequestComponentsHolder holder = (RequestComponentsHolder) request
                        .getAttribute(name);
                if (holder != null) {
                    holder.destroy();
                }
                request.removeAttribute(name);
            }
        }
    }

    @Override
    public <T> T getSessionComponent(final ComponentKey<T> key, final Provider<T> provider,
            final DestroyMethod destroyMethod) {
        final HttpSession session = request.getSession();
        synchronized (session.getId().intern()) {
            final String name = getKeyPrefix() + key.getFullyQualifiedClassName();
            SessionComponentsHolder holder = (SessionComponentsHolder) session.getAttribute(name);
            if (holder == null) {
                holder = new SessionComponentsHolder();
                session.setAttribute(name, holder);
            }
            final T component = holder.getComponent(key, provider, destroyMethod.serialize());
            return component;
        }
    }

    public static void destroySessionComponents(final HttpSession session) {
        synchronized (session.getId().intern()) {
            final String keyPrefix = getKeyPrefix();
            final Enumeration<String> names = session.getAttributeNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                if (name.startsWith(keyPrefix)) {
                    final SessionComponentsHolder holder = (SessionComponentsHolder) session
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

    private static final class RequestComponentsHolder {

        private final Map<ComponentKey<?>, RequestComponentHolder> components = new HashMap<>();

        public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
                final DestroyMethod destroyMethod) {
            if (components.containsKey(key) == false) {
                final RequestComponentHolder holder = new RequestComponentHolder(destroyMethod);
                components.put(key, holder);
            }
            final RequestComponentHolder holder = components.get(key);
            return holder.getComponent(provider);
        }

        public void destroy() {
            for (final RequestComponentHolder holder : components.values()) {
                holder.destroy();
            }
        }
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

    private static final class SessionComponentsHolder implements Serializable {

        private final Map<ComponentKey<?>, SessionComponentHolder> components = new HashMap<>();

        public <T> T getComponent(final ComponentKey<T> key, final Provider<T> provider,
                final SerializedDestroyMethod destroyMethod) {
            if (components.containsKey(key) == false) {
                final SessionComponentHolder holder = new SessionComponentHolder(destroyMethod);
                components.put(key, holder);
            }
            final SessionComponentHolder holder = components.get(key);
            return holder.getComponent(provider);
        }

        public void destroy() {
            for (final SessionComponentHolder holder : components.values()) {
                holder.destroy();
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
