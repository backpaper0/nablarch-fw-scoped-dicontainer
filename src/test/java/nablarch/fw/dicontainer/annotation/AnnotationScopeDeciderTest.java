package nablarch.fw.dicontainer.annotation;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.exception.ErrorCollector;
import nablarch.fw.dicontainer.scope.PrototypeScope;
import nablarch.fw.dicontainer.scope.Scope;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class AnnotationScopeDeciderTest {

    private AnnotationScopeDecider decider;
    private Scope defaultScope;
    private ErrorCollector errorCollector;

    @Before
    public void setUp() throws Exception {
        defaultScope = (Scope) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { Scope.class }, (a, b, c) -> null);
        decider = AnnotationScopeDecider.builder().defaultScope(defaultScope).build();
    }

    @Test
    public void singletonFromClass() throws Exception {
        final Scope scope = decider.fromClass(Aaa.class, errorCollector).get();
        assertEquals(SingletonScope.class, scope.getClass());
    }

    @Test
    public void prototypeFromClass() throws Exception {
        final Scope scope = decider.fromClass(Bbb.class, errorCollector).get();
        assertEquals(PrototypeScope.class, scope.getClass());
    }

    @Test
    public void defaultScopeFromClass() throws Exception {
        final Scope scope = decider.fromClass(Ccc.class, errorCollector).get();
        assertSame(defaultScope, scope);
    }

    @Test
    public void singletonFromMethod() throws Exception {
        final Scope scope = decider
                .fromMethod(Ddd.class.getDeclaredMethod("singleton"), errorCollector).get();
        assertEquals(SingletonScope.class, scope.getClass());
    }

    @Test
    public void prototypeFromMethod() throws Exception {
        final Scope scope = decider
                .fromMethod(Ddd.class.getDeclaredMethod("prototype"), errorCollector).get();
        assertEquals(PrototypeScope.class, scope.getClass());
    }

    @Test
    public void defaultScopeFromMethod() throws Exception {
        final Scope scope = decider
                .fromMethod(Ddd.class.getDeclaredMethod("defaultScope"), errorCollector).get();
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

    static class Ddd {
        @Singleton
        Object singleton() {
            return null;
        }

        @Prototype
        Object prototype() {
            return null;
        }

        Object defaultScope() {
            return null;
        }
    }
}
