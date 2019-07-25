package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

public class ContainerTest {

    @Test
    public void getComponent() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        final Aaa component = container.getComponent(Aaa.class);

        assertNotNull(component);
    }

    @Test
    public void singleton() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        final Aaa component1 = container.getComponent(Aaa.class);
        final Aaa component2 = container.getComponent(Aaa.class);

        assertTrue(component1 == component2);
    }

    @Test
    public void getComponentByInterface() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Bbb4.class)
                .build();

        final Bbb1 component = container.getComponent(Bbb1.class);

        assertTrue(component.getClass() == Bbb4.class);
    }

    @Test
    public void fieldInjection() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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
    public void methodInjectionOverride() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
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

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Iii2.class)
                .register(Aaa.class)
                .build();

        final Iii2 component = container.getComponent(Iii2.class);

        assertEquals(2, component.called.size());
        assertEquals("method1", component.called.get(0));
        assertEquals("method2", component.called.get(1));
    }

    @Test
    public void componentNotFound() throws Exception {
        final Container container = AnnotationContainerBuilder.createDefault()
                .build();

        try {
            container.getComponent(Aaa.class);
            fail();
        } catch (final ComponentNotFoundException e) {
        }
    }

    @Test
    public void eagerLoad() throws Exception {

        final Container container = AnnotationContainerBuilder.builder().eagerLoad(true).build()
                .register(EagerLoading.class)
                .build();

        assertTrue(EagerLoading.loaded);

        container.getComponent(EagerLoading.class);
    }

    @Test
    public void lazyLoad() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(LazyLoading.class)
                .build();

        assertFalse(LazyLoading.loaded);

        container.getComponent(LazyLoading.class);
    }

    @Test
    public void injectionContainer() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(ContainerInjection.class)
                .build();

        final ContainerInjection component = container.getComponent(ContainerInjection.class);

        assertNotNull(component.container);
    }

    @Test
    public void getComponentWithQualifier() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Qualifier2.class)
                .register(Qualifier3.class)
                .build();

        final Qualifier1 component1 = container.getComponent(Qualifier1.class,
                new NamedImpl("foo"));
        final Qualifier1 component2 = container.getComponent(Qualifier1.class,
                new NamedImpl("bar"));

        assertTrue(component1.getClass() == Qualifier2.class);
        assertTrue(component2.getClass() == Qualifier3.class);
    }

    @Test
    public void getComponentWithQualifierViaClassOnly() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Qualifier2.class)
                .build();

        final Qualifier1 component1 = container.getComponent(Qualifier1.class);

        assertTrue(component1.getClass() == Qualifier2.class);
    }

    @Test
    public void getComponents() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Qualifier2.class)
                .register(Qualifier3.class)
                .build();

        final Set<Qualifier1> components = container.getComponents(Qualifier1.class);
        final Set<Class<?>> componentClasses = components.stream().map(Qualifier1::getClass)
                .collect(Collectors.toSet());

        assertEquals(2, componentClasses.size());
        assertTrue(componentClasses.contains(Qualifier2.class));
        assertTrue(componentClasses.contains(Qualifier3.class));
    }

    @Test
    public void getComponentsSingle() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Aaa.class)
                .build();

        final Set<Aaa> components = container.getComponents(Aaa.class);
        final Aaa component = container.getComponent(Aaa.class);

        assertEquals(1, components.size());
        assertEquals(component, components.iterator().next());
    }

    @Test
    public void getComponentsAll() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .register(Jjj1.class)
                .register(Jjj2.class)
                .register(Jjj3.class)
                .build();

        final Set<Jjj1> components = container.getComponents(Jjj1.class);
        final Set<Class<?>> componentClasses = components.stream().map(Jjj1::getClass)
                .collect(Collectors.toSet());

        assertEquals(3, components.size());
        assertTrue(componentClasses.contains(Jjj1.class));
        assertTrue(componentClasses.contains(Jjj2.class));
        assertTrue(componentClasses.contains(Jjj3.class));
    }

    @Test
    public void getComponentsNoComponents() throws Exception {

        final Container container = AnnotationContainerBuilder.createDefault()
                .build();

        final Set<Aaa> components = container.getComponents(Aaa.class);

        assertEquals(0, components.size());
    }

    @Singleton
    private static class Aaa {
    }

    private interface Bbb1 {
    }

    private interface Bbb2 extends Bbb1 {
    }

    private static class Bbb3 implements Bbb2 {
    }

    private static class Bbb4 extends Bbb3 {
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

    private static class Fff1 {

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

    private static class Fff2 extends Fff1 {

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

    private static class Ggg1 {
        @Inject
        Aaa field1;
        @Inject
        Aaa field2;
        Aaa field3;

        public Aaa getField1() {
            return field1;
        }
    }

    private static class Ggg2 extends Ggg1 {
        @Inject
        Aaa field1;
        @Inject
        Aaa field4;
        Aaa field5;
    }

    private static class Hhh1 {

        Aaa component;
        Provider<Aaa> provider;

        @Inject
        public Hhh1(final Aaa component, final Provider<Aaa> provider) {
            this.component = component;
            this.provider = provider;
        }
    }

    private static class Hhh2 {

        @Inject
        Aaa component;
        @Inject
        Provider<Aaa> provider;
    }

    private static class Hhh3 {

        Aaa component;
        Provider<Aaa> provider;

        @Inject
        public void method(final Aaa component, final Provider<Aaa> provider) {
            this.component = component;
            this.provider = provider;
        }
    }

    private static class Iii1 {

        List<String> called = new ArrayList<>();

        @Inject
        void method1() {
            called.add("method1");
        }
    }

    private static class Iii2 extends Iii1 {

        @Inject
        void method2() {
            called.add("method2");
        }
    }

    @Singleton
    private static class EagerLoading {

        static boolean loaded;

        public EagerLoading() {
            loaded = true;
        }
    }

    @Singleton
    private static class LazyLoading {

        static boolean loaded;

        public LazyLoading() {
            loaded = true;
        }
    }

    private static class ContainerInjection {
        @Inject
        Container container;
    }

    private interface Qualifier1 {
    }

    @Named("foo")
    private static class Qualifier2 implements Qualifier1 {
    }

    @Named("bar")
    private static class Qualifier3 implements Qualifier1 {
    }

    @Singleton
    private static class Jjj1 {
    }

    @Singleton
    private static class Jjj2 extends Jjj1 {
    }

    @Singleton
    private static class Jjj3 extends Jjj1 {
    }
}
