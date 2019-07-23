package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

/**
 * コンポーネントを生成するファクトリーメソッドを表すインターフェース。
 *
 */
public interface FactoryMethod {

    /**
     * ファクトリーメソッドを適用してコンポーネント定義を登録する。
     * 
     * @param containerBuilder DIコンテナのビルダー
     */
    void apply(ContainerBuilder<?> containerBuilder);

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param self 自身を含んでいるコンポーネント定義
     */
    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
