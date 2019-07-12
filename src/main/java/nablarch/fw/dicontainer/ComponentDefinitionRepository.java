package nablarch.fw.dicontainer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class ComponentDefinitionRepository {

    private final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap = new HashMap<>();

    public <T> void register(final ComponentKey<T> key, final ComponentDefinition<T> definition) {
        if (definitionsMap.containsKey(key) == false) {
            definitionsMap.put(key, new LinkedHashSet<>());
        }
        final Set<ComponentDefinition<?>> definitions = definitionsMap.get(key);
        definitions.add(definition);
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
}
