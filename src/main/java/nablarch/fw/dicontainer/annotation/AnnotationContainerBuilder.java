package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.util.Objects;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.config.ComponentDefinition;
import nablarch.fw.dicontainer.config.ContainerBuilder;

public final class AnnotationContainerBuilder extends ContainerBuilder<AnnotationContainerBuilder> {

    private final AnnotationScopeDecider scopeDecider;
    //FIXME
    final AnnotationMemberFactory memberFactory = new AnnotationMemberFactory();
    private final AnnotationComponentDefinitionFactory componentDefinitionFactory;

    public AnnotationContainerBuilder() {
        this(new AnnotationScopeDecider());
    }

    public AnnotationContainerBuilder(final AnnotationScopeDecider scopeDecider) {
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
        this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(memberFactory,
                scopeDecider);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final ComponentKey<T> key = ComponentKey.fromClass(componentType);
        final ComponentDefinition<T> definition = componentDefinitionFactory
                .fromClass(componentType, errorCollector);
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType,
            final Annotation... qualifiers) {
        final ComponentKey<T> key = new ComponentKey<>(componentType, qualifiers);
        final ComponentDefinition<T> definition = componentDefinitionFactory
                .fromClass(componentType, errorCollector);
        return register(key, definition);
    }

    @Override
    public Container build() {
        scopeDecider.registerScopes(this);
        return super.build();
    }
}