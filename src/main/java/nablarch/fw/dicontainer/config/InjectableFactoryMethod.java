package nablarch.fw.dicontainer.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.config.ContainerBuilder.CycleDependencyValidationContext;

public class InjectableFactoryMethod implements InjectableMember {

    private final Class<?> componentType;
    private final Method method;
    private final List<InjectionComponentResolver> resolvers;

    public InjectableFactoryMethod(final Class<?> componentType, final Method method, final List<InjectionComponentResolver> resolvers) {
        this.componentType = Objects.requireNonNull(componentType);
        this.method = Objects.requireNonNull(method);
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    @Override
    public Object inject(final Container container, final Object component) {
        //componentTypeは実装クラスなのでQualifierが付与されていたとしてもコンポーネントを検索できる。
        //ただしFactoryのFactoryには対応できない。
        final Object factoryComponent = container.getComponent(componentType);
        final Object[] args = resolvers.stream().map(resolver -> resolver.resolve(container))
                .toArray();
        if (method.isAccessible() == false) {
            method.setAccessible(true);
        }
        try {
            return method.invoke(factoryComponent, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder, final ComponentDefinition<?> self) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validate(containerBuilder, self);
        }
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validateCycleDependency(context);
        }
    }
}
