package nablarch.fw.dicontainer.annotation;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import nablarch.fw.dicontainer.ComponentId;
import nablarch.fw.dicontainer.Scope;
import nablarch.fw.dicontainer.config.ComponentDefinition;
import nablarch.fw.dicontainer.config.ComponentDefinition.Builder;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.ErrorCollector;
import nablarch.fw.dicontainer.config.FactoryMethod;
import nablarch.fw.dicontainer.config.InitMethod;
import nablarch.fw.dicontainer.config.InjectableMember;
import nablarch.fw.dicontainer.config.ObservesMethod;

public final class AnnotationComponentDefinitionFactory {

    private final AnnotationMemberFactory memberFactory;
    private final AnnotationScopeDecider scopeDecider;

    public AnnotationComponentDefinitionFactory(final AnnotationMemberFactory memberFactory,
            final AnnotationScopeDecider scopeDecider) {
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
    }

    public <T> ComponentDefinition<T> fromClass(final Class<T> componentType,
            final ErrorCollector errorCollector) {
        final ComponentId id = ComponentId.generate();
        final InjectableMember injectableConstructor = memberFactory
                .createConstructor(componentType, errorCollector);
        final Set<InjectableMember> injectableMembers = memberFactory
                .createFieldsAndMethods(componentType, errorCollector);
        final Set<ObservesMethod> observesMethods = memberFactory
                .createObservesMethod(componentType, errorCollector);
        final InitMethod initMethod = memberFactory.createInitMethod(componentType, errorCollector);
        final DestroyMethod destroyMethod = memberFactory.createDestroyMethod(componentType,
                errorCollector);
        final Set<FactoryMethod> factoryMethods = memberFactory.createFactoryMethods(id,
                componentType, this, errorCollector);
        final Scope scope = scopeDecider.fromClass(componentType);
        final Builder<T> builder = ComponentDefinition.builder();
        return builder
                .id(id)
                .injectableConstructor(injectableConstructor)
                .injectableMembers(injectableMembers)
                .observesMethods(observesMethods)
                .initMethod(initMethod)
                .destroyMethod(destroyMethod)
                .factoryMethods(factoryMethods)
                .scope(scope)
                .build();
    }

    public ComponentDefinition<?> fromMethod(final ComponentId id, final Method method,
            final ErrorCollector errorCollector) {
        final InjectableMember injectableConstructor = memberFactory.createFactoryMethod(id, method,
                errorCollector);
        final DestroyMethod destroyMethod = memberFactory.createFactoryDestroyMethod(method,
                errorCollector);
        final Scope scope = scopeDecider.fromMethod(method);
        return ComponentDefinition.builder()
                .injectableConstructor(injectableConstructor)
                .destroyMethod(destroyMethod)
                .scope(scope)
                .build();
    }
}
