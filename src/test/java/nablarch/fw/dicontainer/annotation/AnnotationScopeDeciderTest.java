package nablarch.fw.dicontainer.annotation;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.Scope;
import nablarch.fw.dicontainer.config.PrototypeScope;
import nablarch.fw.dicontainer.config.SingletonScope;

public class AnnotationScopeDeciderTest {

    private AnnotationScopeDecider decider;
    private Scope defaultScope;

    @Before
    public void setUp() throws Exception {
        defaultScope = (Scope) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { Scope.class }, (a, b, c) -> null);
        decider = AnnotationScopeDecider.builder().defaultScope(defaultScope).build();
    }

    @Test
    public void singleton() throws Exception {
        final Scope scope = decider.decide(Aaa.class);
        assertEquals(SingletonScope.class, scope.getClass());
    }

    @Test
    public void prototype() throws Exception {
        final Scope scope = decider.decide(Bbb.class);
        assertEquals(PrototypeScope.class, scope.getClass());
    }

    @Test
    public void defaultScope() throws Exception {
        final Scope scope = decider.decide(Ccc.class);
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
