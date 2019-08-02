package nablarch.fw.dicontainer.nablarch;

import static org.junit.Assert.*;

import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nablarch.fw.ExecutionContext;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.nablarch.ContainerImplementers;
import nablarch.fw.dicontainer.nablarch.NablarchWebContextHandler;
import nablarch.fw.dicontainer.web.RequestScoped;
import nablarch.fw.dicontainer.web.exception.WebContextException;
import nablarch.fw.dicontainer.web.scope.RequestScope;

public class RequestComponentTest {

    private RequestScope requestScope;
    private AnnotationContainerBuilder builder;
    private NablarchWebContextHandler supplier;

    @Before
    public void setUp() throws Exception {
        supplier = new NablarchWebContextHandler();
        requestScope = new RequestScope(supplier);
        final AnnotationScopeDecider decider = AnnotationScopeDecider.builder()
                .addScope(RequestScoped.class, requestScope)
                .build();
        builder = AnnotationContainerBuilder.builder().scopeDecider(decider).build();
    }

    @After
    public void tearDown() throws Exception {
        ContainerImplementers.clear();
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
    public void runInScope() throws Exception {
        final Container container = builder
                .register(Aaa.class)
                .build();

        final Aaa[] components = new Aaa[2];
        supplier.handle(null, new ExecutionContext()
                .addHandler((data, context) -> {
                    components[0] = container.getComponent(Aaa.class);
                    return null;
                }));
        supplier.handle(null, new ExecutionContext()
                .addHandler((data, context) -> {
                    components[1] = container.getComponent(Aaa.class);
                    return null;
                }));

        assertNotNull(components[0]);
        assertNotNull(components[1]);
        assertTrue(components[0] != components[1]);
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

    static class Ccc1 {
    }

    @RequestScoped
    @Named("foo")
    static class Ccc2 extends Ccc1 {
    }

    @RequestScoped
    @Named("bar")
    static class Ccc3 extends Ccc1 {
    }
}
