package nablarch.fw.dicontainer.annotation.auto;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.NamedImpl;
import nablarch.fw.dicontainer.annotation.AnnotationComponentDefinitionFactory;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.AnnotationMemberFactory;
import nablarch.fw.dicontainer.annotation.AnnotationScopeDecider;
import nablarch.fw.dicontainer.annotation.DefaultInjectionComponentResolverFactory;
import nablarch.fw.dicontainer.annotation.auto.custom.Custom1;
import nablarch.fw.dicontainer.annotation.auto.custom.Custom2;
import nablarch.fw.dicontainer.annotation.auto.custom.CustomDIConfig;
import nablarch.fw.dicontainer.annotation.auto.customlogger.CustomLogger;
import nablarch.fw.dicontainer.annotation.auto.customlogger.CustomLoggerDIConfig;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto1;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto2;
import nablarch.fw.dicontainer.annotation.auto.demo.Auto3;
import nablarch.fw.dicontainer.annotation.auto.demo.NotComponent;
import nablarch.fw.dicontainer.annotation.auto.demo.subpkg.Auto4;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.factory.ComponentDefinitionFactory;
import nablarch.fw.dicontainer.component.factory.InjectionComponentResolverFactory;
import nablarch.fw.dicontainer.component.factory.MemberFactory;
import nablarch.fw.dicontainer.component.impl.DefaultInjectableConstructor;
import nablarch.fw.dicontainer.component.impl.InjectableField;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.container.ContainerImplementer;
import nablarch.fw.dicontainer.container.CycleDependencyValidationContext;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;
import nablarch.fw.dicontainer.scope.ScopeDecider;
import nablarch.fw.dicontainer.scope.SingletonScope;

public class AnnotationAutoContainerFactoryTest {

    @Test
    public void create() throws Exception {
        final Iterable<TraversalConfig> traversalConfigs = Collections
                .singleton(new TraversalConfig() {
                    @Override
                    public Set<String> includes() {
                        return Collections.singleton(
                                "^nablarch\\.fw\\.dicontainer\\.annotation\\.auto\\.demo\\..*$$");
                    }
                });
        final AnnotationContainerBuilder containerBuilder = AnnotationContainerBuilder
                .createDefault();
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                containerBuilder, traversalConfigs, new DefaultComponentPredicate());
        final Container container = factory.create();

