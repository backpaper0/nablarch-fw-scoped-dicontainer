package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class DefaultContainer implements Container {

    private final ComponentDefinitionRepository definitions;
    private final AliasMapping aliasMapping;

    public DefaultContainer(final ComponentDefinitionRepository definitionsMap,
            final AliasMapping aliasesMap) {
        this.definitions = Objects.requireNonNull(definitionsMap);
        this.aliasMapping = Objects.requireNonNull(aliasesMap);
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key) {
        ComponentDefinition<T> definition = definitions.find(key);
        if (definition != null) {
            return definition.getComponent(this, key);
        }
        final ComponentKey<T> alterKey = aliasMapping.find(key.asAliasKey());
        definition = definitions.find(alterKey);
        return definition.getComponent(this, key);
    }

    @Override
    public <T> T getComponent(final Class<T> key) {
        return getComponent(ComponentKey.fromClass(key));
    }

    @Override
    public <T> T getComponent(final Class<T> key, final Annotation... qualifiers) {
        final Set<Qualifier> qs = Arrays.stream(qualifiers).map(Qualifier::fromAnnotation)
                .collect(Collectors.toSet());
        return getComponent(new ComponentKey<>(key, qs));
    }

    @Override
    public void fire(final Object event) {
        definitions.fire(this, event);
    }

    @Override
    public void destroy() {
        fire(new ContainerDestroy());
    }
}
