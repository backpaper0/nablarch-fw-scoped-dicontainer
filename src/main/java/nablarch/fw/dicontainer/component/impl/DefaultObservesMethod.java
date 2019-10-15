package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.ObserverMethodSignatureException;

/**
 * {@link ObservesMethod}のデフォルト実装クラス。
 */
public final class DefaultObservesMethod implements ObservesMethod {

    /** メソッド */
    private final MethodWrapper method;

    /**
     * コンストラクタ。
     * @param method メソッド
     */
    public DefaultObservesMethod(final Method method) {
        this.method = new MethodWrapper(method);
    }

    @Override
    public boolean isTarget(final Object event) {
        return method.getParameterType(0).isAssignableFrom(event.getClass());
    }

    @Override
    public void invoke(final Object component, final Object event) {
        method.invoke(component, event);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        if (method.isStatic()) {
            containerBuilder.addError(new ObserverMethodSignatureException(
                    "Observes method [" + method + "] must not be static."));
            return;
        }

        if (method.getParameterCount() != 1) {
            containerBuilder.addError(new ObserverMethodSignatureException(
                    "Observes method [" + method + "] must have one parameter."));
            return;
        }
    }
}
