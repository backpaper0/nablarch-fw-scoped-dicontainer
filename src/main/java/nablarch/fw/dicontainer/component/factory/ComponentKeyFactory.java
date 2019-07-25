package nablarch.fw.dicontainer.component.factory;

import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.ComponentKey;

/**
 * 検索キーを生成するファクトリ。
 *
 */
public interface ComponentKeyFactory {

    /**
     * コンポーネントのクラスをもとに検索キーを生成する。
     * 
     * @param componentType コンポーネントのクラス
     * @return 検索キー
     */
    <T> ComponentKey<T> fromComponentClass(Class<T> componentType);

    /**
     * ファクトリメソッドをもとに検索キーを生成する。
     * 
     * @param factoryMethod ファクトリメソッド
     * @return 検索キー
     */
    ComponentKey<?> fromFactoryMethod(Method factoryMethod);

}