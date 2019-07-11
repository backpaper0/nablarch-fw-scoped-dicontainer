package nablarch.fw.dicontainer;

import java.lang.reflect.Field;
import java.util.Objects;

public final class InjectableField implements InjectableMember {

    private final Field field;
    private final InjectionComponentResolver resolver;

    public InjectableField(final Field field, final InjectionComponentResolver resolver) {
        this.field = Objects.requireNonNull(field);
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public Object inject(final Container container, final Object component) {
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
}
