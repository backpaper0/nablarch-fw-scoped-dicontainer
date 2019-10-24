package nablarch.fw.dicontainer.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Provider;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.Observes;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.event.ContainerCreated;
import nablarch.fw.dicontainer.event.ContainerDestroy;

/**
 * シングルトンスコープ。
 *
 */
public final class SingletonScope extends AbstractScope {

    /**
     * IDとインスタンスホルダーのマッピング
     */
    private final ConcurrentMap<ComponentId, InstanceHolder> instances = new ConcurrentHashMap<>();
    /**
     * イーガーロードをする場合は{@literal true}
     */
    private final boolean eagerLoad;
    /**
     * DIコンテナ
     */
    @Inject
    private Container container;

    /**
     * インスタンスを生成する。
     * イーガーロードはしない。
     */
    public SingletonScope() {
        this(false);
    }

    /**
     * インスタンスを生成する。
     * 
     * @param eagerLoad イーガーロードをする場合は{@literal true}
     */
    public SingletonScope(final boolean eagerLoad) {
        this.eagerLoad = eagerLoad;
    }

    @Override
    public <T> T getComponent(final ComponentId id, final Provider<T> provider) {
        InstanceHolder instanceHolder = instances.get(id);
        if (instanceHolder == null) {
            instanceHolder = new InstanceHolder();
            final InstanceHolder previous = instances.putIfAbsent(id, instanceHolder);
            if (previous != null && instanceHolder != previous) {
                instanceHolder = previous;     // getからputIfAbsentまでの間に競合が発生した場合、先にputされたvalueを使う
            }
        }
        return instanceHolder.get(provider);
    }

    /**
     * イーガーロードをする場合、すべてのシングルトンコンポーネントを初期化する。
     * 
     * @param event DIコンテナの初期化イベント
     */
    @Observes
    public void init(final ContainerCreated event) {
        if (eagerLoad) {
            idToDefinition.forEach((id, definition) -> {
                definition.getComponent(container);
            });
        }
    }

    /**
     * すべてのシングルトンコンポーネントを破棄する。
     * 
     * @param event DIコンテナの破棄イベント
     */
    @Observes
    public void destroy(final ContainerDestroy event) {
        idToDefinition.forEach((id, definition) -> {
            final InstanceHolder holder = instances.get(id);
            if (holder != null) {
                holder.destroy((ComponentDefinition<Object>) definition);
            }
        });
    }

    @Override
    public int dimensions() {
        return Integer.MAX_VALUE;
    }

    /**
     * コンポーネントのインスタンスを保持するクラス。
     *
     */
    private static class InstanceHolder {

        Object instance;
        final Lock lock = new ReentrantLock();

        void destroy(final ComponentDefinition<Object> definition) {
            lock.lock();
            try {
                if (instance != null) {
                    definition.destroyComponent(instance);
                }
            } finally {
                lock.unlock();
            }
        }

        <T> T get(final Provider<T> provider) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = provider.get();
                }
                return (T) instance;
            } finally {
                lock.unlock();
            }
        }
    }
}
