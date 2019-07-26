package nablarch.fw.dicontainer.annotation.auto;

import java.util.Objects;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;

/**
 * アノテーションをもとに自動でコンポーネントを登録してDIコンテナを構築するファクトリ。
 *
 */
public final class AnnotationAutoContainerFactory {

    /**
     * DIコンテナのビルダー
     */
    private final AnnotationContainerBuilder containerBuilder;
    /**
     * ディレクトリトラバーサルの設定
     */
    private final Iterable<TraversalConfig> traversalConfigs;
    /**
     * コンポーネントとみなすための条件
     */
    private final ComponentPredicate predicate;

    /**
     * インスタンスを生成する。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param traversalConfigs ディレクトリトラバーサルの設定
     * @param predicate コンポーネントとみなすための条件
     */
    public AnnotationAutoContainerFactory(final AnnotationContainerBuilder containerBuilder,
            final Iterable<TraversalConfig> traversalConfigs, final ComponentPredicate predicate) {
        this.containerBuilder = Objects.requireNonNull(containerBuilder);
        this.traversalConfigs = Objects.requireNonNull(traversalConfigs);
        this.predicate = Objects.requireNonNull(predicate);
    }

    /**
     * コンポーネントを自動登録してDIコンテナを構築する。
     * 
     * @return DIコンテナ
     */
    public Container create() {
        for (final TraversalConfig traversalConfig : traversalConfigs) {
            final ClassLoader classLoader = traversalConfig.getClass().getClassLoader();
            final Class<?> baseClass = traversalConfig.getClass();
            final ClassFilter classFilter = ClassFilter.valueOf(traversalConfig);
            final ClassTraverser classTraverser = new ClassTraverser(classLoader, baseClass,
                    classFilter);
            classTraverser.traverse(clazz -> {
                if (predicate.test(clazz)) {
                    containerBuilder.register(clazz);
                }
            });
        }
        return containerBuilder.build();
    }
}
