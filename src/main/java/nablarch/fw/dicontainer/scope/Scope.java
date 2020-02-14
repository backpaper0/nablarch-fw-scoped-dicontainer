package nablarch.fw.dicontainer.scope;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;

/**
 * スコープのインターフェース。
 *
 */
public interface Scope {

    /**
     * コンポーネントを取得する。
     * 
     * @param id ID
     * @param provider コンポーネントをインスタンス化するためのプロバイダ
     * @return コンポーネント
     */
    <T> T getComponent(ComponentId id, Provider<T> provider);

    /**
     * コンポーネント定義を登録する。
     * 
     * @param definition コンポーネント定義
     */
    <T> void register(ComponentDefinition<T> definition);

    /**
     * スコープの広さを表す値を返す。
     * 
     * <p>この値はDIコンテナ構築時のバリデーションで利用される。
     * コンポーネントは自分よりも広いスコープを持つコンポーネントに対してインジェクションできない。</p>
     * 
     * <p>例えばシングルトンはプロトタイプよりもスコープが広いので次のようなインジェクションは不正とみなされる。</p>
     * <pre>
     * &#64;Prototype
     * class Foo {}
     * 
     * &#64;Singleton
     * class Bar {
     *     &#64;Inject
     *     Foo foo;
     * }
     * </pre>
     * 
     * @return スコープの広さを表す値
     */
    int dimensions();
}
