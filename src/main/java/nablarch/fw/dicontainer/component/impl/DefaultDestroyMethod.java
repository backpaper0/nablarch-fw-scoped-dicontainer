package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.LifeCycleMethodSignatureException;

/**
 * {@link DestroyMethod}のデフォルト実装クラス。
 */
public final class DefaultDestroyMethod implements DestroyMethod {

    /** 破棄メソッド */
    private final MethodWrapper method;

    /**
     * コンストラクタ。
     * @param method 破棄メソッド
     */
    public DefaultDestroyMethod(final Method method) {
        this.method = new MethodWrapper(method);
    }

    @Override
    public void invoke(final Object component) {
        method.invoke(component);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        if (method.isStatic()) {
            containerBuilder.addError(new LifeCycleMethodSignatureException(
                    "Destroy method [" + method + "] must not be static."));
            return;
        }

        if (method.getParameterCount() > 0) {
            containerBuilder.addError(new LifeCycleMethodSignatureException(
                    "Destroy method [" + method + "] must no parameter."));
            return;
        }
    }
}
