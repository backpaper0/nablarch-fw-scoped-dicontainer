package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

/**
 * コンポーネントの破棄を行うメソッドを表すインターフェース。
 *
 */
public interface DestroyMethod {

    /**
     * メソッドを実行する。
     * 
     * @param component メソッドが実行されるコンポーネント
     */
    void invoke(Object component);

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param self 自身を含んでいるコンポーネント定義
     */
    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
