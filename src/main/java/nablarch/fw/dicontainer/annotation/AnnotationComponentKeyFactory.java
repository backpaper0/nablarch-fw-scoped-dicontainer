package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Qualifier;

import nablarch.fw.dicontainer.component.ComponentKey;

public final class AnnotationComponentKeyFactory {

    private final AnnotationSet qualifierAnnotations;

    private AnnotationComponentKeyFactory(final AnnotationSet qualifierAnnotations) {
        this.qualifierAnnotations = Objects.requireNonNull(qualifierAnnotations);
    }

    public <T> ComponentKey<T> fromComponentClass(final Class<T> componentType) {
        final Set<Annotation> qualifiers = Arrays.stream(componentType.getAnnotations())
                .filter(a -> qualifierAnnotations.isAnnotationPresent(a.annotationType()))
                .collect(Collectors.toSet());
        return new ComponentKey<>(componentType, qualifiers);
    }

    public ComponentKey<?> fromFactoryMethod(final Method factoryMethod) {

        final Set<Annotation> qualifiers = Arrays.stream(factoryMethod.getAnnotations())
                .filter(a -> qualifierAnnotations.isAnnotationPresent(a.annotationType()))
                .collect(Collectors.toSet());

        return new ComponentKey<>(factoryMethod.getReturnType(), qualifiers);
    }

    public static AnnotationComponentKeyFactory createDefault() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AnnotationSet qualifierAnnotations = new AnnotationSet(Qualifier.class);

        private Builder() {
        }

        public Builder qualifierAnnotations(final AnnotationSet qualifierAnnotations) {
            this.qualifierAnnotations = qualifierAnnotations;
            return this;
        }

        public AnnotationComponentKeyFactory build() {
            return new AnnotationComponentKeyFactory(qualifierAnnotations);
        }
    }
}
