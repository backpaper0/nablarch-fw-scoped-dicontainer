package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

public final class InjectionComponentResolver {

    private final ComponentKey<?> key;
    private final boolean provider;

    public InjectionComponentResolver(final ComponentKey<?> key, final boolean provider) {
        this.key = Objects.requireNonNull(key);
        this.provider = provider;
    }

    public static InjectionComponentResolver fromField(final Field field) {
        final Set<Qualifier> qualifiers = new LinkedHashSet<>();
        for (final Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(javax.inject.Qualifier.class)) {
                final Qualifier qualifier = Qualifier.fromAnnotation(annotation);
                qualifiers.add(qualifier);
            }
        }

        final boolean provider = field.getType() == Provider.class;
        Class<?> componentType;
        if (provider) {
            componentType = (Class<?>) ((ParameterizedType) field.getGenericType())
                    .getActualTypeArguments()[0];
        } else {
            componentType = field.getType();
        }

        final ComponentKey<?> key = new ComponentKey<>(componentType, qualifiers);
        return new InjectionComponentResolver(key, provider);
    }

    public static List<InjectionComponentResolver> fromMethodParameters(final Method method) {
        final List<InjectionComponentResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < method.getParameterCount(); i++) {
            final Set<Qualifier> qualifiers = new LinkedHashSet<>();
            for (final Annotation annotation : method.getParameterAnnotations()[i]) {
                if (annotation.annotationType().isAnnotationPresent(javax.inject.Qualifier.class)) {
                    final Qualifier qualifier = Qualifier.fromAnnotation(annotation);
                    qualifiers.add(qualifier);
                }
            }

            final boolean provider = method.getParameterTypes()[i] == Provider.class;
            Class<?> componentType;
            if (provider) {
                componentType = (Class<?>) ((ParameterizedType) method
                        .getGenericParameterTypes()[i]).getActualTypeArguments()[0];
            } else {
                componentType = method.getParameterTypes()[i];
            }

            final ComponentKey<?> key = new ComponentKey<>(componentType, qualifiers);
            final InjectionComponentResolver resolver = new InjectionComponentResolver(key,
                    provider);
            resolvers.add(resolver);
        }
        return resolvers;
    }

    public static List<InjectionComponentResolver> fromConstructorParameters(
            final Constructor<?> constructor) {
        final List<InjectionComponentResolver> resolvers = new ArrayList<>();
        for (int i = 0; i < constructor.getParameterCount(); i++) {
            final Set<Qualifier> qualifiers = new LinkedHashSet<>();
            for (final Annotation annotation : constructor.getParameterAnnotations()[i]) {
                if (annotation.annotationType()
                        .isAnnotationPresent(javax.inject.Qualifier.class)) {
                    final Qualifier qualifier = Qualifier.fromAnnotation(annotation);
                    qualifiers.add(qualifier);
                }
            }

            final boolean provider = constructor.getParameterTypes()[i] == Provider.class;
            Class<?> componentType;
            if (provider) {
                componentType = (Class<?>) ((ParameterizedType) constructor
                        .getGenericParameterTypes()[i]).getActualTypeArguments()[0];
            } else {
                componentType = constructor.getParameterTypes()[i];
            }

            final ComponentKey<?> key = new ComponentKey<>(componentType, qualifiers);
            final InjectionComponentResolver resolver = new InjectionComponentResolver(key,
                    provider);
            resolvers.add(resolver);
        }
        return resolvers;
    }

    public Object resolve(final Container container) {
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
}