        assertNotNull(container.getComponent(Auto1.class));
        assertNotNull(container.getComponent(Auto2.class));
        assertNotNull(container.getComponent(Auto3.class, new NamedImpl("")));
        assertNotNull(container.getComponent(Auto4.class));
        try {
            assertNotNull(container.getComponent(NotComponent.class));
            fail();
        } catch (final ComponentNotFoundException e) {
        }
    }

    @Test
    public void customize() throws Exception {
        final Iterable<TraversalConfig> traversalConfigs = Collections
                .singleton(new CustomDIConfig());
        final ScopeDecider scopeDecider = AnnotationScopeDecider.builder()
                .defaultScope(new SingletonScope()).build();
        final MemberFactory memberFactory = new CustomMemberFactory();
        final ComponentDefinitionFactory componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                memberFactory, scopeDecider);
        final AnnotationContainerBuilder containerBuilder = AnnotationContainerBuilder
                .builder()
                .componentDefinitionFactory(componentDefinitionFactory)
                .scopeDecider(scopeDecider)
                .build();
        final ComponentPredicate predicate = a -> true;
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                containerBuilder, traversalConfigs, predicate);
        final Container container = factory.create();

        final Custom1 component1 = container.getComponent(Custom1.class);
        final Custom2 component2 = container.getComponent(Custom2.class);
        assertNotNull(component1);
        assertNotNull(component2);
        assertSame(component2, component1.getCustom2());
    }

    @Test
    public void customizeInjection() throws Exception {
        final Iterable<TraversalConfig> traversalConfigs = Collections
                .singleton(new CustomLoggerDIConfig());
        final ScopeDecider scopeDecider = AnnotationScopeDecider.createDefault();
        final MemberFactory memberFactory = new CustomLoggerMemberFactory();
        final ComponentDefinitionFactory componentDefinitionFactory = new AnnotationComponentDefinitionFactory(
                memberFactory, scopeDecider);
        final AnnotationContainerBuilder containerBuilder = AnnotationContainerBuilder
                .builder()
                .componentDefinitionFactory(componentDefinitionFactory)
                .scopeDecider(scopeDecider)
                .build();
        final ComponentPredicate predicate = a -> true;
        final AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                containerBuilder, traversalConfigs, predicate);
        final Container container = factory.create();

        final CustomLogger component1 = container.getComponent(CustomLogger.class);
        assertNotNull(component1);
        assertNotNull(component1.getLogger());
    }

    private static final class CustomMemberFactory implements MemberFactory {

        InjectionComponentResolverFactory injectionComponentResolverFactory = new DefaultInjectionComponentResolverFactory();

        @Override
        public Optional<InjectableConstructor> createConstructor(final Class<?> componentType,
                final ErrorCollector errorCollector) {

            for (final Constructor<?> constructor : componentType.getDeclaredConstructors()) {
                final InjectionComponentResolvers resolvers = injectionComponentResolverFactory
                        .fromConstructorParameters(constructor);
                return Optional.of(new DefaultInjectableConstructor(constructor, resolvers));
            }

            return Optional.empty();
        }

        @Override
        public List<InjectableMember> createFieldsAndMethods(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return Collections.emptyList();
        }

        @Override
        public List<ObservesMethod> createObservesMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return Collections.emptyList();
        }

        @Override
        public Optional<InitMethod> createInitMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return Optional.empty();
        }

        @Override
        public Optional<DestroyMethod> createDestroyMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return Optional.empty();
        }
    }

    private static final class CustomLoggerMemberFactory implements MemberFactory {

        private final MemberFactory memberFactory = new AnnotationMemberFactory(
                new DefaultInjectionComponentResolverFactory());

        @Override
        public Optional<InjectableConstructor> createConstructor(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return memberFactory.createConstructor(componentType, errorCollector);
        }

        @Override
        public List<InjectableMember> createFieldsAndMethods(final Class<?> componentType,
                final ErrorCollector errorCollector) {

            final List<InjectableMember> injectableMembers = new ArrayList<>();

            final List<InjectableMember> addMe = memberFactory
                    .createFieldsAndMethods(componentType, errorCollector);
            injectableMembers.addAll(addMe);

            for (final Field field : componentType.getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    final InjectionComponentResolver resolver = new LoggerInjectionComponentResolver(
                            field);
                    injectableMembers.add(new InjectableField(field, resolver));
                }
            }

            return injectableMembers;
        }

        @Override
        public List<ObservesMethod> createObservesMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return memberFactory.createObservesMethod(componentType, errorCollector);
        }

        @Override
        public Optional<InitMethod> createInitMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return memberFactory.createInitMethod(componentType, errorCollector);
        }

        @Override
        public Optional<DestroyMethod> createDestroyMethod(final Class<?> componentType,
                final ErrorCollector errorCollector) {
            return memberFactory.createDestroyMethod(componentType, errorCollector);
        }
    }

    private static final class LoggerInjectionComponentResolver
            implements InjectionComponentResolver {

        private final Field field;

        public LoggerInjectionComponentResolver(final Field field) {
            this.field = field;
        }

        @Override
        public Object resolve(final ContainerImplementer container) {
            return LoggerManager.get(field.getType());
        }

        @Override
        public void validate(final ContainerBuilder<?> containerBuilder,
                final ComponentDefinition<?> self) {
        }

        @Override
        public void validateCycleDependency(final CycleDependencyValidationContext context) {
        }
    }
}
