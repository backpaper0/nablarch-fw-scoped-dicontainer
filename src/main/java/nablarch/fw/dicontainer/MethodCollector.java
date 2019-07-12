package nablarch.fw.dicontainer;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class MethodCollector {

    private final Set<Method> methods = new LinkedHashSet<>();

    public void addInstanceMethodIfNotOverridden(final Method method) {

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

    private static boolean isTarget(final Method method) {
        if (method.isBridge()) {
            return false;
        }
        if (method.isSynthetic()) {
            return false;
        }
        if (Modifier.isStatic(method.getModifiers())) {
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

    public Set<Method> getMethods() {
        return methods;
    }
}
