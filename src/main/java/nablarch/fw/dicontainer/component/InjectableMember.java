package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;

/**
 * インジェクションされるコンストラクタ・メソッド・フィールドを表すインターフェース。
 *
 */
public interface InjectableMember {

    /**
     * インジェクションを行う。
     * 
     * @param container DIコンテナ
     * @param component インジェクション対象のコンポーネント
     * @return コンストラクタ・メソッドの場合は戻り値が返される。フィールドの場合は{@literal null}が返される。
     */
    Object inject(Container container, Object component);

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