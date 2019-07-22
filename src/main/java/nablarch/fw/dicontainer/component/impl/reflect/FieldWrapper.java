package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

public final class FieldWrapper {

    private final Field field;

    public FieldWrapper(final Field field) {
        this.field = Objects.requireNonNull(field);
    }

    public void set(final Object obj, final Object value) {
        if (field.isAccessible() == false) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (final IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (final IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public String toString() {
        return field.getDeclaringClass().getName() + "#" + field.getName();
    }
}
