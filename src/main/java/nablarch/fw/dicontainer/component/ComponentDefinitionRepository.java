package nablarch.fw.dicontainer.component;

import java.util.HashMap;
import java.util.Map;

import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

/**
 * コンポーネント定義のリポジトリ。
 *
 */
public final class ComponentDefinitionRepository {

    /**
     * IDとコンポーネント定義のマッピング
     */
    private final Map<ComponentId, ComponentDefinition<?>> idToDefinition = new HashMap<>();
    /**
     * 検索キーとIDのマッピング
     */
    private final Map<ComponentKey<?>, ComponentId> keyToId = new HashMap<>();

    /**
     * コンポーネント定義を登録する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キー
     * @param definition コンポーネント定義
     */
    public <T> void register(final ComponentKey<T> key, final ComponentDefinition<T> definition) {
        final ComponentId id = definition.getId();
        idToDefinition.put(id, definition);
        keyToId.put(key, id);
    }

    /**
     * コンポーネント定義を取得する。
     * 
     * @param <T> コンポーネントの方
     * @param id ID
     * @return コンポーネント定義
     */
    public <T> ComponentDefinition<T> get(final ComponentId id) {
        final ComponentDefinition<?> definition = idToDefinition.get(id);
        if (definition == null) {
            throw new ComponentNotFoundException("id = " + id);
        }
        return (ComponentDefinition<T>) definition;
    }

    /**
     * コンポーネント定義を取得する。
     * 
     * @param <T> コンポーネントの方
     * @param key 検索キー
     * @return コンポーネント定義
     */
    public <T> ComponentDefinition<T> find(final ComponentKey<T> key) {
        final ComponentId id = keyToId.get(key);
        final ComponentDefinition<?> definition = idToDefinition.get(id);
        if (definition == null) {
            return null;
        }
        return (ComponentDefinition<T>) definition;
    }

    /**
     * イベントを発火させる。
     * 
     * @param container DIコンテナ
     * @param event イベント
     */
    public void fire(final ContainerImplementer container, final Object event) {
        for (final ComponentDefinition<?> definition : idToDefinition.values()) {
            definition.fire(container, event);
        }
    }

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     */
    public void validate(final ContainerBuilder<?> containerBuilder) {
        for (final ComponentDefinition<?> definition : idToDefinition.values()) {
            definition.validate(containerBuilder);
        }
    }
}
