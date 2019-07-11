package nablarch.fw.dicontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public final class InjectableConstructor<T> implements InjectableMember {

    private final Constructor<T> constructor;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableConstructor(final Constructor<T> constructor, final List<InjectionComponentResolver> resolvers) {
        this.constructor = Objects.requireNonNull(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    public static <T> InjectableConstructor<T> fromAnnotation(final Class<T> componentType) {
        try {

            final List<Constructor<T>> constructors = new ArrayList<>();
            for (final Constructor<?> c : componentType.getDeclaredConstructors()) {
                final Constructor<T> constructor = (Constructor<T>) c;
                if (constructor.isAnnotationPresent(Inject.class)) {
                    constructors.add(constructor);
                }
            }
            if (constructors.size() > 1) {
                //TODO error
            }
            if (constructors.size() == 1) {
                final Constructor<T> constructor = constructors.get(0);
                final List<InjectionComponentResolver> resolvers = InjectionComponentResolver.fromConstructorParameters(constructor);
                return new InjectableConstructor<>(constructor, resolvers);
            }

            final Constructor<T> constructor = componentType.getDeclaredConstructor();
            final List<InjectionComponentResolver> resolvers = Collections.emptyList();
            return new InjectableConstructor<>(constructor, resolvers);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T inject(final Container container, final Object component) {
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container)).toArray();
        try {
            if (constructor.isAccessible() == false) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
