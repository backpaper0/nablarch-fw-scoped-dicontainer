package nablarch.fw.dicontainer.component;

import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.exception.InjectionComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectionComponentNotFoundException;
import nablarch.fw.dicontainer.exception.InvalidInjectionScopeException;

public final class InjectionComponentResolver {

    private final ComponentKey<?> key;
    private final boolean provider;

    public InjectionComponentResolver(final ComponentKey<?> key, final boolean provider) {
        this.key = Objects.requireNonNull(key);
        this.provider = provider;
    }

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

    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        final Set<ComponentDefinition<?>> definitions = containerBuilder
                .findComponentDefinitions(key);
        if (definitions.isEmpty()) {
            containerBuilder.addError(new InjectionComponentNotFoundException());
        } else if (definitions.size() > 1) {
            containerBuilder.addError(new InjectionComponentDuplicatedException());
        } else if (provider == false) {
            final ComponentDefinition<?> injected = definitions.iterator().next();
            if (self.isNarrowScope(injected) == false) {
                containerBuilder.addError(new InvalidInjectionScopeException());
            } else {
                containerBuilder.validateCycleDependency(key, self);
            }
        }
    }

    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        if (provider == false) {
            context.validateCycleDependency(key);
        }
    }
}
