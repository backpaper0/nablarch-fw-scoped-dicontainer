package nablarch.fw.dicontainer.component.impl.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import nablarch.fw.dicontainer.exception.ReflectionException;

/**
 * {@link Method}のラッパークラス。
 *
 */
public final class MethodWrapper {
    /** ラップ対象のメソッド */
    private final Method method;

    /**
     * コンストラクタ。
     * @param method ラップ対象のメソッド
     */
    public MethodWrapper(final Method method) {
        this.method = Objects.requireNonNull(method);
    }

    /**
     * 与えられたオブジェクトのメソッドを呼び出す。
     * @param obj 対象オブジェクト
     * @param args メソッドの引数
     * @return 呼び出したメソッドの戻り値
     */
    public Object invoke(final Object obj, final Object... args) {
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(obj, args);
        } catch (final IllegalAccessException | IllegalArgumentException e) {
            throw new ReflectionException(e);
        } catch (final InvocationTargetException e) {
            throw new ReflectionException(e.getTargetException());
        }
    }

    /**
     * メソッドがstaticか判定する。
     * @return staticの場合、真
     */
    public boolean isStatic() {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * メソッドの戻り値の型を取得する。
     * @return 戻り値の型
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * メソッド引数の数をカウントする
     * @return 引数の数
     */
    public int getParameterCount() {
        return method.getParameterCount();
    }

    /**
     * メソッド引数の型を取得する
     * @param index 添字
     * @return 引数の型
     */
    public Class<?> getParameterType(final int index) {
        return method.getParameterTypes()[index];
    }

    @Override
    public String toString() {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
