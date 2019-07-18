package nablarch.fw.dicontainer.component;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Qualifier;

import nablarch.fw.dicontainer.exception.QualifierAnnotationException;

public final class ComponentKey<T> implements Serializable {

    private final Class<T> componentType;
    private final Set<Annotation> qualifiers;

    public ComponentKey(final Class<T> componentType, final Annotation... qualifiers) {
        this(componentType, Arrays.stream(qualifiers).collect(Collectors.toSet()));
    }

    public ComponentKey(final Class<T> componentType, final Set<Annotation> qualifiers) {
        this.componentType = Objects.requireNonNull(componentType);
        this.qualifiers = Objects.requireNonNull(qualifiers);

        this.qualifiers.forEach(a -> {
            if (a.annotationType().isAnnotationPresent(Qualifier.class) == false) {
                throw new QualifierAnnotationException();
            }
        });
    }

    public static <T> ComponentKey<T> fromClass(final Class<T> componentType) {
        //FIXME factoryクラスを作って移動したい
        final Set<Annotation> qualifiers = Arrays.stream(componentType.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toSet());

        return new ComponentKey<>(componentType, qualifiers);
    }

    public static ComponentKey<?> fromFactoryMethod(final Method factoryMethod) {

        final Set<Annotation> qualifiers = Arrays.stream(factoryMethod.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toSet());

        return new ComponentKey<>(factoryMethod.getReturnType(), qualifiers);
    }

    public Set<AliasKey> aliasKeys() {
        final Set<Class<?>> classes = new HashSet<>();
        collectAlias(classes, componentType);
        return classes.stream().map(a -> new AliasKey(a, qualifiers)).collect(Collectors.toSet());
    }

    public AliasKey asAliasKey() {
        return new AliasKey(componentType, qualifiers);
    }

    public String getFullyQualifiedClassName() {
        return componentType.getName();
    }

    private void collectAlias(final Set<Class<?>> classes, final Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return;
        }
        if (clazz != componentType) {
            classes.add(clazz);
        }
        collectAlias(classes, clazz.getSuperclass());
        for (final Class<?> i : clazz.getInterfaces()) {
            collectAlias(classes, i);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentType, qualifiers);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponentKey<?> other = (ComponentKey<?>) obj;
        return componentType.equals(other.componentType)
                && qualifiers.equals(other.qualifiers);
    }

    public static final class AliasKey {

        private final Class<?> aliasType;
        private final Set<Annotation> qualifiers;

        private AliasKey(final Class<?> aliasType, final Set<Annotation> qualifiers) {
            this.aliasType = Objects.requireNonNull(aliasType);
            this.qualifiers = Objects.requireNonNull(qualifiers);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aliasType, qualifiers);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (getClass() != obj.getClass()) {
                return false;
            }
            final AliasKey other = (AliasKey) obj;
            return aliasType.equals(other.aliasType)
                    && qualifiers.equals(other.qualifiers);
        }

        @Override
        public String toString() {
            return String.format("%s%s", aliasType, qualifiers);
        }
    }
}
