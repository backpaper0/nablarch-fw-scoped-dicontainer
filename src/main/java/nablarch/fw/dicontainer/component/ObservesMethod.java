package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.container.ContainerBuilder;

/**
 * イベントのハンドリングを行うメソッドを表すインターフェース。
 *
 */
public interface ObservesMethod {

    /**
     * イベントがハンドリング対象かどうかを返す。
     * 
     * @param event イベント
     * @return ハンドリング対象の場合は{@literal true}。
     */
    boolean isTarget(Object event);

    /**
     * メソッドを実行する。
     * 
     * @param component メソッドが実行されるコンポーネント
     * @param event イベント
     */
    void invoke(Object component, Object event);

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param self 自身を含んでいるコンポーネント定義
     */
    void validate(ContainerBuilder<?> containerBuilder, ComponentDefinition<?> self);
}
