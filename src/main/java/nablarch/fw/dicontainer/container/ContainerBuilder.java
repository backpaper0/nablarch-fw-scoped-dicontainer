package nablarch.fw.dicontainer.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.AliasMapping;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinitionRepository;
import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.exception.CycleInjectionException;
import nablarch.fw.dicontainer.exception.ErrorCollector;

public class ContainerBuilder<T extends ContainerBuilder<T>> {

    private final ComponentDefinitionRepository definitions = new ComponentDefinitionRepository();
    private final AliasMapping aliasesMap = new AliasMapping();
    protected final ErrorCollector errorCollector = ErrorCollector.newInstance();

    public T ignoreError(final Class<? extends ContainerException> ignoreMe) {
        errorCollector.ignore(ignoreMe);
        return self();
    }

    public <U> T register(final ComponentKey<U> key, final ComponentDefinition<U> definition) {
        key.aliasKeys().forEach(aliasKey -> aliasesMap.register(aliasKey, key));
        definitions.register(key, definition);
        definition.applyFactories(this);
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
        definitions.validate(this);
        errorCollector.throwExceptionIfExistsError();
        return new DefaultContainer(definitions, aliasesMap);
    }

    private T self() {
        return (T) this;
    }

    public static class CycleDependencyValidationContext {

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
                containerBuilder.addError(new CycleInjectionException());
                return;
            }
            dependency.validateCycleDependency(this);
        }
    }
}
