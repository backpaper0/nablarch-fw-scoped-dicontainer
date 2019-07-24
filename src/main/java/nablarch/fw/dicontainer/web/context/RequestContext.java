package nablarch.fw.dicontainer.web.context;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.ComponentId;

/**
 * リクエストコンテキスト。
 *
 */
public interface RequestContext {

    /**
     * コンテキストが持つコンポーネントを取得する。
     * コンポーネントがない場合はプロバイダから取得する。
     * 一度取得したコンポーネントはリクエストが完了するまでキャッシュされる。
     * 
     * @param id ID
     * @param provider コンポーネントのプロバイダ
     * @return コンポーネント
     */
    <T> T getRequestComponent(final ComponentId id, final Provider<T> provider);
}
