package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Field;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.reflect.FieldWrapper;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.StaticInjectionException;

/**
 * インジェクションされるフィールドを表す{@link InjectableMember}実装クラス。
 */
public final class InjectableField implements InjectableMember {
    /** フィールド */
    private final FieldWrapper field;

    /** コンポーネント解決クラス */
    private final InjectionComponentResolver resolver;

    /**
     * コンストラクタ。
     * @param field フィールド
     * @param resolver コンポーネント解決クラス
     */
    public InjectableField(final Field field,
            final InjectionComponentResolver resolver) {
        this.field = new FieldWrapper(field);
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public Object inject(final Container container, final Object component) {
        final Object value = resolver.resolve(container);
        field.set(component, value);
        return null;
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        if (field.isStatic()) {
            containerBuilder.addError(new StaticInjectionException(
                    "Injection field [" + field + "] must not be static."));
            return;
        }
        resolver.validate(containerBuilder, self);
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        resolver.validateCycleDependency(context);
    }
}
