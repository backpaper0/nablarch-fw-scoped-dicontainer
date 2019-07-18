package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;

public final class AnnotationSet {

    private final Set<Class<? extends Annotation>> annotationClasses;

    @SafeVarargs
    public AnnotationSet(final Class<? extends Annotation>... annotationClasses) {
        this(Stream.of(annotationClasses).collect(Collectors.toSet()));
    }

    public AnnotationSet(final Set<Class<? extends Annotation>> annotationClasses) {
        this.annotationClasses = Objects.requireNonNull(annotationClasses);
    }

    public boolean isAnnotationPresent(final AnnotatedElement annotatedElement) {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            for (final Class<? extends Annotation> annotationClass : annotationClasses) {
                if (annotation.annotationType().equals(annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<String> getStringElement(final AnnotatedElement annotatedElement,
            final String elementName) {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            for (final Class<? extends Annotation> annotationClass : annotationClasses) {
                if (annotation.annotationType().equals(annotationClass)) {
                    for (final Method elementMethod : annotationClass.getDeclaredMethods()) {
                        if (elementMethod.getName().equals(elementName)) {
                            final String value = (String) new MethodWrapper(elementMethod)
                                    .invoke(annotation);
                            return Optional.of(value);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
