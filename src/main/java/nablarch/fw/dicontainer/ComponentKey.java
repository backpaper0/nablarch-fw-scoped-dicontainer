package nablarch.fw.dicontainer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ComponentKey<T> implements Serializable {

    private final Class<T> componentType;
    private final Set<Qualifier> qualifiers;

    public ComponentKey(final Class<T> componentType, final Set<Qualifier> qualifiers) {
        this.componentType = Objects.requireNonNull(componentType);
        this.qualifiers = Objects.requireNonNull(qualifiers);
    }

    public static <T> ComponentKey<T> fromClass(final Class<T> componentType) {

        final Set<Qualifier> qualifiers = Arrays.stream(componentType.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(javax.inject.Qualifier.class))
                .map(Qualifier::fromAnnotation)
                .collect(Collectors.toSet());

        return new ComponentKey<>(componentType, qualifiers);
    }

    public Set<AliasKey> aliasKeys() {
        final Set<Class<?>> classes = new LinkedHashSet<>();
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
        private final Set<Qualifier> qualifiers;

        public AliasKey(final Class<?> aliasType, final Set<Qualifier> qualifiers) {
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
