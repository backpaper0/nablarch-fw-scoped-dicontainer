package nablarch.fw.dicontainer;

import static nablarch.fw.dicontainer.ContainerAsserts.*;
import static org.junit.Assert.*;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.event.EventTrigger;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ObserverMethodSignatureException;

public class ObservesTest {
    @Test
    public void observes() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Observer.class)
                .build();
        final EventTrigger trigger = container.getComponent(EventTrigger.class);

        assertFalse(Observer.called);

        trigger.fire(new EventObject());

        assertTrue(Observer.called);
    }

    @Test
    public void observesMethodMustNotBeNoArgs() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(InvalidObserver1.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ObserverMethodSignatureException.class);
        }
    }

    @Test
    public void observesMethodMustNotBeMultiArgs() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(InvalidObserver2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ObserverMethodSignatureException.class);
        }
    }

    @Test
    public void observesMethodMustBeInstanceMethod() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(InvalidObserver3.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ObserverMethodSignatureException.class);
        }
    }

    private static class EventObject {
    }

    private static class Observer {

        static boolean called;

        @Observes
        void handle(final EventObject event) {
            called = true;
        }
    }

    private static class InvalidObserver1 {

        @Observes
        void handle() {
        }
    }

    private static class InvalidObserver2 {

        @Observes
        void handle(final EventObject arg1, final EventObject arg2) {
        }
    }

    private static class InvalidObserver3 {

        @Observes
        static void handle(final EventObject event) {
        }
    }

}
