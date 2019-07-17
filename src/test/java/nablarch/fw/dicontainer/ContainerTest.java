package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.exception.ComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.exception.CycleInjectionException;
import nablarch.fw.dicontainer.exception.InjectableConstructorDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectableConstructorNotFoundException;
import nablarch.fw.dicontainer.exception.InjectionComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectionComponentNotFoundException;
import nablarch.fw.dicontainer.exception.InvalidInjectionScopeException;
import nablarch.fw.dicontainer.exception.StaticInjectionException;

public class ContainerTest {

    @Test
    public void getComponent() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .build();

        final Aaa component = container.getComponent(Aaa.class);

        assertNotNull(component);
    }

    @Test
    public void singleton() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .build();

        final Aaa component1 = container.getComponent(Aaa.class);
        final Aaa component2 = container.getComponent(Aaa.class);

        assertTrue(component1 == component2);
    }

    @Test
    public void getComponentByInterface() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Bbb4.class)
                .build();

        final Bbb1 component = container.getComponent(Bbb1.class);

        assertTrue(component.getClass() == Bbb4.class);
    }

    @Test
    public void getComponentByQualifier() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ccc2.class)
                .register(Ccc3.class)
                .build();

        final Ccc1 component1 = container.getComponent(Ccc1.class, new NamedImpl("foo"));
        final Ccc1 component2 = container.getComponent(Ccc1.class, new NamedImpl("bar"));

        assertTrue(component1.getClass() == Ccc2.class);
        assertTrue(component2.getClass() == Ccc3.class);
    }

    @Test
    public void fieldInjection() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .register(Ddd1.class)
                .build();

        final Aaa component1 = container.getComponent(Aaa.class);
        final Ddd1 component2 = container.getComponent(Ddd1.class);

        assertNotNull(component2.injected);
        assertNull(component2.notInjected);
        assertTrue(component2.injected == component1);
    }

    @Test
    public void methodInjection() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .register(Ddd2.class)
                .build();

        final Aaa component1 = container.getComponent(Aaa.class);
        final Ddd2 component2 = container.getComponent(Ddd2.class);

        assertNotNull(component2.injected);
        assertNull(component2.notInjected);
        assertTrue(component2.injected == component1);
    }

    @Test
    public void constructorInjection() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Aaa.class)
                .register(Ddd3.class)
                .build();

        final Aaa component1 = container.getComponent(Aaa.class);
        final Ddd3 component2 = container.getComponent(Ddd3.class);

        assertNotNull(component2.injected);
        assertNull(component2.notInjected);
        assertTrue(component2.injected == component1);
    }

    @Test
    public void fieldInjectionQualifier() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ccc2.class)
                .register(Ccc3.class)
                .register(Eee1.class)
                .build();

        final Eee1 component = container.getComponent(Eee1.class);

        assertTrue(component.foo.getClass() == Ccc2.class);
        assertTrue(component.bar.getClass() == Ccc3.class);
    }

    @Test
    public void methodInjectionQualifier() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ccc2.class)
                .register(Ccc3.class)
                .register(Eee2.class)
                .build();

        final Eee2 component = container.getComponent(Eee2.class);

        assertTrue(component.foo.getClass() == Ccc2.class);
        assertTrue(component.bar.getClass() == Ccc3.class);
    }

    @Test
    public void constructorInjectionQualifier() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ccc2.class)
                .register(Ccc3.class)
                .register(Eee3.class)
                .build();

        final Eee3 component = container.getComponent(Eee3.class);

        assertTrue(component.foo.getClass() == Ccc2.class);
        assertTrue(component.bar.getClass() == Ccc3.class);
    }

    @Test
    public void methodInjectionOverride() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Fff2.class)
                .build();

        final Fff2 component = container.getComponent(Fff2.class);

        assertTrue(component.called1);
        assertFalse(component.called2);
        assertTrue(component.called3);
        assertTrue(component.called4);
        assertFalse(component.called5);
    }

    @Test
    public void fieldInjectionExtends() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Ggg2.class)
                .register(Aaa.class)
                .build();

        final Ggg2 component = container.getComponent(Ggg2.class);

        assertNotNull(component.field1);
        assertNotNull(component.field2);
        assertNull(component.field3);
        assertNotNull(component.field4);
        assertNull(component.field5);
        assertNotNull(component.getField1());
    }

    @Test
    public void constructorInjectionProvider() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Hhh1.class)
                .register(Aaa.class)
                .build();

        final Hhh1 component = container.getComponent(Hhh1.class);

        assertNotNull(component.component);
        assertNotNull(component.provider);
        assertTrue(component.component == component.provider.get());
    }

    @Test
    public void fieldInjectionProvider() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Hhh2.class)
                .register(Aaa.class)
                .build();

        final Hhh2 component = container.getComponent(Hhh2.class);

        assertNotNull(component.component);
        assertNotNull(component.provider);
        assertTrue(component.component == component.provider.get());
    }

    @Test
    public void methodInjectionProvider() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Hhh3.class)
                .register(Aaa.class)
                .build();

        final Hhh3 component = container.getComponent(Hhh3.class);

        assertNotNull(component.component);
        assertNotNull(component.provider);
        assertTrue(component.component == component.provider.get());
    }

    @Test
    public void injectionOrder() throws Exception {

        final Container container = new AnnotationContainerBuilder()
                .register(Iii2.class)
                .register(Aaa.class)
                .build();

        final Iii2 component = container.getComponent(Iii2.class);

        assertEquals(2, component.called.size());
        assertEquals("method1", component.called.get(0));
        assertEquals("method2", component.called.get(1));
    }

    @Test
    public void observes() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Jjj2.class)
                .build();

        assertFalse(Jjj2.called);

        container.fire(new Jjj1());

        assertTrue(Jjj2.called);
    }

    @Test
    public void destroySingleton() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Kkk1.class)
                .build();

        container.getComponent(Kkk1.class);

        assertFalse(Kkk1.called);

        container.destroy();

        assertTrue(Kkk1.called);
    }

    @Test
    public void destroyPrototype() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Kkk2.class)
                .build();

        container.getComponent(Kkk2.class);

        assertFalse(Kkk2.called);

        container.destroy();

        assertFalse(Kkk2.called);
    }

    @Test
    public void destroySingletonNotGetComponent() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Kkk3.class)
                .build();

        assertFalse(Kkk3.called);

        container.destroy();

        assertFalse(Kkk3.called);
    }

    @Test
    public void initSingleton() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Lll1.class)
                .build();

        assertFalse(Lll1.called);

        container.getComponent(Lll1.class);

        assertTrue(Lll1.called);
    }

    @Test
    public void initPrototype() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Lll2.class)
                .build();

        final Lll2 component = container.getComponent(Lll2.class);

        assertTrue(component.called);
    }

    @Test
    public void componentNotFound() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .build();

        try {
            container.getComponent(Aaa.class);
            fail();
        } catch (final ComponentNotFoundException e) {
        }
    }

    @Test
    public void fieldInjectionComponentNotFound() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final Container container = new AnnotationContainerBuilder()
                .register(Ooo1.class)
                .register(Ooo5.class)
                .build();

        assertNotNull(container.getComponent(Ooo5.class));
    }

    @Test
    public void methodValidInjectionScopeViaProvider() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Ooo1.class)
                .register(Ooo6.class)
                .build();

        assertNotNull(container.getComponent(Ooo6.class));
    }

    @Test
    public void constructorValidInjectionScopeViaProvider() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Ooo1.class)
                .register(Ooo7.class)
                .build();

        assertNotNull(container.getComponent(Ooo7.class));
    }

    @Test
    public void fieldCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
                .register(Ppp1.class)
                .register(Ppp2.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class, CycleInjectionException.class);
        }
    }

    @Test
    public void methodCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
                .register(Ppp3.class)
                .register(Ppp4.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class, CycleInjectionException.class);
        }
    }

    @Test
    public void constructorCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
                .register(Ppp5.class)
                .register(Ppp6.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class, CycleInjectionException.class);
        }
    }

    @Test
    public void fieldCycleInjectionViaProvider() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Ppp7.class)
                .register(Ppp8.class)
                .build();

        assertNotNull(container.getComponent(Ppp7.class));
        assertNotNull(container.getComponent(Ppp8.class));
    }

    @Test
    public void methodCycleInjectionViaProvider() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Ppp9.class)
                .register(Ppp10.class)
                .build();

        assertNotNull(container.getComponent(Ppp9.class));
        assertNotNull(container.getComponent(Ppp10.class));
    }

    @Test
    public void constructorCycleInjectionViaProvider() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                .register(Ppp11.class)
                .register(Ppp12.class)
                .build();

        assertNotNull(container.getComponent(Ppp11.class));
        assertNotNull(container.getComponent(Ppp12.class));
    }

    @Test
    public void manyCycleInjection() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
                .register(Ppp13.class)
                .register(Ppp14.class)
                .register(Ppp15.class);
        try {
            builder.build();
            fail();
        } catch (final ContainerCreationException e) {
            assertContainerException(e, CycleInjectionException.class, CycleInjectionException.class, CycleInjectionException.class);
        }
    }

    @Test
    public void fieldInjectionComponentDuplicated() throws Exception {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder()
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
        final Container container = new AnnotationContainerBuilder()
                .register(Qqq2.class)
                .register(Qqq3.class)
                .build();
        try {
            container.getComponent(Qqq1.class);
            fail();
        } catch (final ComponentDuplicatedException e) {
        }
    }

    @SafeVarargs
    private static void assertContainerException(final ContainerCreationException e,
            final Class<? extends ContainerException>... expecteds) {
        final Iterator<ContainerException> it = e.getExceptions().iterator();
        for (final Class<? extends ContainerException> expected : expecteds) {
            assertTrue(it.hasNext());
            assertEquals(expected, it.next().getClass());
        }
        assertFalse(it.hasNext());
    }

    @Singleton
    static class Aaa {
    }

    interface Bbb1 {
    }

    interface Bbb2 extends Bbb1 {
    }

    static class Bbb3 implements Bbb2 {
    }

    static class Bbb4 extends Bbb3 {
    }

    interface Ccc1 {
    }

    @Singleton
    @Named("foo")
    static class Ccc2 implements Ccc1 {
    }

    @Singleton
    @Named("bar")
    static class Ccc3 implements Ccc1 {
    }

    @Singleton
    static class Ddd1 {
        @Inject
        Aaa injected;
        Aaa notInjected;
    }

    @Singleton
    static class Ddd2 {

        Aaa injected;
        Aaa notInjected;

        @Inject
        void setInjected(final Aaa injected) {
            this.injected = injected;
        }
    }

    @Singleton
    static class Ddd3 {

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

    @Singleton
    static class Eee1 {
        @Inject
        @Named("foo")
        Ccc1 foo;
        @Inject
        @Named("bar")
        Ccc1 bar;
    }

    @Singleton
    static class Eee2 {

        Ccc1 foo;
        Ccc1 bar;

        @Inject
        void setFoo(@Named("foo") final Ccc1 foo, @Named("bar") final Ccc1 bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    @Singleton
    static class Eee3 {

        Ccc1 foo;
        Ccc1 bar;

        @Inject
        Eee3(@Named("foo") final Ccc1 foo, @Named("bar") final Ccc1 bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    static class Fff1 {

        boolean called1;
        boolean called2;
        boolean called3;

        @Inject
        public void method1() {
            called1 = true;
        }

        @Inject
        public void method2() {
            called2 = true;
        }

        public void method3() {
            called3 = true;
        }
    }

    static class Fff2 extends Fff1 {

        boolean called4;
        boolean called5;

        @Override
        public void method2() {
            super.method2();
        }

        @Inject
        @Override
        public void method3() {
            super.method3();
        }

        @Inject
        public void method4() {
            called4 = true;
        }

        public void method5() {
            called5 = true;
        }
    }

    static class Ggg1 {
        @Inject
        Aaa field1;
        @Inject
        Aaa field2;
        Aaa field3;

        public Aaa getField1() {
            return field1;
        }
    }

    static class Ggg2 extends Ggg1 {
        @Inject
        Aaa field1;
        @Inject
        Aaa field4;
        Aaa field5;
    }

    static class Hhh1 {

        Aaa component;
        Provider<Aaa> provider;

        @Inject
        public Hhh1(final Aaa component, final Provider<Aaa> provider) {
            this.component = component;
            this.provider = provider;
        }
    }

    static class Hhh2 {

        @Inject
        Aaa component;
        @Inject
        Provider<Aaa> provider;
    }

    static class Hhh3 {

        Aaa component;
        Provider<Aaa> provider;

        @Inject
        public void method(final Aaa component, final Provider<Aaa> provider) {
            this.component = component;
            this.provider = provider;
        }
    }

    static class Iii1 {

        List<String> called = new ArrayList<>();

        @Inject
        void method1() {
            called.add("method1");
        }
    }

    static class Iii2 extends Iii1 {

        @Inject
        void method2() {
            called.add("method2");
        }
    }

    static class Jjj1 {
    }

    static class Jjj2 {

        static boolean called;

        @Observes
        void handle(final Jjj1 event) {
            called = true;
        }
    }

    @Singleton
    static class Kkk1 {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    static class Kkk2 {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    @Singleton
    static class Kkk3 {

        static boolean called;

        @Destroy
        void method() {
            called = true;
        }
    }

    @Singleton
    static class Lll1 {

        static boolean called;

        @Init
        void method() {
            called = true;
        }
    }

    static class Lll2 {

        boolean called;

        @Init
        void method() {
            called = true;
        }
    }

    static class Mmm1 {
        Mmm1(final Object obj) {
        }
    }

    static class Mmm2 {
        @Inject
        Mmm2(final Aaa arg1) {
        }

        @Inject
        Mmm2(final Aaa arg1, final Aaa arg2) {
        }
    }

    static class Nnn1 {
        @Inject
        static Aaa field;
    }

    static class Nnn2 {
        @Inject
        static void method(final Aaa aaa) {
        }
    }

    @Prototype
    static class Ooo1 {
    }

    @Singleton
    static class Ooo2 {
        @Inject
        Ooo1 field;
    }

    @Singleton
    static class Ooo3 {
        @Inject
        void method(final Ooo1 arg) {
        }
    }

    @Singleton
    static class Ooo4 {
        @Inject
        Ooo4(final Ooo1 arg) {
        }
    }

    @Singleton
    static class Ooo5 {
        @Inject
        Provider<Ooo1> field;
    }

    @Singleton
    static class Ooo6 {
        @Inject
        void method(final Provider<Ooo1> arg) {
        }
    }

    @Singleton
    static class Ooo7 {
        @Inject
        Ooo7(final Provider<Ooo1> arg) {
        }
    }

    static class Ppp1 {
        @Inject
        Ppp2 field;
    }

    static class Ppp2 {
        @Inject
        Ppp1 field;
    }

    static class Ppp3 {
        @Inject
        void method(final Ppp4 arg) {
        }
    }

    static class Ppp4 {
        @Inject
        void method(final Ppp3 arg) {
        }
    }

    static class Ppp5 {
        @Inject
        Ppp5(final Ppp6 arg) {
        }
    }

    static class Ppp6 {
        @Inject
        Ppp6(final Ppp5 arg) {
        }
    }

    static class Ppp7 {
        @Inject
        Provider<Ppp8> field;
    }

    static class Ppp8 {
        @Inject
        Provider<Ppp7> field;
    }

    static class Ppp9 {
        @Inject
        void method(final Provider<Ppp10> arg) {
        }
    }

    static class Ppp10 {
        @Inject
        void method(final Provider<Ppp9> arg) {
        }
    }

    static class Ppp11 {
        @Inject
        Ppp11(final Provider<Ppp12> arg) {
        }
    }

    static class Ppp12 {
        @Inject
        Ppp12(final Provider<Ppp11> arg) {
        }
    }

    static class Ppp13 {
        @Inject
        Ppp14 field;
    }

    static class Ppp14 {
        @Inject
        Ppp15 field;
    }

    static class Ppp15 {
        @Inject
        Ppp13 field;
    }

    static class Qqq1 {
    }

    static class Qqq2 extends Qqq1 {
    }

    static class Qqq3 extends Qqq2 {
    }

    static class Qqq4 {
        @Inject
        Qqq1 field;
    }

    static class Qqq5 {
        @Inject
        void method(final Qqq1 arg) {
        }
    }

    static class Qqq6 {
        @Inject
        Qqq6(final Qqq1 arg) {
        }
    }
}
