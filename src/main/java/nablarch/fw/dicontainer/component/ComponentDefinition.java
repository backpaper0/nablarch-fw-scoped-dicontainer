package nablarch.fw.dicontainer.component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Provider;

import nablarch.fw.dicontainer.component.impl.NoopDestroyMethod;
import nablarch.fw.dicontainer.component.impl.NoopInitMethod;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.scope.Scope;

/**
 * コンポーネント定義
 *
 * @param <T> コンポーネントの型
 */
public final class ComponentDefinition<T> {

    /**
     * ID
     */
    private final ComponentId id;
    /**
     * コンポーネントのクラス
     */
    private final Class<T> componentType;
    /**
     * コンポーネントを生成するコンストラクタ・メソッド
     */
    private final InjectableMember injectableConstructor;
    /**
     * インジェクションされるメソッド・フィールド
     */
    private final List<InjectableMember> injectableMembers;
    /**
     * イベントハンドラメソッド
     */
    private final List<ObservesMethod> observesMethods;
    /**
     * 初期化メソッド
     */
    private final InitMethod initMethod;
    /**
     * 破棄メソッド
     */
    private final DestroyMethod destroyMethod;
    /**
     * コンポーネントのファクトリーメソッド
     */
    private final List<FactoryMethod> factoryMethods;
    /**
     * スコープ
     */
    private final Scope scope;

    /**
     * インスタンスを生成する。
     * 
     * @param id ID
     * @param componentType コンポーネントのクラス
     * @param injectableConstructor コンポーネントを生成するコンストラクタ・メソッド
     * @param injectableMembers インジェクションされるメソッド・フィールド
     * @param observesMethods イベントハンドラメソッド
     * @param initMethod 初期化メソッド
     * @param destroyMethod 破棄メソッド
     * @param factoryMethods コンポーネントのファクトリーメソッド
     * @param scope スコープ
     */
    private ComponentDefinition(final ComponentId id,
            final Class<T> componentType,
            final InjectableMember injectableConstructor,
            final List<InjectableMember> injectableMembers,
            final List<ObservesMethod> observesMethods,
            final InitMethod initMethod,
            final DestroyMethod destroyMethod,
            final List<FactoryMethod> factoryMethods,
            final Scope scope) {
        this.id = Objects.requireNonNull(id);
        this.componentType = Objects.requireNonNull(componentType);
        this.injectableConstructor = Objects.requireNonNull(injectableConstructor);
        this.injectableMembers = Objects.requireNonNull(injectableMembers);
        this.observesMethods = Objects.requireNonNull(observesMethods);
        this.initMethod = Objects.requireNonNull(initMethod);
        this.destroyMethod = Objects.requireNonNull(destroyMethod);
        this.factoryMethods = Objects.requireNonNull(factoryMethods);
        this.scope = Objects.requireNonNull(scope);

        this.scope.register(this);
    }

    /**
     * IDを取得する。
     * 
     * @return ID
     */
    public ComponentId getId() {
        return id;
    }

    /**
     * バリデーションを行う。
     * 
     * @param containerBuilder DIコンテナのビルダー
     */
    public void validate(final ContainerBuilder<?> containerBuilder) {
        injectableConstructor.validate(containerBuilder, this);
        for (final InjectableMember injectableMember : injectableMembers) {
            injectableMember.validate(containerBuilder, this);
        }
        for (final ObservesMethod observesMethod : observesMethods) {
            observesMethod.validate(containerBuilder, this);
        }
        initMethod.validate(containerBuilder, this);
        destroyMethod.validate(containerBuilder, this);
        for (final FactoryMethod factoryMethod : factoryMethods) {
            factoryMethod.validate(containerBuilder, this);
        }
    }

    /**
     * 渡されたコンポーネント定義よりもスコープが狭いかどうかを返す。
     * 
     * @param injected インジェクションされるコンポーネントの定義
     * @return 自身の方がスコープが狭い場合は{@literal true}を返す
     */
    public boolean isNarrowScope(final ComponentDefinition<?> injected) {
        return scope.dimensions() <= injected.scope.dimensions();
    }

    /**
     * 依存関係の循環を検出するためのバリデーションを行う。
     * 
     * @param context 循環依存バリデーションのコンテキスト
     */
    public void validateCycleDependency(final CycleDependencyValidationContext context) {
        injectableConstructor.validateCycleDependency(context.createSubContext());
        for (final InjectableMember injectableMember : injectableMembers) {
            injectableMember.validateCycleDependency(context.createSubContext());
        }
    }

    /**
     * ファクトリーメソッドを適用する。
     * 
     * @param containerBuilder DIコンテナのビルダー
     */
    public void applyFactories(final ContainerBuilder<?> containerBuilder) {
        for (final FactoryMethod factoryMethod : factoryMethods) {
            factoryMethod.apply(containerBuilder);
        }
    }

    /**
     * コンポーネントを取得する。
     * 
     * @param container DIコンテナ
     * @return コンポーネント
     */
    public T getComponent(final ContainerImplementer container) {
        Objects.requireNonNull(container);
        final Provider<T> provider = new Provider<T>() {
            @Override
            public T get() {
                final Object component = injectableConstructor.inject(container, null);
                for (final InjectableMember injectableMember : injectableMembers) {
                    injectableMember.inject(container, component);
                }
                initMethod.invoke(component);
                return componentType.cast(component);
            }
        };
        return scope.getComponent(id, provider);
    }

