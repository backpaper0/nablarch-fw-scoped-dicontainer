package nablarch.fw.dicontainer.nablarch;

import java.util.ServiceLoader;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.annotation.auto.AnnotationAutoContainerFactory;
import nablarch.fw.dicontainer.annotation.auto.ComponentPredicate;
import nablarch.fw.dicontainer.annotation.auto.DefaultComponentPredicate;
import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.scope.ScopeDecider;
import nablarch.fw.dicontainer.web.RequestScoped;
import nablarch.fw.dicontainer.web.SessionScoped;
import nablarch.fw.dicontainer.web.context.RequestContextSupplier;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;
import nablarch.fw.dicontainer.web.scope.RequestScope;
import nablarch.fw.dicontainer.web.scope.SessionScope;

/**
 * {@link nablarch.core.repository.initialization.ApplicationInitializer}の初期化処理で、
 * NablarchにDIコンテナの提供を行うクラス。
 */
public final class AnnotationAutoContainerProvider implements Initializable {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(AnnotationAutoContainerProvider.class);

    /** クラスのトラバース設定 */
    private Iterable<TraversalConfig> traversalConfigs = ServiceLoader.load(TraversalConfig.class);

    /** シングルトンコンポーネントのイーガーロードを行うか */
    private boolean eagerLoad;

    /** アノテーションをもとにDIコンテナを構築するビルダー */
    private AnnotationContainerBuilder annotationContainerBuilder;

    /** コンポーネント抽出条件 */
    private ComponentPredicate componentPredicate = new DefaultComponentPredicate();

    /** リクエストコンテキスト取得クラス */
    private RequestContextSupplier requestContextSupplier;

    /** セッションコンテキスト取得クラス */
    private SessionContextSupplier sessionContextSupplier;

    @Override
    public void initialize() {
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                annotationContainerBuilder(), traversalConfigs, componentPredicate);
        try {
            final Container container = factory.create();
            Containers.set(container);
        } catch (final ContainerCreationException e) {
            LOGGER.logDebug("Container Creation failed. see following messages for detail.");
            for (final ContainerException ce : e.getExceptions()) {
                LOGGER.logDebug(ce.getMessage());
            }
            throw e;
        }
    }

    /**
     * {@link AnnotationContainerBuilder}を取得する。
     *
     * @return {@link AnnotationContainerBuilder}
     */
    private AnnotationContainerBuilder annotationContainerBuilder() {
        if (annotationContainerBuilder != null) {
            return annotationContainerBuilder;
        }
        final ScopeDecider scopeDecider = AnnotationScopeDecider.builder()
                .addScope(RequestScoped.class, new RequestScope(requestContextSupplier))
                .addScope(SessionScoped.class, new SessionScope(sessionContextSupplier))
                .eagerLoad(eagerLoad)
                .build();
        return AnnotationContainerBuilder.builder()
                .scopeDecider(scopeDecider)
                .build();
    }

    /**
     * {@link AnnotationContainerBuilder}を設定する。
     * 本プロパティが設定済みの場合、設定されたインスタンスがそのまま使用される。
     * 未設定の場合は、以下のプロパティの設定を元にインスタンスを生成する。
     * <ul>
     * <li>{@link #setRequestContextSupplier(RequestContextSupplier)}</li>
     * <li>{@link #setSessionContextSupplier(SessionContextSupplier)}</li>
     * <li>{@link #setEagerLoad(boolean)}</li>
     * </ul>
     *
     * @param annotationContainerBuilder {@link AnnotationContainerBuilder}
     */
    public void setAnnotationContainerBuilder(
            final AnnotationContainerBuilder annotationContainerBuilder) {
        this.annotationContainerBuilder = annotationContainerBuilder;
    }

    /**
     * {@link ComponentPredicate}を設定する。
     * @param componentPredicate {@link ComponentPredicate}
     */
    public void setComponentPredicate(final ComponentPredicate componentPredicate) {
        this.componentPredicate = componentPredicate;
    }

    /**
     * {@link TraversalConfig}を設定する。
     *
     * @param traversalConfigs {@link TraversalConfig}
     */
    public void setTraversalConfigs(final Iterable<TraversalConfig> traversalConfigs) {
        this.traversalConfigs = traversalConfigs;
    }

    /**
     *  シングルトンコンポーネントのイーガーロードを行うかを設定する。
     * {@link #setAnnotationContainerBuilder(AnnotationContainerBuilder)}を明示的に設定した場合、
     * 本プロパティは使用されない。
     *
     * @param eagerLoad イーガーロードを行う場合、真
     */
    public void setEagerLoad(final boolean eagerLoad) {
        this.eagerLoad = eagerLoad;
    }

    /**
     * リクエストコンテキスト取得クラスを設定する。
     * {@link #setAnnotationContainerBuilder(AnnotationContainerBuilder)}を明示的に設定した場合、
     * 本プロパティは使用されない。
     *
     * @param requestContextSupplier  リクエストコンテキスト取得クラス
     */
    public void setRequestContextSupplier(final RequestContextSupplier requestContextSupplier) {
        this.requestContextSupplier = requestContextSupplier;
    }

    /**
     * セッションコンテキスト取得クラスを設定する。
     * @param sessionContextSupplier セッションコンテキスト取得クラス
     */
    public void setSessionContextSupplier(final SessionContextSupplier sessionContextSupplier) {
        this.sessionContextSupplier = sessionContextSupplier;
    }
}
