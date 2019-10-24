package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

/**
 * {@link Constructor}のラッパークラス。
 */
public final class ConstructorWrapper {
    /** ラップ対象のコンストラクタ */
    private final Constructor<?> constructor;

    /**
     * 本クラスのコンストラクタ。
     * @param constructor ラップ対象のコンストラクタ
     */
    public ConstructorWrapper(final Constructor<?> constructor) {
        this.constructor = Objects.requireNonNull(constructor);
    }

    /**
     * 与えられたコンストラクタ引数を使ってインスタンスを生成する。
     * @param initargs 引数
     * @return インスタンス
     */
    public Object newInstance(final Object... initargs) {
        if (constructor.isAccessible() == false) {
            constructor.setAccessible(true);
        }
        try {
            return constructor.newInstance(initargs);
        } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (final InvocationTargetException e) {
            throw new ReflectionException(e.getTargetException());
        }
    }
}
