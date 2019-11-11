package nablarch.fw.dicontainer.component.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import javax.inject.Provider;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.InjectionComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectionComponentNotFoundException;
import nablarch.fw.dicontainer.exception.InvalidInjectionScopeException;

/**
 * {@link InjectionComponentResolver}のデフォルト実装クラス。
 */
public final class DefaultInjectionComponentResolver implements InjectionComponentResolver {

    /** コンポーネントの検索キー */
    private final ComponentKey<?> key;

    /** {@link Provider}を使用するかどうか */
    private final boolean provider;


    private final Member source;

    /**
     * コンストラクタ。
     * @param source メンバー
     * @param key コンポーネント検索キー
     * @param provider {@link Provider}を使用するかどうか
     */
    public DefaultInjectionComponentResolver(final Member source, final ComponentKey<?> key,
            final boolean provider) {
        this.source = source;
        this.key = Objects.requireNonNull(key);
        this.provider = provider;
    }

    @Override
    public Object resolve(final Container container) {
        if (provider) {
            return new Provider<Object>() {
                @Override
                public Object get() {
                    return container.getComponent(key);
                }
            };
        }
        return container.getComponent(key);
    }

    @Override
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        final Set<ComponentDefinition<?>> definitions = containerBuilder
                .findComponentDefinitions(key);
        if (definitions.isEmpty()) {
            String sourceName = sourceName();
            containerBuilder.addError(new InjectionComponentNotFoundException(
                    "Injection component not found at " + sourceName + ": key=" + key));
        } else if (definitions.size() > 1) {
            containerBuilder.addError(new InjectionComponentDuplicatedException(
                    "key=" + key + ", definitions=" + definitions));
        } else if (provider == false) {
            final ComponentDefinition<?> injected = definitions.iterator().next();
            if (self.isNarrowScope(injected) == false) {
                containerBuilder.addError(new InvalidInjectionScopeException(
                        "[" + self + "] must be narrow scope than [" + injected + "] (Or wrap ["
                                + injected + "] with Provider)."));
            } else {
                containerBuilder.validateCycleDependency(key, self);
            }
        }
    }

    private String sourceName() {
        Class<?> declaringClass = source.getDeclaringClass();
        String className = declaringClass.getName();
        String memberName;
        if (source instanceof Constructor) {
            // ConstructorのgetNameはFQCNを返すため、クラス名を取得
            memberName = declaringClass.getSimpleName();
        } else {
            memberName = source.getName();
        }
        return className + "." + memberName;
    }

    @Override
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        if (provider == false) {
            context.validateCycleDependency(key);
        }
    }
}
