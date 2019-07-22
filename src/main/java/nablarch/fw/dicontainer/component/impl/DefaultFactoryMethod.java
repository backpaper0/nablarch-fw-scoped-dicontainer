package nablarch.fw.dicontainer.component.impl;

import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.FactoryMethod;
import nablarch.fw.dicontainer.container.ContainerBuilder;

public final class DefaultFactoryMethod implements FactoryMethod {

    private final ComponentKey<?> key;
    private final ComponentDefinition<?> definition;

    public DefaultFactoryMethod(final ComponentKey<?> key,
            final ComponentDefinition<?> definition) {
        this.key = Objects.requireNonNull(key);
        this.definition = Objects.requireNonNull(definition);
    }

    @Override
    public void apply(final ContainerBuilder<?> containerBuilder) {
        containerBuilder.register((ComponentKey) key, (ComponentDefinition) definition);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
    }
}
