package nablarch.fw.dicontainer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ComponentDefinitionRepository {

    private final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap;

    private ComponentDefinitionRepository(
            final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap) {
        this.definitionsMap = Objects.requireNonNull(definitionsMap);
    }

    public <T> ComponentDefinition<T> find(final ComponentKey<T> key) {
        final Set<ComponentDefinition<?>> definitions = definitionsMap.get(key);
        if (definitions == null || definitions.isEmpty()) {
            return null;
        }
        if (definitions.size() > 1) {
            //TODO error
            throw new RuntimeException();
        }
        final ComponentDefinition<?> definition = definitions.iterator().next();
        return (ComponentDefinition<T>) definition;
    }

    public <T> void fire(final Container container, final Object event) {
        for (final Entry<ComponentKey<?>, Set<ComponentDefinition<?>>> entry : definitionsMap
                .entrySet()) {
            final ComponentKey<T> key = (ComponentKey<T>) entry.getKey();
            for (final ComponentDefinition<?> d : entry.getValue()) {
                final ComponentDefinition<T> definition = (ComponentDefinition<T>) d;
                definition.fire(container, key, event);
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<ComponentKey<?>, Set<ComponentDefinition.Builder<?>>> definitionsMap = new HashMap<>();

        private Builder() {
        }

        public <T> Builder register(final ComponentKey<T> key,
                final ComponentDefinition.Builder<T> definition) {
            if (definitionsMap.containsKey(key) == false) {
                definitionsMap.put(key, new LinkedHashSet<>());
            }
            final Set<ComponentDefinition.Builder<?>> definitions = definitionsMap.get(key);
            definitions.add(definition);
            return this;
        }

        public ComponentDefinitionRepository build() {
            final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> map = new HashMap<>(
                    definitionsMap.size());
            for (final Entry<ComponentKey<?>, Set<ComponentDefinition.Builder<?>>> entry : definitionsMap
                    .entrySet()) {
                final Set<ComponentDefinition<?>> value = entry.getValue().stream()
                        .map(ComponentDefinition.Builder::build)
                        .collect(Collectors.toSet());
                map.put(entry.getKey(), value);
            }
            return new ComponentDefinitionRepository(map);
        }
    }
}
