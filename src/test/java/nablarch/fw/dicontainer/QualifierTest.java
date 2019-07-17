package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Test;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

public class QualifierTest {

    @Named("spare")
    Object spare;

    @Singleton
    Object notQualifier;

    @Test
    public void fromAnnotation() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("spare")
                .getAnnotation(Named.class);
        final Qualifier qualifier = Qualifier.fromAnnotation(annotation);
        assertNotNull(qualifier);
    }

    @Test
    public void fromAnnotationNotQualifier() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("notQualifier")
                .getAnnotation(Singleton.class);
        try {
            Qualifier.fromAnnotation(annotation);
            fail();
        } catch (final RuntimeException e) {
        }
    }

    @Test
    public void equals() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("spare")
                .getAnnotation(Named.class);
        final Qualifier qualifier1 = Qualifier.fromAnnotation(annotation);
        final Qualifier qualifier2 = Qualifier.fromAnnotation(new NamedImpl("spare"));
        assertTrue(qualifier1.equals(qualifier2));
    }

    @Test
    public void equalsReverse() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("spare")
                .getAnnotation(Named.class);
        final Qualifier qualifier1 = Qualifier.fromAnnotation(annotation);
        final Qualifier qualifier2 = Qualifier.fromAnnotation(new NamedImpl("spare"));
        assertTrue(qualifier2.equals(qualifier1));
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

    private interface Ccc1 {
    }

    @Singleton
    @Named("foo")
    private static class Ccc2 implements Ccc1 {
    }

    @Singleton
    @Named("bar")
    private static class Ccc3 implements Ccc1 {
    }

    @Singleton
    private static class Eee1 {
        @Inject
        @Named("foo")
        Ccc1 foo;
        @Inject
        @Named("bar")
        Ccc1 bar;
    }

    @Singleton
    private static class Eee2 {

        Ccc1 foo;
        Ccc1 bar;

        @Inject
        void setFoo(@Named("foo") final Ccc1 foo, @Named("bar") final Ccc1 bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    @Singleton
    private static class Eee3 {

        Ccc1 foo;
        Ccc1 bar;

        @Inject
        Eee3(@Named("foo") final Ccc1 foo, @Named("bar") final Ccc1 bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }
}
