package nablarch.fw.dicontainer.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
        return createMock(new MockInvocationHandler());
    }

    private static HttpSession createMock(final InvocationHandler invocationHandler) {
        return (HttpSession) Proxy.newProxyInstance(
                MockHttpSessions.class.getClassLoader(),
                new Class[] { HttpSession.class },
                invocationHandler);
    }

    public static byte[] serialize(final HttpSession session) throws IOException {
        final MockInvocationHandler invocationHandler = (MockInvocationHandler) Proxy
                .getInvocationHandler(session);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(invocationHandler);
            oos.flush();
        }
        return baos.toByteArray();
    }

    public static HttpSession createMock(final byte[] serialized)
            throws IOException, ClassNotFoundException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            final MockInvocationHandler invocationHandler = (MockInvocationHandler) ois
                    .readObject();
            return createMock(invocationHandler);
        }
    }

    private static final class MockInvocationHandler implements InvocationHandler, Serializable {

        private final Map<String, Object> map = new HashMap<>();
        private final String id = UUID.randomUUID().toString();

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
            } else if (method.getName().equals("getAttributeNames")) {
                return Collections.enumeration(map.keySet());
            } else if (method.getName().equals("getId")) {
                return id;
            }
            throw new UnsupportedOperationException(method.toGenericString());
        }
    }
}
