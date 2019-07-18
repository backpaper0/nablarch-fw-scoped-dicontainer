package nablarch.fw.dicontainer;

import static nablarch.fw.dicontainer.ContainerAsserts.*;
import static org.junit.Assert.*;

import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodDuplicatedException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodSignatureException;

public class InitTest {

    @Test
    public void initSingleton() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        assertFalse(Aaa.called);

        container.getComponent(Aaa.class);

        assertTrue(Aaa.called);
    }

    @Test
    public void initPrototype() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Bbb.class)
                .build();

        final Bbb component = container.getComponent(Bbb.class);

        assertTrue(component.called);
    }

    @Test
    public void initMethodMustBeNoArgs() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ccc.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodSignatureException.class);
        }
    }

    @Test
    public void initMethodMustBeOnePerComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ddd.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodDuplicatedException.class);
        }
    }

    @Test
    public void initMethodMustBeInstanceMethod() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Eee.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodSignatureException.class);
        }
    }

    @Singleton
    private static class Aaa {

        static boolean called;

        @Init
        void method() {
            called = true;
        }
    }

    private static class Bbb {

        boolean called;

        @Init
        void method() {
            called = true;
        }
    }

    private static class Ccc {
        @Init
        void method(final Object arg) {
        }
    }

    private static class Ddd {
        @Init
        void method1() {
        }

        @Init
        void method2() {
        }
    }

    private static class Eee {
        @Init
        static void method() {
        }
    }

}
