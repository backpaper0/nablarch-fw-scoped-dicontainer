package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

public class FactoryTest {

    @Test
    public void getComponent() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .build();

        final Bbb component = container.getComponent(Bbb.class);

        assertNotNull(component);
    }

    @Test
    public void qualifier() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ccc.class)
                .build();

        final Ccc component1 = container.getComponent(Ccc.class);
        final Bbb component2 = container.getComponent(Bbb.class, new NamedImpl("foo"));
        final Bbb component3 = container.getComponent(Bbb.class, new NamedImpl("bar"));

        assertNotNull(component2);
        assertNotNull(component3);
        assertSame(component1.foo, component2);
        assertSame(component1.bar, component3);
    }

    @Singleton
    private static class Aaa {

        @Factory
        Bbb bbb() {
            return new Bbb();
        }
    }

    private static class Bbb {
    }

    @Singleton
    private static class Ccc {

        Bbb foo = new Bbb();
        Bbb bar = new Bbb();

        @Factory
        @Named("foo")
        Bbb foo() {
            return foo;
        }

        @Factory
        @Named("bar")
        Bbb bar() {
            return bar;
        }
    }
}
