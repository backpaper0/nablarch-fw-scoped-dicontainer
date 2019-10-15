package nablarch.fw.dicontainer.component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.annotation.ClassInheritances;

/**
 * メソッドを収集するクラス。
 */
public final class MethodCollector {

    /** 集められたメソッド */
    private final List<Method> methods = new ArrayList<>();

    /**
     * メソッドがオーバーライドメソッドでなければ追加する。
     * @param method メソッド
     */
    public void addMethodIfNotOverridden(final Method method) {

        if (isTarget(method) == false) {
            return;
        }

        if (Modifier.isPrivate(method.getModifiers()) == false) {
            final boolean isPackagePrivate = Modifier.isProtected(method.getModifiers()) == false
                    && Modifier.isPublic(method.getModifiers()) == false;
            for (final Method m : methods) {
                if (isOverridden(method, m, isPackagePrivate)) {
                    return;
                }
            }
        }

        methods.add(method);
    }

    /**
     * 与えられたメソッドが対象であるか判定する。
     * @param method メソッド
     * @return 対象である場合、真
     */
    private static boolean isTarget(final Method method) {
        if (method.isBridge()) {
            return false;
        }
        if (method.isSynthetic()) {
            return false;
        }
        if (Modifier.isAbstract(method.getModifiers())) {
            return false;
        }
        return true;
    }

    /**
     * オーバーライドメソッドであるか判定する。
     * @param self 比較元となるメソッド
     * @param other 比較対象のメソッド
     * @param isPackagePrivate メソッドがパッケージプライベートかどうか
     * @return オーバーライドメソッドの場合、真
     */
    private static boolean isOverridden(final Method self, final Method other,
            final boolean isPackagePrivate) {
        if (self.getDeclaringClass() == other.getDeclaringClass()) {
            return false;
        } else if (Modifier.isPrivate(other.getModifiers())) {
            return false;
        } else if (self.getName().equals(other.getName()) == false) {
            return false;
        } else if (Arrays.equals(self.getParameterTypes(), other.getParameterTypes()) == false) {
            return false;
        } else if (isPackagePrivate && Objects.equals(self.getDeclaringClass().getPackage(),
                other.getDeclaringClass().getPackage()) == false) {
            return false;
        }
        return true;
    }

    /**
     * 収集したメソッドを取得する。
     * @return 収集したメソッド
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * 指定されたクラスをもとにメソッドの収集を行う。
     * @param clazz クラス
     * @return 収集結果（本クラスのインスタンス）
     */
    public static MethodCollector collectFromClass(final Class<?> clazz) {
        final MethodCollector methodCollector = new MethodCollector();
        for (final Class<?> c : new ClassInheritances(clazz)) {
            for (final Method method : c.getDeclaredMethods()) {
                methodCollector.addMethodIfNotOverridden(method);
            }
        }
        return methodCollector;
    }
}
