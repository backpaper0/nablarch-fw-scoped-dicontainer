package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Constructor;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.component.impl.reflect.ConstructorWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

/**
 * {@link InjectableConstructor}のデフォルト実装クラス。
 */
public final class DefaultInjectableConstructor implements InjectableConstructor {

    /** コンストラクタ */
    private final ConstructorWrapper constructor;

    /** コンポーネント解決クラス */
    private final InjectionComponentResolvers resolvers;

    /**
     * コンストラクタ。
     * @param constructor 本クラスで管理するコンストラクタメソッド
     * @param resolvers コンポーネント解決クラス
     */
    public DefaultInjectableConstructor(final Constructor<?> constructor,
            final InjectionComponentResolvers resolvers) {
        this.constructor = new ConstructorWrapper(constructor);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final Container container) {
        final Object[] args = resolvers.resolve(container);
        return constructor.newInstance(args);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        resolvers.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolvers.validateCycleDependency(context);
    }
}
