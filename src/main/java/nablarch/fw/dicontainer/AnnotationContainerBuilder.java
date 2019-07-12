package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class AnnotationContainerBuilder {

    private final ComponentDefinitionRepository definitions = new ComponentDefinitionRepository();
    private final AliasMapping aliasesMap = new AliasMapping();
    private final AnnotationScopeDecider decider;

    public AnnotationContainerBuilder() {
        this(new AnnotationScopeDecider());
    }

    public AnnotationContainerBuilder(final AnnotationScopeDecider decider) {
        this.decider = Objects.requireNonNull(decider);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final ComponentKey<T> key = ComponentKey.fromClass(componentType);
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = ComponentDefinition
                .builderFromAnnotation(componentType).scope(scope).build();
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType,
            final Annotation... qualifiers) {
        final ComponentKey<T> key = new ComponentKey<>(componentType,
                Arrays.stream(qualifiers).map(Qualifier::fromAnnotation)
                        .collect(Collectors.toSet()));
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = ComponentDefinition
                .builderFromAnnotation(componentType).scope(scope).build();
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final ComponentKey<T> key,
            final ComponentDefinition<T> definition) {
        key.aliasKeys().forEach(aliasKey -> aliasesMap.register(aliasKey, key));
        definitions.register(key, definition);
        return this;
    }

    public Container build() {
        decider.registerScopes(this);
        return new DefaultContainer(definitions, aliasesMap);
    }
}
