package nablarch.fw.dicontainer.component.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

public final class InjectionComponentResolvers {

    private final List<InjectionComponentResolver> resolvers;

    public InjectionComponentResolvers(final List<InjectionComponentResolver> resolvers) {
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    public static InjectionComponentResolvers empty() {
        return new InjectionComponentResolvers(Collections.emptyList());
    }

    public Object[] resolve(final ContainerImplementer container) {
        return resolvers.stream().map(resolver -> resolver.resolve(container)).toArray();
    }

    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validate(containerBuilder, self);
        }
    }

    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validateCycleDependency(context.createSubContext());
        }
    }
}
