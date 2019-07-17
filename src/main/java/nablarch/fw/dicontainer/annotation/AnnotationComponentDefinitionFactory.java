package nablarch.fw.dicontainer.annotation;

import java.util.Objects;
import java.util.Set;

import nablarch.fw.dicontainer.config.ComponentDefinition;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.ErrorCollector;
import nablarch.fw.dicontainer.config.InitMethod;
import nablarch.fw.dicontainer.config.InjectableMember;
import nablarch.fw.dicontainer.config.ObservesMethod;
import nablarch.fw.dicontainer.config.ComponentDefinition.Builder;

public final class AnnotationComponentDefinitionFactory {

    private final AnnotationMemberFactory memberFactory;

    public AnnotationComponentDefinitionFactory(final AnnotationMemberFactory memberFactory) {
        this.memberFactory = Objects.requireNonNull(memberFactory);
    }

    public <T> Builder<T> builder(final Class<T> componentType,
            final ErrorCollector errorCollector) {
        final InjectableMember injectableConstructor = memberFactory
                .createConstructor(componentType, errorCollector);
        final Set<InjectableMember> injectableMembers = memberFactory
                .createFieldsAndMethods(componentType, errorCollector);
        final Set<ObservesMethod> observesMethods = memberFactory
                .createObservesMethod(componentType, errorCollector);
        final InitMethod initMethod = memberFactory.createInitMethod(componentType, errorCollector);
        final DestroyMethod destroyMethod = memberFactory.createDestroyMethod(componentType,
                errorCollector);
        final Builder<T> builder = ComponentDefinition.builder();
        return builder
                .injectableConstructor(injectableConstructor)
                .injectableMembers(injectableMembers)
                .observesMethods(observesMethods)
                .initMethod(initMethod)
                .destroyMethod(destroyMethod);
    }
}
