package nablarch.fw.dicontainer.annotation.auto;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.inject.Qualifier;
import javax.inject.Scope;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.annotation.AnnotationSet;

public final class AnnotationAutoContainerFactory {

    private final AnnotationSet targetAnnotations;
    private final Iterable<TraversalConfig> traversalConfigs;
    private final boolean eagerLoad;
    private final AnnotationScopeDecider scopeDecider;

    private AnnotationAutoContainerFactory(final AnnotationSet targetAnnotations,
            final Iterable<TraversalConfig> traversalConfigs, final boolean eagerLoad,
            final AnnotationScopeDecider scopeDecider) {
        this.targetAnnotations = Objects.requireNonNull(targetAnnotations);
        this.traversalConfigs = Objects.requireNonNull(traversalConfigs);
        this.eagerLoad = eagerLoad;
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
    }

    public Container create() {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.builder()
                .scopeDecider(scopeDecider)
                .eagerLoad(eagerLoad).build();
        for (final TraversalConfig traversalConfig : traversalConfigs) {
            final ClassLoader classLoader = traversalConfig.getClass().getClassLoader();
            final Class<?> base = traversalConfig.getClass();
            final ClassFilter classFilter = ClassFilter.valueOf(traversalConfig);
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
        private Iterable<TraversalConfig> traversalConfigs;
        private boolean eagerLoad;
        private AnnotationScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();

        private Builder() {
        }

        @SafeVarargs
        public final Builder targetAnnotations(final Class<? extends Annotation>... annotations) {
            this.targetAnnotations = new AnnotationSet(annotations);
            return this;
        }

        public Builder traversalConfigs(final Iterable<TraversalConfig> traversalConfigs) {
            this.traversalConfigs = traversalConfigs;
            return this;
        }

        public Builder eagerLoad(final boolean eagerLoad) {
            this.eagerLoad = eagerLoad;
            return this;
        }

        public Builder scopeDecider(final AnnotationScopeDecider scopeDecider) {
            this.scopeDecider = scopeDecider;
            return this;
        }

        public AnnotationAutoContainerFactory build() {
            return new AnnotationAutoContainerFactory(targetAnnotations, traversalConfigs,
                    eagerLoad, scopeDecider);
        }
    }
}
