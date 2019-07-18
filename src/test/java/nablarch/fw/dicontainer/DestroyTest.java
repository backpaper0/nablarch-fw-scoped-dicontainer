package nablarch.fw.dicontainer;

import static nablarch.fw.dicontainer.ContainerAsserts.*;
import static org.junit.Assert.*;

import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodDuplicatedException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodSignatureException;

public class DestroyTest {

    @Test
    public void destroySingleton() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        container.getComponent(Aaa.class);

        assertFalse(Aaa.called);

        container.destroy();

        assertTrue(Aaa.called);
    }

    @Test
    public void destroyPrototype() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Bbb.class)
                .build();

        container.getComponent(Bbb.class);

        assertFalse(Bbb.called);

        container.destroy();

        assertFalse(Bbb.called);
    }

    @Test
    public void destroySingletonNotGetComponent() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ccc.class)
                .build();

        assertFalse(Ccc.called);

        container.destroy();

        assertFalse(Ccc.called);
    }

    @Test
    public void destroyMethodMustBeNoArgs() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ddd.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodSignatureException.class);
        }
    }

    @Test
    public void destroyMethodMustBeOnePerComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Eee.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, LifeCycleMethodDuplicatedException.class);
        }
    }

    @Test
    public void destroyMethodMustBeInstanceMethod() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Fff.class);
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

        @Destroy
        void method() {
            called = true;
        }
    }

    private static class Bbb {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    @Singleton
    private static class Ccc {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    private static class Ddd {

        @Destroy
        void method(final Object arg) {
        }
    }

    private static class Eee {

        @Destroy
        void method1() {
        }

        @Destroy
        void method2() {
        }
    }

    private static class Fff {

        @Destroy
        static void method1() {
        }
    }
}
