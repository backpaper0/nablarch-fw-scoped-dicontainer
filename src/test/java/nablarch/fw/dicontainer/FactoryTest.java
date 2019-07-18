package nablarch.fw.dicontainer;

import static nablarch.fw.dicontainer.ContainerAsserts.*;
import static org.junit.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Named;
import javax.inject.Scope;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.FactoryMethodSignatureException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodNotFoundException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodSignatureException;
import nablarch.fw.dicontainer.exception.ScopeDuplicatedException;
import nablarch.fw.dicontainer.exception.ScopeNotFoundException;

public class FactoryTest {

    @Test
    public void getComponent() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        final Bbb component = container.getComponent(Bbb.class);

        assertNotNull(component);
    }

    @Test
    public void qualifier() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
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

    @Test
    public void factoryMethodMustReturnValue() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Eee.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, FactoryMethodSignatureException.class);
        }
    }

    @Test
    public void factoryMethodMustBeNoArg() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Fff.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, FactoryMethodSignatureException.class);
        }
    }

    @Test
    public void factoryMethodMustBeInstanceMethod() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ggg.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, FactoryMethodSignatureException.class);
        }
    }

    @Test
    public void scopeDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Hhh.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ScopeDuplicatedException.class);
        }
    }

    @Test
    public void scopeNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Iii.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ScopeNotFoundException.class);
        }
    }

    @Test
    public void scope() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Jjj1.class)
                .build();

        final Jjj2 component1 = container.getComponent(Jjj2.class);
        final Jjj2 component2 = container.getComponent(Jjj2.class);
        final Jjj3 component3 = container.getComponent(Jjj3.class);
        final Jjj3 component4 = container.getComponent(Jjj3.class);

        assertTrue(component1 == component2);
        assertFalse(component3 == component4);
    }

    @Test
    public void destroyMethodMustBeNoArgs() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Kkk1.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodSignatureException.class);
        }
    }

    @Test
    public void destroyMethodNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Kkk3.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodNotFoundException.class);
        }
    }

    @Test
    public void destroyMethodMustBeInstanceMethod() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Kkk5.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodSignatureException.class);
        }
    }

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

    private static class Eee {
        @Factory
        void method() {
        }
    }

    private static class Fff {
        @Factory
        Bbb method(final Object arg) {
            return null;
        }
    }

    private static class Ggg {
        @Factory
        static Bbb method() {
            return null;
        }
    }

    private static class Hhh {
        @Factory
        @Singleton
        @Prototype
        Bbb method() {
            return null;
        }
    }

    private static class Iii {
        @Factory
        @UnknownScoped
        Bbb method() {
            return null;
        }
    }

    @Scope
    @Retention(RetentionPolicy.RUNTIME)
    private @interface UnknownScoped {
    }

    private static class Jjj1 {

        @Factory
        @Singleton
        Jjj2 singleton() {
            return new Jjj2();
        }

        @Factory
        @Prototype
        Jjj3 prototype() {
            return new Jjj3();
        }
    }

    private static class Jjj2 {
    }

    private static class Jjj3 {
    }

    private static class Kkk1 {
        @Factory(destroy = "destroy")
        Kkk2 create() {
            return new Kkk2();
        }
    }

    private static class Kkk2 {
        void destroy(final Object arg) {
        }
    }

    private static class Kkk3 {
        @Factory(destroy = "destroy")
        Kkk4 create() {
            return new Kkk4();
        }
    }

    private static class Kkk4 {
    }

    private static class Kkk5 {
        @Factory(destroy = "destroy")
        Kkk6 create() {
            return new Kkk6();
        }
    }

    private static class Kkk6 {
        static void destroy() {
        }
    }
}
