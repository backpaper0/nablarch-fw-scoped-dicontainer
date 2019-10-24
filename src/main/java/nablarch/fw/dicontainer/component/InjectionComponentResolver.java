package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

/**
 * インジェクションされるコンポーネントを解決するメソッドを表すインターフェース。
 *
 */
public interface InjectionComponentResolver {

    /**
     * 依存コンポーネントを解決する。
     * 
     * @param container DIコンテナ
     * @return 解決されたコンポーネント
     */
    Object resolve(Container container);

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param self 自身を含んでいるコンポーネント定義
     */
    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);

    /**
     * 依存関係の循環を検出するためのバリデーションを行う。
     * 
     * @param context 循環依存バリデーションのコンテキスト
     */
    void validateCycleDependency(CycleDependencyValidationContext context);
}
