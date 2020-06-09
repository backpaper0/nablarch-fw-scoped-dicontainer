package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;
import java.util.Set;

import nablarch.fw.dicontainer.component.ComponentKey;

/**
 * DIコンテナのインターフェース。
 *
 */
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

    /**
     * コンポーネントを取得する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キーとなるクラス
     * @return コンポーネントの集合
     */
    <T> Set<T> getComponents(Class<T> key);

    /**
     * DIコンテナを破棄する。
     * 
     */
    void destroy();

    /**
     * コンポーネントを取得する。
     *
     * @param <T> コンポーネントの型
     * @param key 検索キー
     * @return コンポーネント
     */
    <T> T getComponent(ComponentKey<T> key);

    /**
     * コンポーネントを削除する。
     *
     * @param <T> コンポーネントの型
     * @param key 検索キーとなるクラス
     * @return コンポーネント
     */
    <T> T removeComponent(Class<T> key);

    /**
     * コンポーネントを削除する。
     *
     * @param <T> コンポーネントの型
     * @param key 検索キーとなるクラス
     * @param qualifiers 限定子
     * @return コンポーネント
     */
    <T> T removeComponent(Class<T> key, Annotation... qualifiers);

    /**
     * コンポーネントを削除する。
     *
     * @param <T> コンポーネントの型
     * @param key 検索キー
     * @return コンポーネント
     */
    <T> T removeComponent(ComponentKey<T> key);
}
