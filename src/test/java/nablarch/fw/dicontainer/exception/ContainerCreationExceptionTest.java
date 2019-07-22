package nablarch.fw.dicontainer.exception;

import static nablarch.fw.dicontainer.ContainerAsserts.*;
import static org.junit.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Scope;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Prototype;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

public class ContainerCreationExceptionTest {

    @Test
    public void fieldInjectionComponentNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ddd1.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentNotFoundException.class);
        }
    }

    @Test
    public void methodInjectionComponentNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ddd2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentNotFoundException.class);
        }
    }

    @Test
    public void constructorInjectionComponentNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ddd3.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentNotFoundException.class);
        }
    }

    @Test
    public void injectableConstructorNotFound() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Mmm1.class)
                .register(Aaa.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectableConstructorNotFoundException.class);
        }
    }

    @Test
    public void injectableConstructorDuplicatedException() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Mmm2.class)
                .register(Aaa.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectableConstructorDuplicatedException.class);
        }
    }

    @Test
    public void fieldStaticInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Nnn1.class)
                .register(Aaa.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, StaticInjectionException.class);
        }
    }

    @Test
    public void methodStaticInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Nnn2.class)
                .register(Aaa.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, StaticInjectionException.class);
        }
    }

    @Test
    public void fieldInvalidInjectionScope() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidInjectionScopeException.class);
        }
    }

    @Test
    public void methodInvalidInjectionScope() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo3.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidInjectionScopeException.class);
        }
    }

    @Test
    public void constructorInvalidInjectionScope() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo4.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidInjectionScopeException.class);
        }
    }

    @Test
    public void fieldValidInjectionScopeViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo5.class)
                .build();

        assertNotNull(container.getComponent(Ooo5.class));
    }

    @Test
    public void methodValidInjectionScopeViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo6.class)
                .build();

        assertNotNull(container.getComponent(Ooo6.class));
    }

    @Test
    public void constructorValidInjectionScopeViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ooo1.class)
                .register(Ooo7.class)
                .build();

        assertNotNull(container.getComponent(Ooo7.class));
    }

    @Test
    public void fieldCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ppp1.class)
                .register(Ppp2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class,
                    CycleInjectionException.class);
        }
    }

    @Test
    public void methodCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ppp3.class)
                .register(Ppp4.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class,
                    CycleInjectionException.class);
        }
    }

    @Test
    public void constructorCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ppp5.class)
                .register(Ppp6.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class,
                    CycleInjectionException.class);
        }
    }

    @Test
    public void fieldCycleInjectionViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ppp7.class)
                .register(Ppp8.class)
                .build();

        assertNotNull(container.getComponent(Ppp7.class));
        assertNotNull(container.getComponent(Ppp8.class));
    }

    @Test
    public void methodCycleInjectionViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ppp9.class)
                .register(Ppp10.class)
                .build();

        assertNotNull(container.getComponent(Ppp9.class));
        assertNotNull(container.getComponent(Ppp10.class));
    }

    @Test
    public void constructorCycleInjectionViaProvider() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Ppp11.class)
                .register(Ppp12.class)
                .build();

        assertNotNull(container.getComponent(Ppp11.class));
        assertNotNull(container.getComponent(Ppp12.class));
    }

    @Test
    public void manyCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Ppp13.class)
                .register(Ppp14.class)
                .register(Ppp15.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class,
                    CycleInjectionException.class, CycleInjectionException.class);
        }
    }

    @Test
    public void fieldInjectionComponentDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Qqq2.class)
                .register(Qqq3.class)
                .register(Qqq4.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentDuplicatedException.class);
        }
    }

    @Test
    public void methodInjectionComponentDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Qqq2.class)
                .register(Qqq3.class)
                .register(Qqq5.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentDuplicatedException.class);
        }
    }

    @Test
    public void constructorInjectionComponentDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Qqq2.class)
                .register(Qqq3.class)
                .register(Qqq6.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InjectionComponentDuplicatedException.class);
        }
    }

    @Test
    public void componentDuplicated() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Qqq2.class)
                .register(Qqq3.class)
                .build();
        try {
            container.getComponent(Qqq1.class);
            fail();
        } catch (final ComponentDuplicatedException e) {
        }
    }

    @Test
    public void scopeDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Rrr1.class);
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
                .register(Rrr2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, ScopeNotFoundException.class);
        }
    }

    @Test
    public void interfaceIsNotComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Sss1.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidComponentException.class);
        }
    }

    @Test
    public void abstractClassIsNotComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Sss2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidComponentException.class);
        }
    }

    @Test
    public void anonymousClassIsNotComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(new Sss3() {
                }.getClass());
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidComponentException.class);
        }
    }

    @Test
    public void enumIsNotComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Sss4.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidComponentException.class);
        }
    }

    @Test
    public void annotationIsNotComponent() throws Exception {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.createDefault()
                .register(Sss5.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, InvalidComponentException.class);
        }
    }

    @Singleton
    private static class Aaa {
    }

    @Singleton
    private static class Ddd1 {
        @Inject
        Aaa injected;
        Aaa notInjected;
    }

    @Singleton
    private static class Ddd2 {

        Aaa injected;
        Aaa notInjected;

        @Inject
        void setInjected(final Aaa injected) {
            this.injected = injected;
        }
    }

    @Singleton
    private static class Ddd3 {

        Aaa injected;
        Aaa notInjected;

        @Inject
        Ddd3(final Aaa injected) {
            this.injected = injected;
        }

        Ddd3(final Aaa injected, final Aaa notInjected) {
            throw new AssertionError();
        }
    }

    private static class Mmm1 {
        Mmm1(final Object obj) {
        }
    }

    private static class Mmm2 {
        @Inject
        Mmm2(final Aaa arg1) {
        }

        @Inject
        Mmm2(final Aaa arg1, final Aaa arg2) {
        }
    }

    private static class Nnn1 {
        @Inject
        static Aaa field;
    }

    private static class Nnn2 {
        @Inject
        static void method(final Aaa aaa) {
        }
    }

    @Prototype
    private static class Ooo1 {
    }

    @Singleton
    private static class Ooo2 {
        @Inject
        Ooo1 field;
    }

    @Singleton
    private static class Ooo3 {
        @Inject
        void method(final Ooo1 arg) {
        }
    }

    @Singleton
    private static class Ooo4 {
        @Inject
        Ooo4(final Ooo1 arg) {
        }
    }

    @Singleton
    private static class Ooo5 {
        @Inject
        Provider<Ooo1> field;
    }

    @Singleton
    private static class Ooo6 {
        @Inject
        void method(final Provider<Ooo1> arg) {
        }
    }

    @Singleton
    private static class Ooo7 {
        @Inject
        Ooo7(final Provider<Ooo1> arg) {
        }
    }

    private static class Ppp1 {
        @Inject
        Ppp2 field;
    }

    private static class Ppp2 {
        @Inject
        Ppp1 field;
    }

    private static class Ppp3 {
        @Inject
        void method(final Ppp4 arg) {
        }
    }

    private static class Ppp4 {
        @Inject
        void method(final Ppp3 arg) {
        }
    }

    private static class Ppp5 {
        @Inject
        Ppp5(final Ppp6 arg) {
        }
    }

    private static class Ppp6 {
        @Inject
        Ppp6(final Ppp5 arg) {
        }
    }

    private static class Ppp7 {
        @Inject
        Provider<Ppp8> field;
    }

    private static class Ppp8 {
        @Inject
        Provider<Ppp7> field;
    }

    private static class Ppp9 {
        @Inject
        void method(final Provider<Ppp10> arg) {
        }
    }

    private static class Ppp10 {
        @Inject
        void method(final Provider<Ppp9> arg) {
        }
    }

    private static class Ppp11 {
        @Inject
        Ppp11(final Provider<Ppp12> arg) {
        }
    }

    private static class Ppp12 {
        @Inject
        Ppp12(final Provider<Ppp11> arg) {
        }
    }

    private static class Ppp13 {
        @Inject
        Ppp14 field;
    }

    private static class Ppp14 {
        @Inject
        Ppp15 field;
    }

    private static class Ppp15 {
        @Inject
        Ppp13 field;
    }

    private static class Qqq1 {
    }

    private static class Qqq2 extends Qqq1 {
    }

    private static class Qqq3 extends Qqq2 {
    }

    private static class Qqq4 {
        @Inject
        Qqq1 field;
    }

    private static class Qqq5 {
        @Inject
        void method(final Qqq1 arg) {
        }
    }

    private static class Qqq6 {
        @Inject
        Qqq6(final Qqq1 arg) {
        }
    }

    @Singleton
    @Prototype
    private static class Rrr1 {
    }

    @UnknownScoped
    private static class Rrr2 {
    }

    @Scope
    @Retention(RetentionPolicy.RUNTIME)
    private @interface UnknownScoped {
    }

    private interface Sss1 {
    }

    private abstract class Sss2 {
    }

    private static class Sss3 {
    }

    private enum Sss4 {
    }

    private @interface Sss5 {
    }
}
