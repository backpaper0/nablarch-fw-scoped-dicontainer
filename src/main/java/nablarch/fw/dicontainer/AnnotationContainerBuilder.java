package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AnnotationContainerBuilder {

    private final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap = new LinkedHashMap<>();
    private final Map<ComponentKey.AliasKey, Set<ComponentKey<?>>> aliasesMap = new LinkedHashMap<>();
    private final AnnotationScopeDecider decider = new AnnotationScopeDecider();

    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final ComponentKey<T> key = ComponentKey.fromClass(componentType);
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = ComponentDefinition.builderFromAnnotation(componentType).scope(scope).build();
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final Class<T> componentType, final Annotation... qualifiers) {
        final ComponentKey<T> key = new ComponentKey<>(componentType,
                Arrays.stream(qualifiers).map(Qualifier::fromAnnotation).collect(Collectors.toSet()));
        final Scope scope = decider.decide(componentType);
        final ComponentDefinition<T> definition = ComponentDefinition.builderFromAnnotation(componentType).scope(scope).build();
        return register(key, definition);
    }

    public <T> AnnotationContainerBuilder register(final ComponentKey<T> key,
            final ComponentDefinition<T> definition) {
        key.aliasKeys().forEach(aliasKey -> {
            if (aliasesMap.containsKey(aliasKey) == false) {
                aliasesMap.put(aliasKey, new LinkedHashSet<>());
            }
            final Set<ComponentKey<?>> keys = aliasesMap.get(aliasKey);
            keys.add(key);
        });
        if (definitionsMap.containsKey(key) == false) {
            definitionsMap.put(key, new LinkedHashSet<>());
        }
        final Set<ComponentDefinition<?>> definitions = definitionsMap.get(key);
        definitions.add(definition);
        return this;
    }

    public Container build() {
        return new DefaultContainer(definitionsMap, aliasesMap);
    }
}
