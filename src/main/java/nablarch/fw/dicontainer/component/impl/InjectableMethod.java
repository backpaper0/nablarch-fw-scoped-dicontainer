package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Method;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.StaticInjectionException;

/**
 * インジェクションされるメソッドを表す{@link InjectableMember}実装クラス。
 */
public final class InjectableMethod implements InjectableMember {

    /** メソッド */
    private final MethodWrapper method;

    /** コンポーネント解決クラス */
    private final InjectionComponentResolvers resolvers;

    /**
     * コンストラクタ。
     * @param method メソッド
     * @param resolvers コンポーネント解決クラス
     */
    public InjectableMethod(final Method method, final InjectionComponentResolvers resolvers) {
        this.method = new MethodWrapper(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final Container container, final Object component) {
        final Object[] args = resolvers.resolve(container);
        return method.invoke(component, args);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        if (method.isStatic()) {
            containerBuilder.addError(new StaticInjectionException(
                    "Injection method [" + method + "] must not be static."));
            return;
        }
        resolvers.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolvers.validateCycleDependency(context);
    }
}
