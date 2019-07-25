package nablarch.fw.dicontainer.nablarch;

import java.lang.annotation.Annotation;
import java.util.ServiceLoader;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.annotation.auto.AnnotationAutoContainerFactory;
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
    private final AnnotationAutoContainerFactory.Builder builder = AnnotationAutoContainerFactory
            .builder()
            .traversalConfigs(ServiceLoader.load(TraversalConfig.class));
    private RequestContextSupplier requestContextSupplier;
    private SessionContextSupplier sessionContextSupplier;

    @Override
    public void initialize() {
        final ScopeDecider scopeDecider = AnnotationScopeDecider.builder()
                .addScope(RequestScoped.class, new RequestScope(requestContextSupplier))
                .addScope(SessionScoped.class, new SessionScope(sessionContextSupplier))
                .build();
        final AnnotationAutoContainerFactory factory = builder.scopeDecider(scopeDecider).build();
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

    @SafeVarargs
    public final void setTargetAnnotations(final Class<? extends Annotation>... annotations) {
        builder.targetAnnotations(annotations);
    }

    public void traversalConfigs(final Iterable<TraversalConfig> traversalConfigs) {
        builder.traversalConfigs(traversalConfigs);
    }

    public void eagerLoad(final boolean eagerLoad) {
        builder.eagerLoad(eagerLoad);
    }

    public void setRequestContextSupplier(final RequestContextSupplier requestContextSupplier) {
        this.requestContextSupplier = requestContextSupplier;
    }

    public void setSessionContextSupplier(final SessionContextSupplier sessionContextSupplier) {
        this.sessionContextSupplier = sessionContextSupplier;
    }
}
