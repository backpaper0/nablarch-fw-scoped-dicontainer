package nablarch.fw.dicontainer.servlet;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.DestroyMethod;
import nablarch.fw.dicontainer.SessionContext;

public final class HttpSessionContext implements SessionContext {

    private final HttpServletRequest request;

    public HttpSessionContext(final HttpServletRequest request) {
        this.request = Objects.requireNonNull(request);
    }

    @Override
    public <T> T get(final ComponentKey<T> key, final Provider<T> provider, final Set<DestroyMethod> destroyMethods) {
        final HttpSession session = request.getSession();
        synchronized (session.getId().intern()) {
            final String sessionKey = key.getSessionKey();
            SerializedInstanceHolder instanceHolder = (SerializedInstanceHolder) session.getAttribute(sessionKey);
            if (instanceHolder == null) {
                final Set<SerializedDestroyMethod> serializedDestroyMethods = destroyMethods.stream().map(DestroyMethod::serialize)
                        .collect(Collectors.toCollection(HashSet::new));
                instanceHolder = new SerializedInstanceHolder(serializedDestroyMethods);
                session.setAttribute(sessionKey, instanceHolder);
            }
            return instanceHolder.getComponent(provider);
        }
    }

    public static void destroy(final HttpSession session) {
        synchronized (session.getId().intern()) {
            final String sessionKeyPrefix = ComponentKey.getSessionKeyPrefix();
            final Enumeration<String> enumeration = session.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                final String name = enumeration.nextElement();
                if (name.startsWith(sessionKeyPrefix)) {
                    final SerializedInstanceHolder instanceHolder = (SerializedInstanceHolder) session.getAttribute(name);
                    instanceHolder.destroy();
                }
            }
        }
    }
}
