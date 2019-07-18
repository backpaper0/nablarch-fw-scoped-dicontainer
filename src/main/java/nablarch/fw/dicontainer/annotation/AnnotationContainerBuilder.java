package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.container.ContainerBuilder;

public final class AnnotationContainerBuilder extends ContainerBuilder<AnnotationContainerBuilder> {

    private final AnnotationScopeDecider scopeDecider;
    //FIXME
    final AnnotationMemberFactory memberFactory;
    private final AnnotationComponentDefinitionFactory componentDefinitionFactory;

    private AnnotationContainerBuilder(final AnnotationScopeDecider scopeDecider,
            final AnnotationMemberFactory memberFactory,
            final AnnotationComponentDefinitionFactory componentDefinitionFactory) {
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.componentDefinitionFactory = Objects.requireNonNull(componentDefinitionFactory);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final ComponentKey<T> key = ComponentKey.fromClass(componentType);
        final Optional<ComponentDefinition<T>> definition = componentDefinitionFactory
                .fromClass(componentType, errorCollector);
        definition.ifPresent(a -> register(key, a));
        return this;
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType,
            final Annotation... qualifiers) {
        final ComponentKey<T> key = new ComponentKey<>(componentType, qualifiers);
        final Optional<ComponentDefinition<T>> definition = componentDefinitionFactory
                .fromClass(componentType, errorCollector);
        definition.ifPresent(a -> register(key, a));
        return this;
    }

    @Override
    public Container build() {
        scopeDecider.registerScopes(this);
        return super.build();
    }

    public static AnnotationContainerBuilder createDefault() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AnnotationScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();
        private AnnotationMemberFactory memberFactory = AnnotationMemberFactory.createDefault();
        private AnnotationComponentDefinitionFactory componentDefinitionFactory;

        private Builder() {
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
        }

        public Builder scopeDecider(final AnnotationScopeDecider scopeDecider) {
            this.scopeDecider = scopeDecider;
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
            return this;
        }

        public Builder memberFactory(final AnnotationMemberFactory memberFactory) {
            this.memberFactory = memberFactory;
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
            return this;
        }

        public Builder componentDefinitionFactory(
                final AnnotationComponentDefinitionFactory componentDefinitionFactory) {
            this.componentDefinitionFactory = componentDefinitionFactory;
            return this;
        }

        public AnnotationContainerBuilder build() {
            return new AnnotationContainerBuilder(scopeDecider, memberFactory,
                    componentDefinitionFactory);
        }
    }
}
