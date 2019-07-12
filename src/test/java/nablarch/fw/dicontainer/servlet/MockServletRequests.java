package nablarch.fw.dicontainer.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

public final class MockServletRequests {

    public static ServletRequest createMock() {
        return (ServletRequest) Proxy.newProxyInstance(
                MockServletRequests.class.getClassLoader(),
                new Class[] { ServletRequest.class },
                new MockInvocationHandler());
    }

    private static final class MockInvocationHandler implements InvocationHandler {

        private final Map<String, Object> map = new HashMap<>();

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
            }
            throw new UnsupportedOperationException(method.toGenericString());
        }
    }
}
