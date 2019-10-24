package nablarch.fw.dicontainer.component.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

/**
 * {@link InjectionComponentResolver}のファーストクラスコレクションクラス。
 */
public final class InjectionComponentResolvers {

    /** {@link InjectionComponentResolver}のリスト */
    private final List<InjectionComponentResolver> resolvers;

    /**
     * コンストラクタ。
     * @param resolvers {@link InjectionComponentResolver}のリスト
     */
    public InjectionComponentResolvers(final List<InjectionComponentResolver> resolvers) {
        this.resolvers = Objects.requireNonNull(resolvers);
    }

    /**
     * 空のリゾルバを持つインスタンスを生成する。
     * @return インスタンス
     */
    public static InjectionComponentResolvers empty() {
        return new InjectionComponentResolvers(Collections.emptyList());
    }

    /**
     * 自身が持つ{@link InjectionComponentResolver}を使ってコンポーネントの解決を行う。
     *
     * @param container DIコンテナ
     * @return 解決されたコンポーネント
     */
    public Object[] resolve(final Container container) {
        return resolvers.stream().map(resolver -> resolver.resolve(container)).toArray();
    }

    /**
     * 自身が持つ{@link InjectionComponentResolver}を使ってバリデーションを行う
     * @param containerBuilder DIコンテナのビルダー
     * @param self コンポーネント定義
     */
    public void validate(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> self) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validate(containerBuilder, self);
        }
    }

    /**
     * 自身が持つ{@link InjectionComponentResolver}を使って依存関係の循環を検出する。
     * @param context 循環依存バリデーションのコンテキスト
     */
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        for (final InjectionComponentResolver resolver : resolvers) {
            resolver.validateCycleDependency(context.createSubContext());
        }
    }
}
