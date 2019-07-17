package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Qualifier;
import nablarch.fw.dicontainer.Scope;
import nablarch.fw.dicontainer.config.ComponentDefinition;
import nablarch.fw.dicontainer.config.ContainerBuilder;

public final class AnnotationContainerBuilder extends ContainerBuilder<AnnotationContainerBuilder> {

    private final AnnotationScopeDecider decider;
    //FIXME
    final AnnotationMemberFactory memberFactory = new AnnotationMemberFactory();
    private final AnnotationComponentDefinitionFactory componentDefinitionFactory;

    public AnnotationContainerBuilder() {
        this(new AnnotationScopeDecider());
    }

    public AnnotationContainerBuilder(final AnnotationScopeDecider decider) {
        this.decider = Objects.requireNonNull(decider);
        this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(memberFactory);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final ComponentKey<T> key = ComponentKey.fromClass(componentType);
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = componentDefinitionFactory
                .builder(componentType, errorCollector).scope(scope).build();
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType,
            final Annotation... qualifiers) {
        final ComponentKey<T> key = new ComponentKey<>(componentType,
                Arrays.stream(qualifiers).map(Qualifier::fromAnnotation)
                        .collect(Collectors.toSet()));
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = componentDefinitionFactory
                .builder(componentType, errorCollector).scope(scope).build();
        return register(key, definition);
    }

    @Override
    public Container build() {
        decider.registerScopes(this);
        return super.build();
    }
}
