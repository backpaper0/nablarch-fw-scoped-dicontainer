package nablarch.fw.dicontainer.component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class MethodCollector {

    private final List<Method> methods = new ArrayList<>();

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

    @Deprecated
    public void addInstanceMethodIfNotOverridden(final Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            return;
        }
        addMethodIfNotOverridden(method);
    }

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

    public List<Method> getMethods() {
        return methods;
    }
}
