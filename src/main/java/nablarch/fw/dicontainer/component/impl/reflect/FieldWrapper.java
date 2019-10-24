package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

/**
 * {@link Field}のラッパークラス。
 */
public final class FieldWrapper {
    /** ラップ対象の{@link Field} */
    private final Field field;

    /**
     * コンストラクタ。
     * @param field ラップ対象の{@link Field}
     */
    public FieldWrapper(final Field field) {
        this.field = Objects.requireNonNull(field);
    }

    /**
     * 与えられたオブジェクトのフィールドに値を設定する。
     * @param obj 設定対象のオブジェクト
     * @param value 設定される値
     */
    public void set(final Object obj, final Object value) {
        if (field.isAccessible() == false) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (final IllegalArgumentException | IllegalAccessException e) {
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
