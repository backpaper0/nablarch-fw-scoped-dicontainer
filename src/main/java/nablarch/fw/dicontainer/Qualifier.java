package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Qualifier {

    private final String name;
    private final Map<String, Object> elements;

    public Qualifier(final String name, final Map<String, Object> elements) {
        this.name = Objects.requireNonNull(name);
        this.elements = Objects.requireNonNull(elements);
    }

    public static Qualifier fromAnnotation(final Annotation annotation) {
        if (annotation.annotationType()
                .isAnnotationPresent(javax.inject.Qualifier.class) == false) {
            throw new IllegalArgumentException();
        }
        final String name = annotation.annotationType().getName();
        final Map<String, Object> elements = new HashMap<>();
        for (final Method method : annotation.annotationType().getDeclaredMethods()) {
            try {
                elements.put(method.getName(), method.invoke(annotation));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return new Qualifier(name, elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, elements);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != getClass()) {
            return false;
        }
        final Qualifier other = (Qualifier) obj;
        return name.equals(other.name) && elements.equals(other.elements);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", name, elements);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String name;
        private final Map<String, Object> elements = new HashMap<>();

        private Builder() {
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder element(final String key, final Object value) {
            this.elements.put(key, value);
            return this;
        }

        public Qualifier build() {
            return new Qualifier(name, elements);
        }
    }
}
