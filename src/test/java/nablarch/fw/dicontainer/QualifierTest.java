package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Test;

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
}
