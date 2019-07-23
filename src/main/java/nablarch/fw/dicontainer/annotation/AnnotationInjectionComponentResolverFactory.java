package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.DefaultInjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;

public final class AnnotationInjectionComponentResolverFactory {

    private final AnnotationSet qualifierAnnotations;

    public AnnotationInjectionComponentResolverFactory(final AnnotationSet qualifierAnnotations) {
        this.qualifierAnnotations = Objects.requireNonNull(qualifierAnnotations);
    }

    public InjectionComponentResolver fromField(final Field field) {
        return fromSource(new FieldSource(field));
    }

    public InjectionComponentResolvers fromMethodParameters(final Method method) {
        final List<InjectionComponentResolver> resolvers = IntStream
                .range(0, method.getParameterCount())
                .mapToObj(i -> new MethodParameterSource(method, i))
                .map(this::fromSource)
                .collect(Collectors.toList());
        return new InjectionComponentResolvers(resolvers);
    }

    public InjectionComponentResolvers fromConstructorParameters(
            final Constructor<?> constructor) {
        final List<InjectionComponentResolver> resolvers = IntStream
                .range(0, constructor.getParameterCount())
                .mapToObj(i -> new ConstructorParameterSource(constructor, i))
                .map(this::fromSource)
                .collect(Collectors.toList());
        return new InjectionComponentResolvers(resolvers);
    }

    private InjectionComponentResolver fromSource(final Source source) {
        final Set<Annotation> qualifiers = Arrays.stream(source.getAnnotations())
                .filter(a -> qualifierAnnotations.isAnnotationPresent(a.annotationType()))
                .collect(Collectors.toSet());

        final boolean provider = source.getComponentType().equals(Provider.class);
        final Class<?> componentType;
        if (provider) {
            componentType = (Class<?>) source.getGenericComponentType()
                    .getActualTypeArguments()[0];
        } else {
            componentType = source.getComponentType();
        }

        final ComponentKey<?> key = new ComponentKey<>(componentType, qualifiers);
        return new DefaultInjectionComponentResolver(key, provider);
    }

    private interface Source {

        Annotation[] getAnnotations();

        Class<?> getComponentType();

        ParameterizedType getGenericComponentType();
    }

    private static final class FieldSource implements Source {

        private final Field field;

        public FieldSource(final Field field) {
            this.field = Objects.requireNonNull(field);
        }

        @Override
        public Annotation[] getAnnotations() {
            return field.getAnnotations();
        }

        @Override
        public Class<?> getComponentType() {
            return field.getType();
        }

        @Override
        public ParameterizedType getGenericComponentType() {
            return (ParameterizedType) field.getGenericType();
        }
    }

    private static final class MethodParameterSource implements Source {

        private final Method method;
        private final int index;

        public MethodParameterSource(final Method method, final int index) {
            this.method = Objects.requireNonNull(method);
            this.index = index;
        }

        @Override
        public Annotation[] getAnnotations() {
            return method.getParameterAnnotations()[index];
        }

        @Override
        public Class<?> getComponentType() {
            return method.getParameterTypes()[index];
        }

        @Override
        public ParameterizedType getGenericComponentType() {
            return (ParameterizedType) method.getGenericParameterTypes()[index];
        }
    }

    private static final class ConstructorParameterSource implements Source {

        private final Constructor<?> constructor;
        private final int index;

        public ConstructorParameterSource(final Constructor<?> constructor, final int index) {
            this.constructor = Objects.requireNonNull(constructor);
            this.index = index;
        }

        @Override
        public Annotation[] getAnnotations() {
            return constructor.getParameterAnnotations()[index];
        }

        @Override
        public Class<?> getComponentType() {
            return constructor.getParameterTypes()[index];
        }

        @Override
        public ParameterizedType getGenericComponentType() {
            return (ParameterizedType) constructor.getGenericParameterTypes()[index];
        }
    }
}
