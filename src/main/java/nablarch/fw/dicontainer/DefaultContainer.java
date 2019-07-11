package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import nablarch.fw.dicontainer.ComponentKey.AliasKey;

public final class DefaultContainer implements Container {

    private final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap;
    private final Map<AliasKey, Set<ComponentKey<?>>> aliasesMap;

    public DefaultContainer(
            final Map<ComponentKey<?>, Set<ComponentDefinition<?>>> definitionsMap,
            final Map<AliasKey, Set<ComponentKey<?>>> aliasesMap) {
        this.definitionsMap = Objects.requireNonNull(definitionsMap);
        this.aliasesMap = Objects.requireNonNull(aliasesMap);
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key) {
        Set<ComponentDefinition<?>> definitions = definitionsMap.get(key);
        if (definitions == null) {
            final Set<ComponentKey<?>> keys = aliasesMap.get(key.asAliasKey());
            if (keys.size() > 1) {
                //TODO error
            }
            definitions = definitionsMap.get(keys.iterator().next());
        }
        if (definitions.size() == 1) {
            final ComponentDefinition<T> definition = (ComponentDefinition<T>) definitions
                    .iterator().next();
            return definition.getComponent(this, key);
        }
        return null;
    }

    @Override
    public <T> T getComponent(final Class<T> key) {
        return getComponent(ComponentKey.fromClass(key));
    }

    @Override
    public <T> T getComponent(final Class<T> key, final Annotation... qualifiers) {
        final Set<Qualifier> qs = Arrays.stream(qualifiers).map(Qualifier::fromAnnotation).collect(Collectors.toSet());
        return getComponent(new ComponentKey<>(key, qs));
    }
}
