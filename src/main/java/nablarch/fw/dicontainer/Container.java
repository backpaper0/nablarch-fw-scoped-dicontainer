package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;

import nablarch.core.util.annotation.Published;

/**
 * DIコンテナのインターフェース。
 *
 */
@Published(tag = "architect")
public interface Container {

    /**
     * コンポーネントを取得する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キーとなるクラス
     * @return コンポーネント
     */
    <T> T getComponent(Class<T> key);

    /**
     * コンポーネントを取得する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キーとなるクラス
     * @param qualifiers 限定子
     * @return コンポーネント
     */
    <T> T getComponent(Class<T> key, Annotation... qualifiers);

    //TODO
    //    /**
    //     * コンポーネントを取得する。
    //     * 
    //     * @param <T> コンポーネントの型
    //     * @param key 検索キーとなるクラス
    //     * @return コンポーネントの集合
    //     */
    //    <T> Set<T> getComponents(Class<T> key);

    /**
     * イベントを発火させる。
     * 
     * @param event イベント
     */
    void fire(Object event);

    /**
     * DIコンテナを破棄する。
     * 
     */
    void destroy();
}
