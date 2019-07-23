package nablarch.fw.dicontainer.component.impl;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.InjectionComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectionComponentNotFoundException;
import nablarch.fw.dicontainer.exception.InvalidInjectionScopeException;

public final class DefaultInjectionComponentResolver implements InjectionComponentResolver {

    private final ComponentKey<?> key;
    private final boolean provider;

    public DefaultInjectionComponentResolver(final ComponentKey<?> key, final boolean provider) {
        this.key = Objects.requireNonNull(key);
        this.provider = provider;
    }

    @Override
    public Object resolve(final ContainerImplementer container) {
        if (provider) {
            return new Provider<Object>() {
                @Override
                public Object get() {
                    return container.getComponent(key);
                }
            };
        }
        return container.getComponent(key);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        final Set<ComponentDefinition<?>> definitions = containerBuilder
                .findComponentDefinitions(key);
        if (definitions.isEmpty()) {
            containerBuilder.addError(new InjectionComponentNotFoundException("key=" + key));
        } else if (definitions.size() > 1) {
            containerBuilder.addError(new InjectionComponentDuplicatedException(
                    "key=" + key + ", definitions=" + definitions));
        } else if (provider == false) {
            final ComponentDefinition<?> injected = definitions.iterator().next();
            if (self.isNarrowScope(injected) == false) {
                containerBuilder.addError(new InvalidInjectionScopeException(
                        "[" + self + "] must be narrow scope than [" + injected + "] (Or wrap ["
                                + injected + "] with Provider)."));
            } else {
                containerBuilder.validateCycleDependency(key, self);
            }
        }
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        if (provider == false) {
            context.validateCycleDependency(key);
        }
    }
}