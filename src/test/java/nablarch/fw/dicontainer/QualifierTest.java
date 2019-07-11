package nablarch.fw.dicontainer;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import javax.inject.Named;
import javax.inject.Singleton;

import org.junit.Test;

public class QualifierTest {

    @Named("spare")
    Object spare;

    @Named
    Object defaultValue;

    @Singleton
    Object notQualifier;

    @Test
    public void fromAnnotation() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("spare")
                .getAnnotation(Named.class);
        final Qualifier actual = Qualifier.fromAnnotation(annotation);
        final Qualifier expected = Qualifier.builder()
                .name(Named.class.getName())
                .element("value", "spare")
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void fromAnnotationDefaultValue() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("defaultValue")
                .getAnnotation(Named.class);
        final Qualifier actual = Qualifier.fromAnnotation(annotation);
        final Qualifier expected = Qualifier.builder()
                .name(Named.class.getName())
                .element("value", "")
                .build();
        assertEquals(expected, actual);
    }

    @Test
    public void fromAnnotationNotQualifier() throws Exception {
        final Annotation annotation = getClass().getDeclaredField("notQualifier")
                .getAnnotation(Singleton.class);
        try {
            Qualifier.fromAnnotation(annotation);
            fail();
        } catch (final IllegalArgumentException e) {
        }
    }
}
