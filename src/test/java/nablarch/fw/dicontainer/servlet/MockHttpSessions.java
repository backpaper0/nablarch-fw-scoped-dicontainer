package nablarch.fw.dicontainer.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

public final class MockHttpSessions {

    public static HttpSession createMock() {
        return (HttpSession) Proxy.newProxyInstance(
                MockHttpSessions.class.getClassLoader(),
                new Class[] { HttpSession.class },
                new MockInvocationHandler());
    }

    private static final class MockInvocationHandler implements InvocationHandler {

        private final Map<String, Object> map = new HashMap<>();
        private final String id = UUID.randomUUID().toString();

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if (method.getName().equals("getAttribute")) {
                return map.get(args[0]);
            } else if (method.getName().equals("setAttribute")) {
                map.put((String) args[0], args[1]);
                return null;
            } else if (method.getName().equals("removeAttribute")) {
                map.remove(args[0]);
                return null;
            } else if (method.getName().equals("getAttributeNames")) {
                return Collections.enumeration(map.keySet());
            } else if (method.getName().equals("getId")) {
                return id;
            }
            throw new UnsupportedOperationException(method.toGenericString());
        }
    }
}
