package nablarch.fw.dicontainer.annotation.auto;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.inject.Qualifier;
import javax.inject.Scope;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.annotation.AnnotationSet;
import nablarch.fw.dicontainer.scope.ScopeDecider;

/**
 * アノテーションをもとに自動でコンポーネントを登録してDIコンテナを構築するファクトリ。
 *
 */
public final class AnnotationAutoContainerFactory {

    /**
     * コンポーネントとみなす条件となるアノテーションセット
     */
    private final AnnotationSet targetAnnotations;
    /**
     * ディレクトリトラバーサルの設定
     */
    private final Iterable<TraversalConfig> traversalConfigs;
    /**
     * イーガーロードする場合は{@literal true}
     */
    private final boolean eagerLoad;
    /**
     * スコープを決定するクラス
     */
    private final ScopeDecider scopeDecider;

    /**
     * インスタンスを生成する。
     * 
     * @param targetAnnotations コンポーネントとみなす条件となるアノテーションセット
     * @param traversalConfigs ディレクトリトラバーサルの設定
     * @param eagerLoad イーガーロードする場合は{@literal true}
     * @param scopeDecider スコープを決定するクラス
     */
    private AnnotationAutoContainerFactory(final AnnotationSet targetAnnotations,
            final Iterable<TraversalConfig> traversalConfigs, final boolean eagerLoad,
            final ScopeDecider scopeDecider) {
        this.targetAnnotations = Objects.requireNonNull(targetAnnotations);
        this.traversalConfigs = Objects.requireNonNull(traversalConfigs);
        this.eagerLoad = eagerLoad;
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
    }

    /**
     * コンポーネントを自動登録してDIコンテナを構築する。
     * 
     * @return DIコンテナ
     */
    public Container create() {
        final AnnotationContainerBuilder builder = AnnotationContainerBuilder.builder()
                .scopeDecider(scopeDecider)
                .eagerLoad(eagerLoad).build();
        for (final TraversalConfig traversalConfig : traversalConfigs) {
            final ClassLoader classLoader = traversalConfig.getClass().getClassLoader();
            final Class<?> baseClass = traversalConfig.getClass();
            final ClassFilter classFilter = ClassFilter.valueOf(traversalConfig);
            final ClassTraverser classTraverser = new ClassTraverser(classLoader, baseClass,
                    classFilter);
            classTraverser.traverse(clazz -> {
                if (isTarget(clazz)) {
                    builder.register(clazz);
                }
            });
        }
        return builder.build();
    }

    private boolean isTarget(final Class<?> clazz) {
        for (final Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (targetAnnotations.isAnnotationPresent(annotationType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * デフォルトの設定をしたインスタンスを構築する。
     * 
     * @return インスタンス
     */
    public static AnnotationAutoContainerFactory createDefault() {
        return builder().build();
    }

    /**
     * ビルダーを生成する。
     * 
     * @return ビルダー
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * ビルダー。
     *
     */
    public static final class Builder {

        /**
         * コンポーネントとみなす条件となるアノテーションセット
         */
        private AnnotationSet targetAnnotations = new AnnotationSet(Scope.class, Qualifier.class);
        /**
         * ディレクトリトラバーサルの設定
         */
        private Iterable<TraversalConfig> traversalConfigs;
        /**
         * イーガーロードする場合は{@literal true}
         */
        private boolean eagerLoad;
        /**
         * スコープを決定するクラス
         */
        private ScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();

        /**
         * インスタンスを生成する。
         * 
         */
        private Builder() {
        }

        /**
         * コンポーネントとみなす条件となるアノテーションセットを設定する。
         * 
         * @param annotations コンポーネントとみなす条件となるアノテーションセット
         * @return このビルダー自身
         */
        @SafeVarargs
        public final Builder targetAnnotations(final Class<? extends Annotation>... annotations) {
            this.targetAnnotations = new AnnotationSet(annotations);
            return this;
        }

        /**
         * ディレクトリトラバーサルの設定を設定する。
         * 
         * @param traversalConfigs ディレクトリトラバーサルの設定
         * @return このビルダー自身
         */
        public Builder traversalConfigs(final Iterable<TraversalConfig> traversalConfigs) {
            this.traversalConfigs = traversalConfigs;
            return this;
        }

        /**
         * イーガーロードをするかどうかを設定する。
         * 
         * @param eagerLoad イーガーロードする場合は{@literal true}
         * @return このビルダー自身
         */
        public Builder eagerLoad(final boolean eagerLoad) {
            this.eagerLoad = eagerLoad;
            return this;
        }

        /**
         * スコープを決定するクラスを設定する。
         * 
         * @param scopeDecider スコープを決定するクラス
         * @return このビルダー自身
         */
        public Builder scopeDecider(final ScopeDecider scopeDecider) {
            this.scopeDecider = scopeDecider;
            return this;
        }

        /**
         * インスタンスを構築する。
         * 
         * @return 構築されたインスタンス
         */
        public AnnotationAutoContainerFactory build() {
            return new AnnotationAutoContainerFactory(targetAnnotations, traversalConfigs,
                    eagerLoad, scopeDecider);
        }
    }
}
