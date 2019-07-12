package nablarch.fw.dicontainer.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

public final class MockServletContexts {

    public static ServletContext createMock() {
        return (ServletContext) Proxy.newProxyInstance(
                MockServletContexts.class.getClassLoader(),
                new Class[] { ServletContext.class },
                new MockInvocationHandler());
    }

    private static final class MockInvocationHandler implements InvocationHandler {

        private final Map<String, Object> map = new HashMap<>();

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            throw new UnsupportedOperationException(method.toGenericString());
        }
    }
}
