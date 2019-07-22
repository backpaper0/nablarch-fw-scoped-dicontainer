package nablarch.fw.dicontainer.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.AliasMapping;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinitionRepository;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.ComponentKey.AliasKey;
import nablarch.fw.dicontainer.component.impl.ContainerInjectableMember;
import nablarch.fw.dicontainer.event.ContainerCreated;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.exception.CycleInjectionException;
import nablarch.fw.dicontainer.exception.ErrorCollector;
import nablarch.fw.dicontainer.scope.PassthroughScope;

public class ContainerBuilder<T extends ContainerBuilder<T>> {

    private static final Logger logger = LoggerManager.get(ContainerBuilder.class);
    private final ComponentDefinitionRepository definitions = new ComponentDefinitionRepository();
    private final AliasMapping aliasesMap = new AliasMapping();
    protected final ErrorCollector errorCollector = ErrorCollector.newInstance();
    private final long startedAt;

    public ContainerBuilder() {
        this.startedAt = System.nanoTime();
        if (logger.isInfoEnabled()) {
            logger.logInfo("Start building a Container.");
        }
    }

    public T ignoreError(final Class<? extends ContainerException> ignoreMe) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(
                    "Ignore error during building Container. ignored class=" + ignoreMe.getName());
        }
        errorCollector.ignore(ignoreMe);
        return self();
    }

    public <U> T register(final ComponentKey<U> key, final ComponentDefinition<U> definition) {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Start registering component definition. key=" + key);
        }
        for (final AliasKey aliasKey : key.aliasKeys()) {
            if (logger.isDebugEnabled()) {
                logger.logDebug("Register alias key [" + aliasKey + "] for [" + key + "]");
            }
            aliasesMap.register(aliasKey, key);
        }
        definitions.register(key, definition);
        definition.applyFactories(this);
        if (logger.isDebugEnabled()) {
            logger.logDebug("Component definition registered. key=" + key);
        }
        return self();
    }

    public Set<ComponentDefinition<?>> findComponentDefinitions(final ComponentKey<?> key) {
        final ComponentDefinition<?> definition = definitions.find(key);
        if (definition != null) {
            return Collections.singleton(definition);
        }
        final Set<ComponentKey<?>> alterKeys = aliasesMap.find(key.asAliasKey());
        return alterKeys.stream().map(definitions::find).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public void validateCycleDependency(final ComponentKey<?> key,
            final ComponentDefinition<?> target) {
        final CycleDependencyValidationContext context = CycleDependencyValidationContext
                .newContext(this, target);
        context.validateCycleDependency(key);
    }

    public void addError(final ContainerException exception) {
        errorCollector.add(exception);
    }

    public Container build() {
        registerContainer();
        definitions.validate(this);
        errorCollector.throwExceptionIfExistsError();
        final DefaultContainer container = new DefaultContainer(definitions, aliasesMap);
        if (logger.isInfoEnabled()) {
            final long time = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
            logger.logInfo("Built Container. " + time + "(msec)");
        }
        container.fire(new ContainerCreated());
        return container;
    }

    private void registerContainer() {
        final ComponentKey<ContainerImplementer> key = new ComponentKey<>(
                ContainerImplementer.class);
        final ComponentDefinition<ContainerImplementer> definition = ComponentDefinition
                .builder(ContainerImplementer.class)
                .injectableConstructor(new ContainerInjectableMember())
                .scope(new PassthroughScope())
                .build()
                .get();
        register(key, definition);
    }

    private T self() {
        return (T) this;
    }

    public static final class CycleDependencyValidationContext {

        private final ContainerBuilder<?> containerBuilder;
        private final ComponentDefinition<?> target;
        private final List<ComponentDefinition<?>> dependencies;

        public CycleDependencyValidationContext(final ContainerBuilder<?> containerBuilder,
                final ComponentDefinition<?> target,
                final List<ComponentDefinition<?>> dependencies) {
            this.containerBuilder = Objects.requireNonNull(containerBuilder);
            this.target = Objects.requireNonNull(target);
            this.dependencies = Objects.requireNonNull(dependencies);
        }

        static CycleDependencyValidationContext newContext(
                final ContainerBuilder<?> containerBuilder, final ComponentDefinition<?> target) {
            return new CycleDependencyValidationContext(containerBuilder, target,
                    new ArrayList<>());
        }

        public CycleDependencyValidationContext createSubContext() {
            return new CycleDependencyValidationContext(containerBuilder, target,
                    new ArrayList<>(dependencies));
        }

        public void validateCycleDependency(final ComponentKey<?> key) {
            final Set<ComponentDefinition<?>> cds = containerBuilder.findComponentDefinitions(key);
            if (cds.size() != 1) {
                return;
            }
            final ComponentDefinition<?> dependency = cds.iterator().next();
            dependencies.add(dependency);
            if (dependencies.contains(target)) {
                final ComponentDefinition<?> cd = dependencies.stream().filter(target::equals)
                        .findAny()
                        .get();
                containerBuilder.addError(
                        new CycleInjectionException("Dependency between [" + target + "] and ["
                                + cd + "] is cycled."));
                return;
            }
            dependency.validateCycleDependency(this);
        }
    }
}
