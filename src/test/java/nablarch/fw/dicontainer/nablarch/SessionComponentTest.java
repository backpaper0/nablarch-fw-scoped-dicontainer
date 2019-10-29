package nablarch.fw.dicontainer.nablarch;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nablarch.common.web.session.SessionEntry;
import nablarch.common.web.session.SessionManager;
import nablarch.common.web.session.SessionStore;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.ExecutionContext;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.web.SessionScoped;
import nablarch.fw.dicontainer.web.exception.WebContextException;
import nablarch.fw.dicontainer.web.scope.SessionScope;

public class SessionComponentTest {

    private SessionScope sessionScope;
    private AnnotationContainerBuilder builder;
    private NablarchWebContextHandler supplier;

    @Before
    public void setUp() throws Exception {
        final SessionManager sessionManager = new SessionManager();
        sessionManager.setDefaultStoreName("test");
        final SessionStore sessionStore = new TestSessionStore();
        final List<SessionStore> sessionStores = Collections.singletonList(sessionStore);
        sessionManager.setAvailableStores(sessionStores);
        SystemRepository.load(() -> Collections.singletonMap("sessionManager", sessionManager));
        supplier = new NablarchWebContextHandler();
        sessionScope = new SessionScope(supplier);
        final AnnotationScopeDecider decider = AnnotationScopeDecider.builder()
                .addScope(SessionScoped.class, sessionScope)
                .build();
        builder = AnnotationContainerBuilder.builder().scopeDecider(decider).build();
    }

    @After
    public void clearRepository() {
        Containers.clear();
    }

    @Test
    public void getComponent() throws Exception {
        final Container container = builder
                .register(Aaa.class)
                .build();

        final Aaa[] components = new Aaa[2];
        supplier.handle(null, new ExecutionContext()
                .addHandler((data, context) -> {
                    components[0] = container.getComponent(Aaa.class);
                    components[1] = container.getComponent(Aaa.class);
                    return null;
                }));

        assertNotNull(components[0]);
        assertNotNull(components[1]);
        assertTrue(components[0] == components[1]);
    }

    @Test
    public void getComponentQualifier() throws Exception {
        final Container container = builder
                .register(Ccc2.class)
                .register(Ccc3.class)
                .build();

        final Ccc1[] components = new Ccc1[2];
        supplier.handle(null, new ExecutionContext()
                .addHandler((data, context) -> {
                    components[0] = container.getComponent(Ccc1.class, new NamedImpl("foo"));
                    components[1] = container.getComponent(Ccc1.class, new NamedImpl("bar"));
                    return null;
                }));

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
    private static class Aaa implements Serializable {
    }

    private static class Ccc1 {
    }

    @SessionScoped
    @Named("foo")
    private static class Ccc2 extends Ccc1 {
    }

    @SessionScoped
    @Named("bar")
    private static class Ccc3 extends Ccc1 {
    }

    private static class TestSessionStore extends SessionStore {

        protected TestSessionStore() {
            super("test");
        }

        @Override
        public List<SessionEntry> load(final String sessionId,
                final ExecutionContext executionContext) {
            return null;
        }

        @Override
        public void save(final String sessionId, final List<SessionEntry> entries,
                final ExecutionContext executionContext) {
        }

        @Override
        public void delete(final String sessionId, final ExecutionContext executionContext) {
        }

        @Override
        public void invalidate(final String sessionId, final ExecutionContext executionContext) {
        }
    }
}
