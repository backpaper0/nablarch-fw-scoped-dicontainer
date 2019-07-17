package nablarch.fw.dicontainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class ComponentDefinitionRepository {

    private final Map<ComponentKey<?>, ComponentDefinition<?>> definitionsMap = new HashMap<>();

    public <T> void register(final ComponentKey<T> key, final ComponentDefinition<T> definition) {
        definitionsMap.put(key, definition);
    }

    public <T> ComponentDefinition<T> find(final ComponentKey<T> key) {
        final ComponentDefinition<?> definition = definitionsMap.get(key);
        if (definition == null) {
            return null;
        }
        return (ComponentDefinition<T>) definition;
    }

    public <T> void fire(final Container container, final Object event) {
        for (final Entry<ComponentKey<?>, ComponentDefinition<?>> entry : definitionsMap
                .entrySet()) {
            final ComponentKey<T> key = (ComponentKey<T>) entry.getKey();
            final ComponentDefinition<T> definition = (ComponentDefinition<T>) entry.getValue();
            definition.fire(container, key, event);
        }
    }

    public void validate(final ContainerBuilder<?> containerBuilder) {
        for (final ComponentDefinition<?> definition : definitionsMap.values()) {
            definition.validate(containerBuilder);
        }
    }
}
