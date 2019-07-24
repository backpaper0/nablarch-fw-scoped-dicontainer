package nablarch.fw.dicontainer.web.context;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentId;

/**
 * セッションコンテキスト
 *
 */
public interface SessionContext {

    /**
     * コンテキストが持つコンポーネントを取得する。
     * コンポーネントがない場合はプロバイダから取得する。
     * 一度取得したコンポーネントはセッションが破棄されるまでキャッシュされる。
     * 
     * @param id ID
     * @param provider コンポーネントのプロバイダ
     * @return コンポーネント
     */
    <T> T getSessionComponent(final ComponentId id, final Provider<T> provider);
}
