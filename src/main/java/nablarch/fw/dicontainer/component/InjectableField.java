package nablarch.fw.dicontainer.component;

import java.lang.reflect.Field;
import java.util.Objects;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerBuilder.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class InjectableField implements InjectableMember {

    private final Field field;
    private final InjectionComponentResolver resolver;

    public InjectableField(final Field field, final InjectionComponentResolver resolver) {
        this.field = Objects.requireNonNull(field);
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public Object inject(final ContainerImplementer container, final Object component) {
        final Object value = resolver.resolve(container);
        if (field.isAccessible() == false) {
            field.setAccessible(true);
        }
        try {
            field.set(component, value);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        resolver.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolver.validateCycleDependency(context);
    }
}
