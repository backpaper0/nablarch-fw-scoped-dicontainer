package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.InvalidComponentException;

/**
 * アノテーションをもとにDIコンテナを構築するビルダー。
 *
 */
public final class AnnotationContainerBuilder extends ContainerBuilder<AnnotationContainerBuilder> {

    /**
     * 検索キーのファクトリ
     */
    private final AnnotationComponentKeyFactory componentKeyFactory;
    /**
     * スコープを決定するクラス
     */
    private final AnnotationScopeDecider scopeDecider;
    /**
     * コンポーネント定義の構成要素ファクトリ
     */
    private final AnnotationMemberFactory memberFactory;
    /**
     * コンポーネント定義のファクトリ
     */
    private final AnnotationComponentDefinitionFactory componentDefinitionFactory;

    private AnnotationContainerBuilder(final AnnotationComponentKeyFactory componentKeyFactory,
            final AnnotationScopeDecider scopeDecider,
            final AnnotationMemberFactory memberFactory,
            final AnnotationComponentDefinitionFactory componentDefinitionFactory) {
        this.componentKeyFactory = Objects.requireNonNull(componentKeyFactory);
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.componentDefinitionFactory = Objects.requireNonNull(componentDefinitionFactory);
    }

    /**
     * コンポーネント定義を登録する。
     * 
     * @param <T> コンポーネントの型
     * @param keyFactory 検索キーのファクトリ
     * @param componentType コンポーネントのクラス
     * @return このビルダー自身
     */
    private <T> AnnotationContainerBuilder register(final Supplier<ComponentKey<T>> keyFactory,
            final Class<T> componentType) {

        if (componentType.isAnnotation()) {
            errorCollector.add(new InvalidComponentException(
                    "Annotation [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isInterface()) {
            errorCollector.add(new InvalidComponentException(
                    "Interface [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isEnum()) {
            errorCollector.add(new InvalidComponentException(
                    "Enum [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (componentType.isAnonymousClass()) {
            errorCollector.add(new InvalidComponentException(
                    "Anonymous Class [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }
        if (Modifier.isAbstract(componentType.getModifiers())) {
            errorCollector.add(new InvalidComponentException(
                    "Abstract Class [" + componentType.getName()
                            + "] can not be component. Component must be class (not abstract, not enum) with name."));
            return this;
        }

        final ComponentKey<T> key = keyFactory.get();
        final Optional<ComponentDefinition<T>> definition = componentDefinitionFactory
                .fromComponentClass(componentType, errorCollector);
        definition.ifPresent(a -> register(key, a));
        return this;
    }

    /**
     * コンポーネント定義を登録する。
     * 
     * @param <T> コンポーネントの型
     * @param componentType コンポーネントのクラス。
     * @return このビルダー自身
     */
    public <T> AnnotationContainerBuilder register(final Class<T> componentType) {
        final Supplier<ComponentKey<T>> keyFactory = () -> componentKeyFactory
                .fromComponentClass(componentType);
        return register(keyFactory, componentType);
    }

    /**
     * コンポーネント定義を登録する。
     * 
     * @param <T> コンポーネントの型
     * @param componentType コンポーネントのクラス
     * @param qualifiers 限定子
     * @return このビルダー自身
     */
    public <T> AnnotationContainerBuilder register(final Class<T> componentType,
            final Annotation... qualifiers) {
        final Supplier<ComponentKey<T>> keyFactory = () -> new ComponentKey<>(componentType,
                qualifiers);
        return register(keyFactory, componentType);
    }

    @Override
    public Container build() {
        scopeDecider.registerScopes(this, memberFactory);
        return super.build();
    }

    /**
     * デフォルトの設定をしたインスタンスを構築する。
     * 
     * @return インスタンス
     */
    public static AnnotationContainerBuilder createDefault() {
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
         * 検索キーのファクトリ
         */
        private AnnotationComponentKeyFactory componentKeyFactory = AnnotationComponentKeyFactory
                .createDefault();
        /**
         * スコープを決定するクラス
         */
        private AnnotationScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();
        /**
         * コンポーネント定義の構成要素ファクトリ
         */
        private AnnotationMemberFactory memberFactory = AnnotationMemberFactory.createDefault();
        /**
         * コンポーネント定義のファクトリ
         */
        private AnnotationComponentDefinitionFactory componentDefinitionFactory;

        /**
         * インスタンスを生成する。
         */
        private Builder() {
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
        }

        /**
         * 検索キーのファクトリを設定する。
         * 
         * @param componentKeyFactory 検索キーのファクトリ
         * @return このビルダー自身
         */
        public Builder componentKeyFactory(
                final AnnotationComponentKeyFactory componentKeyFactory) {
            this.componentKeyFactory = componentKeyFactory;
            return this;
        }

        /**
         * スコープを決定するクラスを設定する。
         * 
         * @param scopeDecider スコープを決定するクラス
         * @return このビルダー自身
         */
        public Builder scopeDecider(final AnnotationScopeDecider scopeDecider) {
            this.scopeDecider = scopeDecider;
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
            return this;
        }

        /**
         * コンポーネント定義の構成要素ファクトリを設定する。
         * 
         * @param memberFactory コンポーネント定義の構成要素ファクトリ
         * @return このビルダー自身
         */
        public Builder memberFactory(final AnnotationMemberFactory memberFactory) {
            this.memberFactory = memberFactory;
            this.componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                    memberFactory, scopeDecider);
            return this;
        }

        /**
         * コンポーネント定義のファクトリを設定する。
         * 
         * @param componentDefinitionFactory コンポーネント定義のファクトリ
         * @return このビルダー自身
         */
        public Builder componentDefinitionFactory(
                final AnnotationComponentDefinitionFactory componentDefinitionFactory) {
            this.componentDefinitionFactory = componentDefinitionFactory;
            return this;
        }

        /**
         * シングルトンのコンポーネントをイーガーロードするかどうかを設定する。
         * 
         * @param eagerLoad イーガーロードする場合は{@literal true}
         * @return このビルダー自身
         */
        public Builder eagerLoad(final boolean eagerLoad) {
            scopeDecider(
                    AnnotationScopeDecider.builderFrom(scopeDecider).eagerLoad(eagerLoad).build());
            return this;
        }

        /**
         * DIコンテナのビルダーを構築する。
         * 
         * @return DIコンテナのビルダー
         */
        public AnnotationContainerBuilder build() {
            return new AnnotationContainerBuilder(componentKeyFactory, scopeDecider, memberFactory,
                    componentDefinitionFactory);
        }
    }
}
