package nablarch.fw.dicontainer.servlet;

import static org.junit.Assert.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.AnnotationScopeDecider;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.RequestContextFactory;
import nablarch.fw.dicontainer.RequestScope;
import nablarch.fw.dicontainer.RequestScoped;

public class ServletRequestContextFactoryTest {

    private RequestScope requestScope;
    private AnnotationContainerBuilder builder;

    @Before
    public void setUp() throws Exception {
        final RequestContextFactory factory = new ServletRequestContextFactory();
        requestScope = new RequestScope(factory);
        final AnnotationScopeDecider decider = AnnotationScopeDecider.builder()
                .addScope(RequestScoped.class, requestScope)
                .build();
        builder = new AnnotationContainerBuilder(decider);
    }

    @Test
    public void getComponent() throws Exception {
        final Container container = builder
                .register(Aaa.class)
                .build();

        final Aaa[] components = new Aaa[2];
        requestScope.runInScope(MockServletRequests.createMock(), () -> {
            components[0] = container.getComponent(Aaa.class);
            components[1] = container.getComponent(Aaa.class);
        });

        assertNotNull(components[0]);
        assertNotNull(components[1]);
        assertTrue(components[0] == components[1]);
    }

    @Test
    public void runInScope() throws Exception {
        final Container container = builder
                .register(Aaa.class)
                .build();

        final Aaa[] components = new Aaa[2];
        requestScope.runInScope(MockServletRequests.createMock(), () -> {
            components[0] = container.getComponent(Aaa.class);
        });
        requestScope.runInScope(MockServletRequests.createMock(), () -> {
            components[1] = container.getComponent(Aaa.class);
        });

        assertNotNull(components[0]);
        assertNotNull(components[1]);
        assertTrue(components[0] != components[1]);
    }

    @Test
    public void destroy() throws Exception {
        final Container container = builder
                .register(Bbb.class)
                .build();

        assertFalse(Bbb.called);

        final ServletRequest request = MockServletRequests.createMock();
        requestScope.runInScope(request, () -> {
            container.getComponent(Bbb.class);
        });

        final ServletRequestEvent sre = new ServletRequestEvent(MockServletContexts.createMock(), request);
        new ContainerLifecycleServletListener().requestDestroyed(sre);

        assertTrue(Bbb.called);
    }

    @RequestScoped
    static class Aaa {
    }

    @RequestScoped
    static class Bbb {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }
}
