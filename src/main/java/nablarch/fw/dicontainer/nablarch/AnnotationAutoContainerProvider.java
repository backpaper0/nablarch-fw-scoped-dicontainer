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
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.scope.ScopeDecider;
import nablarch.fw.dicontainer.web.RequestScoped;
import nablarch.fw.dicontainer.web.SessionScoped;
import nablarch.fw.dicontainer.web.context.RequestContextSupplier;
import nablarch.fw.dicontainer.web.context.SessionContextSupplier;
import nablarch.fw.dicontainer.web.scope.RequestScope;
import nablarch.fw.dicontainer.web.scope.SessionScope;

public final class AnnotationAutoContainerProvider implements Initializable {

    private static final Logger logger = LoggerManager.get(AnnotationAutoContainerProvider.class);
    private Iterable<TraversalConfig> traversalConfigs = ServiceLoader.load(TraversalConfig.class);
    private boolean eagerLoad;
    private AnnotationContainerBuilder annotationContainerBuilder;
    private ComponentPredicate componentPredicate = new DefaultComponentPredicate();
    private RequestContextSupplier requestContextSupplier;
    private SessionContextSupplier sessionContextSupplier;

    @Override
    public void initialize() {
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                annotationContainerBuilder(), traversalConfigs, componentPredicate);
        try {
            final Container container = factory.create();
            ContainerImplementers.set((ContainerImplementer) container);
        } catch (final ContainerCreationException e) {
            if (logger.isDebugEnabled()) {
                for (final ContainerException ce : e.getExceptions()) {
                    logger.logDebug(ce.getMessage());
                }
            }
            throw e;
        }
    }

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

    public void setAnnotationContainerBuilder(
            final AnnotationContainerBuilder annotationContainerBuilder) {
        this.annotationContainerBuilder = annotationContainerBuilder;
    }

    public void setComponentPredicate(final ComponentPredicate componentPredicate) {
        this.componentPredicate = componentPredicate;
    }

    public void setTraversalConfigs(final Iterable<TraversalConfig> traversalConfigs) {
        this.traversalConfigs = traversalConfigs;
    }

    public void setEagerLoad(final boolean eagerLoad) {
        this.eagerLoad = eagerLoad;
    }

    public void setRequestContextSupplier(final RequestContextSupplier requestContextSupplier) {
        this.requestContextSupplier = requestContextSupplier;
    }

    public void setSessionContextSupplier(final SessionContextSupplier sessionContextSupplier) {
        this.sessionContextSupplier = sessionContextSupplier;
    }
}