    /**
     * イベントを発火させる。
     * 
     * @param container DIコンテナ
     * @param event イベント
     */
    public void fire(final ContainerImplementer container, final Object event) {
        for (final ObservesMethod observesMethod : observesMethods) {
            if (observesMethod.isTarget(event)) {
                final T component = getComponent(container);
                observesMethod.invoke(component, event);
            }
        }
    }

    /**
     * コンポーネントを破棄する。
     * 
     * @param component コンポーネント
     */
    public void destroyComponent(final T component) {
        destroyMethod.invoke(component);
    }

    @Override
    public String toString() {
        return "Component(class=" + componentType.getName() + ", scope="
                + scope.getClass().getSimpleName() + ")";
    }

    /**
     * ビルダーのインスタンスを生成する。
     * 
     * @param <T> コンポーネントの型
     * @param componentType コンポーネントのクラス
     * @return ビルダー
     */
    public static <T> Builder<T> builder(final Class<T> componentType) {
        return new Builder<>(componentType);
    }

    /**
     * コンポーネント定義のビルダー
     *
     * @param <T> コンポーネントの型
     */
    public static final class Builder<T> {

        /**
         * ID
         */
        private final ComponentId id = ComponentId.generate();
        /**
         * コンポーネントのクラス
         */
        private final Class<T> componentType;
        /**
         * コンポーネントを生成するコンストラクタ・メソッド
         */
        private InjectableMember injectableConstructor;
        /**
         * インジェクションされるメソッド・フィールド
         */
        private List<InjectableMember> injectableMembers = Collections.emptyList();
        /**
         * イベントハンドラメソッド
         */
        private List<ObservesMethod> observesMethods = Collections.emptyList();
        /**
         * 初期化メソッド
         */
        private InitMethod initMethod = new NoopInitMethod();
        /**
         * 破棄メソッド
         */
        private DestroyMethod destroyMethod = new NoopDestroyMethod();
        /**
         * コンポーネントのファクトリーメソッド
         */
        private List<FactoryMethod> factoryMethods = Collections.emptyList();
        /**
         * スコープ
         */
        private Scope scope;

        private Builder(final Class<T> componentType) {
            this.componentType = Objects.requireNonNull(componentType);
        }

        /**
         * IDを返す。
         * 
         * @return ID
         */
        public ComponentId id() {
            return id;
        }

        /**
         * コンポーネントを生成するコンストラクタ・メソッドを設定する。
         * 
         * @param injectableConstructor コンポーネントを生成するコンストラクタ・メソッド
         * @return このビルダー自身
         */
        public Builder<T> injectableConstructor(
                final InjectableMember injectableConstructor) {
            this.injectableConstructor = injectableConstructor;
            return this;
        }

        /**
         * インジェクションされるメソッド・フィールドを設定する。
         * 
         * @param injectableMembers インジェクションされるメソッド・フィールド
         * @return このビルダー自身
         */
        public Builder<T> injectableMembers(final List<InjectableMember> injectableMembers) {
            this.injectableMembers = injectableMembers;
            return this;
        }

        /**
         * イベントハンドラメソッドを設定する。
         * 
         * @param observesMethods イベントハンドラメソッド
         * @return このビルダー自身
         */
        public Builder<T> observesMethods(final List<ObservesMethod> observesMethods) {
            this.observesMethods = observesMethods;
            return this;
        }

        /**
         * 初期化メソッドを設定する。
         * 
         * @param initMethod 初期化メソッド
         * @return このビルダー自身
         */
        public Builder<T> initMethod(final InitMethod initMethod) {
            this.initMethod = initMethod;
            return this;
        }

        /**
         * 破棄メソッドを設定する。
         * 
         * @param destroyMethod 破棄メソッド
         * @return このビルダー自身
         */
        public Builder<T> destroyMethod(final DestroyMethod destroyMethod) {
            this.destroyMethod = destroyMethod;
            return this;
        }

        /**
         * コンポーネントのファクトリーメソッドを設定する。
         * 
         * @param factoryMethods コンポーネントのファクトリーメソッド
         * @return このビルダー自身
         */
        public Builder<T> factoryMethods(final List<FactoryMethod> factoryMethods) {
            this.factoryMethods = factoryMethods;
            return this;
        }

        /**
         * スコープを設定する。
         * 
         * @param scope スコープ
         * @return このビルダー自身
         */
        public Builder<T> scope(final Scope scope) {
            this.scope = scope;
            return this;
        }

        /**
         * コンポーネント定義を構築する。
         * 
         * @return コンポーネント定義
         */
        public Optional<ComponentDefinition<T>> build() {
            if (injectableConstructor == null) {
                return Optional.empty();
            }
            if (scope == null) {
                return Optional.empty();
            }
            final ComponentDefinition<T> cd = new ComponentDefinition<>(id, componentType,
                    injectableConstructor, injectableMembers, observesMethods, initMethod,
                    destroyMethod, factoryMethods, scope);
            return Optional.of(cd);
        }
    }
}
