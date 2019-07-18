package nablarch.fw.dicontainer.scope;

import java.util.HashMap;
import java.util.Map;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;

public abstract class AbstractScope implements Scope {

    protected final Map<ComponentId, ComponentDefinition<?>> idToDefinition = new HashMap<>();

    @Override
    public <T> void register(final ComponentDefinition<T> definition) {
        final ComponentId id = definition.getId();
        idToDefinition.put(id, definition);
    }
}
