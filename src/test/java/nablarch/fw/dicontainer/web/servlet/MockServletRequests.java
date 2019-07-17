package nablarch.fw.dicontainer.web.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class MockServletRequests {

    public static HttpServletRequest createMock(final HttpSession session) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                MockServletRequests.class.getClassLoader(),
                new Class[] { HttpServletRequest.class },
                new MockInvocationHandler(session));
    }

    public static HttpServletRequest createMock() {
        return createMock(MockHttpSessions.createMock());
    }

    private static final class MockInvocationHandler implements InvocationHandler {

        private final Map<String, Object> map = new HashMap<>();
        private final HttpSession session;

        public MockInvocationHandler(final HttpSession session) {
            this.session = session;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args)
                throws Throwable {
            if (method.getName().equals("getAttribute")) {
                return map.get(args[0]);
            } else if (method.getName().equals("setAttribute")) {
                map.put((String) args[0], args[1]);
                return null;
            } else if (method.getName().equals("removeAttribute")) {
                map.remove(args[0]);
                return null;
            } else if (method.getName().equals("getSession")) {
                return session;
            } else if (method.getName().equals("getAttributeNames")) {
                return Collections.enumeration(map.keySet());
            }
            throw new UnsupportedOperationException(method.toGenericString());
        }
    }
}
