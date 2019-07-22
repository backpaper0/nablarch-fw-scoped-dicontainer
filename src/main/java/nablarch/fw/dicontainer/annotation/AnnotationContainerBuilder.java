package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.InvalidComponentException;

public final class AnnotationContainerBuilder extends ContainerBuilder<AnnotationContainerBuilder> {

    private final AnnotationComponentKeyFactory componentKeyFactory;
    private final AnnotationScopeDecider scopeDecider;
    //FIXME
    final AnnotationMemberFactory memberFactory;
    private final AnnotationComponentDefinitionFactory componentDefinitionFactory;

    private AnnotationContainerBuilder(final AnnotationComponentKeyFactory componentKeyFactory,
            final AnnotationScopeDecider scopeDecider,
            final AnnotationMemberFactory memberFactory,
            final AnnotationComponentDefinitionFactory componentDefinitionFactory) {
        this.componentKeyFactory = Objects.requireNonNull(componentKeyFactory);
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.componentDefinitionFactory = Objects.requireNonNull(componentDefinitionFactory);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {

        if (componentType.isAnnotation()) {
            errorCollector.add(new InvalidComponentException(
                    "Annotation [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isInterface()) {
            errorCollector.add(new InvalidComponentException(
                    "Interface [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isEnum()) {
            errorCollector.add(new InvalidComponentException(
                    "Enum [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isAnonymousClass()) {
            errorCollector.add(new InvalidComponentException(
                    "Anonymous Class [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (Modifier.isAbstract(componentType.getModifiers())) {
            errorCollector.add(new InvalidComponentException(
                    "Abstract Class [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }

        final ComponentKey<T> key = componentKeyFactory.fromComponentClass(componentType);
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

        private AnnotationComponentKeyFactory componentKeyFactory = AnnotationComponentKeyFactory
                .createDefault();
        private AnnotationScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();
        private AnnotationMemberFactory memberFactory = AnnotationMemberFactory.createDefault();
        private AnnotationComponentDefinitionFactory componentDefinitionFactory;

        private Builder() {
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
        }

        public Builder componentKeyFactory(
                final AnnotationComponentKeyFactory componentKeyFactory) {
            this.componentKeyFactory = componentKeyFactory;
            return this;
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

        public Builder eagerLoad(final boolean eagerLoad) {
            scopeDecider(AnnotationScopeDecider.builder().eagerLoad(eagerLoad).build());
            return this;
        }

        public AnnotationContainerBuilder build() {
            return new AnnotationContainerBuilder(componentKeyFactory, scopeDecider, memberFactory,
                    componentDefinitionFactory);
        }
    }
}
