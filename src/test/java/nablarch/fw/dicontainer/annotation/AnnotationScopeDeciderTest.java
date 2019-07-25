package nablarch.fw.dicontainer.annotation;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.Scope;
import nablarch.fw.dicontainer.scope.ScopeDecider;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class AnnotationScopeDeciderTest {

    private ScopeDecider decider;
    private Scope defaultScope;
    private ErrorCollector errorCollector;

    @Before
    public void setUp() throws Exception {
        defaultScope = (Scope) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { Scope.class }, (a, b, c) -> null);
        decider = AnnotationScopeDecider.builder().defaultScope(defaultScope).build();
    }

    @Test
    public void singletonFromComponentClass() throws Exception {
        final Scope scope = decider.fromComponentClass(Aaa.class, errorCollector).get();
        assertEquals(SingletonScope.class, scope.getClass());
    }

    @Test
    public void prototypeFromComponentClass() throws Exception {
        final Scope scope = decider.fromComponentClass(Bbb.class, errorCollector).get();
        assertEquals(PrototypeScope.class, scope.getClass());
    }

    @Test
    public void defaultScopeFromComponentClass() throws Exception {
        final Scope scope = decider.fromComponentClass(Ccc.class, errorCollector).get();
        assertSame(defaultScope, scope);
    }

    @Singleton
    static class Aaa {
    }

    @Prototype
    static class Bbb {
    }

    static class Ccc {
    }
}
