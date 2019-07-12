package nablarch.fw.dicontainer.servlet;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.AnnotationScopeDecider;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.SessionContextFactory;
import nablarch.fw.dicontainer.SessionScope;
import nablarch.fw.dicontainer.SessionScoped;

public class HttpSessionContextFactoryTest {

    private SessionScope sessionScope;
    private AnnotationContainerBuilder builder;

    @Before
    public void setUp() throws Exception {
        final SessionContextFactory factory = new HttpSessionContextFactory();
        sessionScope = new SessionScope(factory);
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
        sessionScope.runInScope(MockServletRequests.createMock(), () -> {
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
        sessionScope.runInScope(request, () -> {
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
            sessionScope.runInScope(request, () -> {
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
            sessionScope.runInScope(request, () -> {
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

    @SessionScoped
    static class Aaa {
    }

    @SessionScoped
    static class Bbb {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    @SessionScoped
    static class Ccc {
        public Ccc() {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
