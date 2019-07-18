package nablarch.fw.dicontainer.web.servlet;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.exception.web.WebContextException;
import nablarch.fw.dicontainer.web.SessionScope;
import nablarch.fw.dicontainer.web.SessionScoped;

public class SessionComponentTest {

    private SessionScope sessionScope;
    private AnnotationContainerBuilder builder;
    private ServletAPIContextSupplier supplier;

    @Before
    public void setUp() throws Exception {
        supplier = new ServletAPIContextSupplier();
        sessionScope = new SessionScope(supplier);
        final AnnotationScopeDecider decider = AnnotationScopeDecider.builder()
                .addScope(SessionScoped.class, sessionScope)
                .build();
        builder = new AnnotationContainerBuilder(decider);
    }

    @Test
    public void getComponent() throws Exception {
        final Container container = builder
                .register(Aaa.class)
                .build();

        final Aaa[] components = new Aaa[2];
        supplier.doWithContext(MockServletRequests.createMock(), () -> {
            components[0] = container.getComponent(Aaa.class);
            components[1] = container.getComponent(Aaa.class);
        });

        assertNotNull(components[0]);
        assertNotNull(components[1]);
        assertTrue(components[0] == components[1]);
    }

    @Test
    public void destroy() throws Exception {
        final Container container = builder
                .register(Bbb.class)
                .build();

        assertFalse(Bbb.called);

        final HttpServletRequest request = MockServletRequests.createMock();
        supplier.doWithContext(request, () -> {
            container.getComponent(Bbb.class);
        });

        final HttpSessionEvent se = new HttpSessionEvent(request.getSession());
        new ContainerLifecycleServletListener().sessionDestroyed(se);

        assertTrue(Bbb.called);
    }

    @Test
    public void multiThread() throws Exception {
        final Container container = builder
                .register(Ccc.class)
                .build();

        final HttpSession session = MockHttpSessions.createMock();
        final HttpServletRequest request = MockServletRequests.createMock(session);

        final CountDownLatch ready = new CountDownLatch(2);
        final CountDownLatch go = new CountDownLatch(1);

        final Ccc[] components = new Ccc[2];

        final Thread t1 = new Thread(() -> {
            supplier.doWithContext(request, () -> {
                ready.countDown();
                try {
                    go.await();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
                components[0] = container.getComponent(Ccc.class);
            });
        });

        final Thread t2 = new Thread(() -> {
            supplier.doWithContext(request, () -> {
                ready.countDown();
                try {
                    go.await();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
                components[1] = container.getComponent(Ccc.class);
            });
        });

        t1.start();
        t2.start();

        ready.await();
        go.countDown();

        t1.join();
        t2.join();

        assertTrue(components[0] == components[1]);
    }

    @Test
    public void serialize() throws Exception {
        final Container container = builder
                .register(Ddd.class)
                .build();

        final UUID[] ids = new UUID[2];

        final HttpSession session = MockHttpSessions.createMock();
        supplier.doWithContext(MockServletRequests.createMock(session), () -> {
            final Ddd component = container.getComponent(Ddd.class);
            ids[0] = component.id;
        });

        final byte[] serialized = MockHttpSessions.serialize(session);

        final HttpSession deserialized = MockHttpSessions.createMock(serialized);
        supplier.doWithContext(MockServletRequests.createMock(deserialized), () -> {
            final Ddd component = container.getComponent(Ddd.class);
            ids[1] = component.id;
        });

        assertTrue(ids[0].equals(ids[1]));
        assertTrue(ids[0] != ids[1]);
    }

    @Test
    public void getComponentQualifier() throws Exception {
        final Container container = builder
                .register(Ccc2.class)
                .register(Ccc3.class)
                .build();

        final Ccc1[] components = new Ccc1[2];
        supplier.doWithContext(MockServletRequests.createMock(), () -> {
            components[0] = container.getComponent(Ccc1.class, new NamedImpl("foo"));
            components[1] = container.getComponent(Ccc1.class, new NamedImpl("bar"));
        });

        assertTrue(components[0].getClass() == Ccc2.class);
        assertTrue(components[1].getClass() == Ccc3.class);
    }

    @Test
    public void outOfScope() throws Exception {
        final Container container = builder.register(Aaa.class)
                .build();
        try {
            container.getComponent(Aaa.class);
            fail();
        } catch (final WebContextException e) {
        }
    }

    @SessionScoped
    static class Aaa implements Serializable {
    }

    @SessionScoped
    static class Bbb implements Serializable {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    @SessionScoped
    static class Ccc implements Serializable {
        public Ccc() {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SessionScoped
    static class Ddd implements Serializable {
        UUID id = UUID.randomUUID();
    }

    static class Ccc1 {
    }

    @SessionScoped
    @Named("foo")
    static class Ccc2 extends Ccc1 {
    }

    @SessionScoped
    @Named("bar")
    static class Ccc3 extends Ccc1 {
    }
}
