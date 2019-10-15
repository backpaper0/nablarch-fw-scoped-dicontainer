package nablarch.fw.dicontainer.scope;

import java.util.Optional;

import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.factory.MemberFactory;
import nablarch.fw.dicontainer.container.ContainerBuilder;

/**
 * スコープを決定するクラス。
 *
 */
public interface ScopeDecider {

    /**
     * コンポーネントのクラスが持つアノテーションからスコープを決定する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return スコープ
     */
    Optional<Scope> fromComponentClass(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * スコープをコンポーネント登録する。
     * 
     * @param builder DIコンテナのビルダー
     * @param memberFactory コンポーネント定義の構成要素を生成するファクトリ
     */
    void registerScopes(ContainerBuilder<?> builder,
            MemberFactory memberFactory);

}