package nablarch.fw.dicontainer.component;

import java.util.HashMap;
import java.util.Map;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

public final class ComponentDefinitionRepository {

    private final Map<ComponentId, ComponentDefinition<?>> idToDefinition = new HashMap<>();
    private final Map<ComponentKey<?>, ComponentId> keyToId = new HashMap<>();

    public <T> void register(final ComponentKey<T> key, final ComponentDefinition<T> definition) {
        final ComponentId id = definition.getId();
        idToDefinition.put(id, definition);
        keyToId.put(key, id);
    }

    public <T> ComponentDefinition<T> get(final ComponentId id) {
        final ComponentDefinition<?> definition = idToDefinition.get(id);
        if (definition == null) {
            throw new ComponentNotFoundException();
        }
        return (ComponentDefinition<T>) definition;
    }

    public <T> ComponentDefinition<T> find(final ComponentKey<T> key) {
        final ComponentId id = keyToId.get(key);
        final ComponentDefinition<?> definition = idToDefinition.get(id);
        if (definition == null) {
            return null;
        }
        return (ComponentDefinition<T>) definition;
    }

    public <T> void fire(final ContainerImplementer container, final Object event) {
        for (final ComponentDefinition<?> definition : idToDefinition.values()) {
            definition.fire(container, event);
        }
    }

    public void validate(final ContainerBuilder<?> containerBuilder) {
        for (final ComponentDefinition<?> definition : idToDefinition.values()) {
            definition.validate(containerBuilder);
        }
    }
}
