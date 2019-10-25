package nablarch.fw.dicontainer.container;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Provider;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.annotation.Published;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.AliasMapping;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinitionRepository;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.ComponentKey.AliasKey;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.impl.ContainerInjectableConstructor;
import nablarch.fw.dicontainer.event.ContainerCreated;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.scope.Scope;

/**
 * DIコンテナのビルダー。
 *
 * @param <BUILDER> このビルダーのサブクラス
 */
@Published(tag = "architect")
public class ContainerBuilder<BUILDER extends ContainerBuilder<BUILDER>> {

    /**
     * ロガー
     */
    private static final Logger logger = LoggerManager.get(ContainerBuilder.class);
    /**
     * コンポーネント定義のリポジトリ
     */
    private final ComponentDefinitionRepository definitions = new ComponentDefinitionRepository();
    /**
     * エイリアスキーと検索キーのマッピング
     */
    private final AliasMapping aliasesMap = new AliasMapping();
    /**
     * バリデーションエラーを収集するクラス
     */
    protected final ErrorCollector errorCollector = ErrorCollector.newInstance();
    /**
     * DIコンテナの構築を開始した時点の{@link System#nanoTime()}値
     */
    private final long startedAt;

    /**
     * インスタンスを生成する。
     * 
     */
    public ContainerBuilder() {
        this.startedAt = System.nanoTime();
        logger.logInfo("Start building a Container.");
    }

    /**
     * {@link ErrorCollector#throwExceptionIfExistsError()}で無視をする例外クラスを設定する。
     * 
     * @param ignoreMe 無視される例外クラス
     * @return このビルダー自身
     */
    public BUILDER ignoreError(final Class<? extends ContainerException> ignoreMe) {
        logger.logDebug("Ignore error during building Container. ignored class=" + ignoreMe.getName());

        errorCollector.ignore(ignoreMe);
        return self();
    }

    /**
     * コンポーネント定義を登録する。
     * 
     * @param <T> コンポーネントの型
     * @param key 検索キー
     * @param definition コンポーネント定義
     * @return このビルダー自身
     */
    public <T> BUILDER register(final ComponentKey<T> key,
            final ComponentDefinition<T> definition) {

        logger.logDebug("Start registering component definition. key=" + key);

        for (final AliasKey aliasKey : key.aliasKeys()) {
            logger.logDebug("Register alias key [" + aliasKey + "] for [" + key + "]");
            aliasesMap.register(aliasKey, key);
        }
        definitions.register(key, definition);

        logger.logDebug("Component definition registered. key=" + key);

        return self();
    }

    /**
     * コンポーネント定義を検索する。
     * 
     * @param key 検索キー
     * @return コンポーネント定義の集合
     */
    public Set<ComponentDefinition<?>> findComponentDefinitions(final ComponentKey<?> key) {
        final ComponentDefinition<?> definition = definitions.find(key);
        if (definition != null) {
            return Collections.singleton(definition);
        }
        final Set<ComponentKey<?>> alterKeys = aliasesMap.find(key.asAliasKey());
        return alterKeys.stream().map(definitions::find).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 依存関係の循環を検出するためのバリデーションを行う。
     * 
     * @param key 検索キー
     * @param target 対象となるコンポーネント定義
     */
    public void validateCycleDependency(final ComponentKey<?> key,
            final ComponentDefinition<?> target) {
        final CycleDependencyValidationContext context = CycleDependencyValidationContext
                .newContext(this, target);
        context.validateCycleDependency(key);
    }

    /**
     * バリデーションエラーを追加する。
     * 
     * @param exception バリデーションエラー
     */
    public void addError(final ContainerException exception) {
        errorCollector.add(exception);
    }

    /**
     * DIコンテナを構築する。
     * 
     * @return 構築されたDIコンテナ
     */
    public Container build() {
        registerContainer();
        definitions.validate(this);
        errorCollector.throwExceptionIfExistsError();
        final DefaultContainer container = new DefaultContainer(definitions, aliasesMap);

        final long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
        logger.logInfo("Built Container. " + time + "(msec)");

        container.fire(new ContainerCreated());
        return container;
    }

    /**
     * DIコンテナのコンポーネント定義を登録する。
     * 
     */
    private void registerContainer() {
        final ComponentKey<DefaultContainer> key = new ComponentKey<>(
                DefaultContainer.class);
        final ComponentDefinition<DefaultContainer> definition = ComponentDefinition
                .builder(DefaultContainer.class)
                .injectableConstructor(new ContainerInjectableConstructor())
                .scope(new ContainerScope())
                .build()
                .get();
        register(key, definition);
    }

    /**
     * 自分自身を{@code BUILDER}型へキャストする。
     * 
     * @return 自分自身
     */
    private BUILDER self() {
        return (BUILDER) this;
    }

    /**
     * DIコンテナ専用のスコープ。
     *
     */
    private final class ContainerScope implements Scope {

        @Override
        public <T> T getComponent(final ComponentId id, final Provider<T> provider) {
            return provider.get();
        }

        @Override
        public int dimensions() {
            return Integer.MAX_VALUE;
        }

        @Override
        public <T> void register(final ComponentDefinition<T> definition) {
        }
    }
}
