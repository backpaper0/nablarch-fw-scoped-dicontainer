package nablarch.fw.dicontainer.exception;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StaticInjectionException extends ContainerException {

    public StaticInjectionException(final Class<?> componentType, final Field field) {
        // TODO Auto-generated constructor stub
    }

    public StaticInjectionException(final Class<?> componentType, final Method method) {
        // TODO Auto-generated constructor stub
    }
}
