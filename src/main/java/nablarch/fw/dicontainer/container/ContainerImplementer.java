package nablarch.fw.dicontainer.container;

import nablarch.core.util.annotation.Published;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.ComponentKey;

/**
 * 内部のAPIから呼び出すためDIコンテナを拡張するインターフェース。
 *
 */
@Published(tag = "architect")
public interface ContainerImplementer extends Container {

    /**
     * コンポーネントを取得する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キー
     * @return コンポーネント
     */
    <T> T getComponent(ComponentKey<T> key);

    /**
     * コンポーネントを取得する。
     * 
     * @param <T> コンポーネントの型
     * @param id ID
     * @return コンポーネント
     */
    <T> T getComponent(ComponentId id);

    /**
     * コンポーネント定義を取得する。
     * 
     * @param <T> コンポーネントの型
     * @param id ID
     * @return コンポーネント定義
     */
    <T> ComponentDefinition<T> getComponentDefinition(ComponentId id);
}
