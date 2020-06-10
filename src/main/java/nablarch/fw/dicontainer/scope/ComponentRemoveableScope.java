package nablarch.fw.dicontainer.scope;

import nablarch.fw.dicontainer.component.ComponentId;

public interface ComponentRemoveableScope extends Scope {

    /**
     * コンポーネントを削除する。
     *
     * @param id ID
     * @param <T> 削除されてコンテナ管理対象外となったコンポーネントのインスタンス
     */
    <T> T removeComponent(ComponentId id);
}
