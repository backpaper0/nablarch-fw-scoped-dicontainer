package nablarch.fw.dicontainer.annotation.auto;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.inject.Qualifier;
import javax.inject.Scope;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationSet;

public final class AnnotationAutoContainerFactory {

    private final AnnotationSet targetAnnotations;
    private final Iterable<TraversalConfig> traversalMarks;
    private final boolean eagerLoad;

    private AnnotationAutoContainerFactory(final AnnotationSet targetAnnotations,
            final Iterable<TraversalConfig> traversalMarks, final boolean eagerLoad) {
        this.targetAnnotations = Objects.requireNonNull(targetAnnotations);
        this.traversalMarks = Objects.requireNonNull(traversalMarks);
        this.eagerLoad = eagerLoad;
    }

    public Container create() {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.builder()
                .eagerLoad(eagerLoad).build();
        for (final TraversalConfig traversalMark : traversalMarks) {
            final ClassLoader classLoader = traversalMark.getClass().getClassLoader();
            final Class<?> base = traversalMark.getClass();
            final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);
            final ClassTraverser classTraverser = new ClassTraverser(classLoader, base,
                    classFilter);
            classTraverser.traverse(clazz -> {
                if (isTarget(clazz)) {
                    builder.register(clazz);
                }
            });
        }
        return builder.build();
    }

    private boolean isTarget(final Class<?> clazz) {
        for (final Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (targetAnnotations.isAnnotationPresent(annotationType)) {
                return true;
            }
        }
        return false;
    }

    public static AnnotationAutoContainerFactory createDefault() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AnnotationSet targetAnnotations = new AnnotationSet(Scope.class, Qualifier.class);
        private Iterable<TraversalConfig> traversalMarks;
        private boolean eagerLoad;

        private Builder() {
        }

        @SafeVarargs
        public final Builder targetAnnotations(final Class<? extends Annotation>... annotations) {
            this.targetAnnotations = new AnnotationSet(annotations);
            return this;
        }

        public Builder traversalMarks(final Iterable<TraversalConfig> traversalMarks) {
            this.traversalMarks = traversalMarks;
            return this;
        }

        public Builder eagerLoad(final boolean eagerLoad) {
            this.eagerLoad = eagerLoad;
            return this;
        }

        public AnnotationAutoContainerFactory build() {
            return new AnnotationAutoContainerFactory(targetAnnotations, traversalMarks, eagerLoad);
        }
    }
}
