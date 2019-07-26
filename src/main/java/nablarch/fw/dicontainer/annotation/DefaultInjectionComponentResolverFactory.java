package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
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
import javax.inject.Qualifier;

import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.factory.InjectionComponentResolverFactory;
import nablarch.fw.dicontainer.component.impl.DefaultInjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;

/**
 * 依存コンポーネントのリゾルバを生成するファクトリ。
 *
 */
public final class DefaultInjectionComponentResolverFactory
        implements InjectionComponentResolverFactory {

    @Override
    public InjectionComponentResolver fromField(final Field field) {
        return new FieldSource(field).create();
    }

    @Override
    public InjectionComponentResolvers fromMethodParameters(final Method method) {
        return fromExecutable(method);
    }

    @Override
    public InjectionComponentResolvers fromConstructorParameters(
            final Constructor<?> constructor) {
        return fromExecutable(constructor);
    }

    private InjectionComponentResolvers fromExecutable(
            final Executable executable) {
        final List<InjectionComponentResolver> resolvers = IntStream
                .range(0, executable.getParameterCount())
                .mapToObj(i -> new ExecutableSource(executable, i))
                .map(Source::create)
                .collect(Collectors.toList());
        return new InjectionComponentResolvers(resolvers);
    }

    private abstract class Source {

        protected abstract Annotation[] getAnnotations();

        protected abstract Class<?> getComponentType();

        protected abstract ParameterizedType getGenericComponentType();

        public InjectionComponentResolver create() {
            final Set<Annotation> qualifiers = Arrays.stream(getAnnotations())
                    .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                    .collect(Collectors.toSet());

            final boolean provider = getComponentType().equals(Provider.class);
            final Class<?> componentType;
            if (provider) {
                componentType = (Class<?>) getGenericComponentType().getActualTypeArguments()[0];
            } else {
                componentType = getComponentType();
            }

            final ComponentKey<?> key = new ComponentKey<>(componentType, qualifiers);
            return new DefaultInjectionComponentResolver(key, provider);
        }
    }

    private final class FieldSource extends Source {

        private final Field field;

        public FieldSource(final Field field) {
            this.field = Objects.requireNonNull(field);
        }

        @Override
        protected Annotation[] getAnnotations() {
            return field.getAnnotations();
        }

        @Override
        protected Class<?> getComponentType() {
            return field.getType();
        }

        @Override
        protected ParameterizedType getGenericComponentType() {
            return (ParameterizedType) field.getGenericType();
        }
    }

    private final class ExecutableSource extends Source {

        private final Executable executable;
        private final int index;

        public ExecutableSource(final Executable executable, final int index) {
            this.executable = Objects.requireNonNull(executable);
            this.index = index;
        }

        @Override
        protected Annotation[] getAnnotations() {
            return executable.getParameterAnnotations()[index];
        }

        @Override
        protected Class<?> getComponentType() {
            return executable.getParameterTypes()[index];
        }

        @Override
        protected ParameterizedType getGenericComponentType() {
            return (ParameterizedType) executable.getGenericParameterTypes()[index];
        }
    }
}
