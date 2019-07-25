package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nablarch.fw.dicontainer.annotation.AnnotationComponentKeyFactory;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.container.ContainerImplementer;

public final class TinyContainer implements ContainerImplementer {

    private final Map<ComponentKey<?>, Object> components = new HashMap<>();
    private final AnnotationComponentKeyFactory componentKeyFactory = AnnotationComponentKeyFactory
            .createDefault();

    public TinyContainer(final Class<?>... componentTypes) {
        for (final Class<?> componentType : componentTypes) {
            register(componentType);
        }
    }

    public TinyContainer register(final Class<?> componentType) {
        try {
            final ComponentKey<?> key = componentKeyFactory.fromComponentClass(componentType);
            final Constructor<?> constructor = componentType.getDeclaredConstructor();
            if (constructor.isAccessible() == false) {
                constructor.setAccessible(true);
            }
            final Object value = constructor.newInstance();
            return register(key, value);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public TinyContainer register(final ComponentKey<?> key, final Object value) {
        components.put(key, value);
        return this;
    }

    @Override
    public <T> T getComponent(final ComponentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> ComponentDefinition<T> getComponentDefinition(final ComponentId id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getComponent(final ComponentKey<T> key) {
        return (T) components.get(key);
    }

    @Override
    public <T> T getComponent(final Class<T> key) {
        return getComponent(componentKeyFactory.fromComponentClass(key));
    }

    @Override
    public <T> T getComponent(final Class<T> key, final Annotation... qualifiers) {
        return getComponent(new ComponentKey<>(key, qualifiers));
    }

    @Override
    public <T> Set<T> getComponents(final Class<T> key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }
}
