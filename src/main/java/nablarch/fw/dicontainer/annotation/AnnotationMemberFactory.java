package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Qualifier;

import nablarch.fw.dicontainer.ComponentKey;
import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.Factory;
import nablarch.fw.dicontainer.Init;
import nablarch.fw.dicontainer.Observes;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.FactoryMethod;
import nablarch.fw.dicontainer.component.FieldCollector;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.MethodCollector;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.impl.DefaultDestroyMethod;
import nablarch.fw.dicontainer.component.impl.DefaultFactoryMethod;
import nablarch.fw.dicontainer.component.impl.DefaultInitMethod;
import nablarch.fw.dicontainer.component.impl.DefaultObservesMethod;
import nablarch.fw.dicontainer.component.impl.InjectableConstructor;
import nablarch.fw.dicontainer.component.impl.InjectableFactoryMethod;
import nablarch.fw.dicontainer.component.impl.InjectableField;
import nablarch.fw.dicontainer.component.impl.InjectableMethod;
import nablarch.fw.dicontainer.exception.ErrorCollector;
import nablarch.fw.dicontainer.exception.FactoryMethodSignatureException;
import nablarch.fw.dicontainer.exception.InjectableConstructorDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectableConstructorNotFoundException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodDuplicatedException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodNotFoundException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodSignatureException;
import nablarch.fw.dicontainer.exception.ObserverMethodSignatureException;
import nablarch.fw.dicontainer.exception.StaticInjectionException;

public final class AnnotationMemberFactory {

    private final AnnotationSet injectAnnotations;
    private final AnnotationSet initAnnotations;
    private final AnnotationSet destroyAnnotations;
    private final AnnotationSet observesAnnotations;
    private final AnnotationSet factoryAnnotations;
    private final AnnotationInjectionComponentResolverFactory injectionComponentResolverFactory;
    private final String destroyMethodName;

    private AnnotationMemberFactory(final AnnotationSet injectAnnotations,
            final AnnotationSet initAnnotations,
            final AnnotationSet destroyAnnotations, final AnnotationSet observesAnnotations,
            final AnnotationSet factoryAnnotations,
            final AnnotationInjectionComponentResolverFactory injectionComponentResolverFactory,
            final String destroyMethodName) {
        this.injectAnnotations = Objects.requireNonNull(injectAnnotations);
        this.initAnnotations = Objects.requireNonNull(initAnnotations);
        this.destroyAnnotations = Objects.requireNonNull(destroyAnnotations);
        this.observesAnnotations = Objects.requireNonNull(observesAnnotations);
        this.factoryAnnotations = Objects.requireNonNull(factoryAnnotations);
        this.injectionComponentResolverFactory = Objects
                .requireNonNull(injectionComponentResolverFactory);
        this.destroyMethodName = Objects.requireNonNull(destroyMethodName);
    }

    public Optional<InjectableMember> createConstructor(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final Set<Constructor<?>> constructors = new HashSet<>();
        Constructor<?> noArgConstructor = null;
        for (final Constructor<?> constructor : componentType.getDeclaredConstructors()) {
            if (injectAnnotations.isAnnotationPresent(constructor)) {
                constructors.add(constructor);
            }
            if (constructor.getParameterCount() == 0) {
                noArgConstructor = constructor;
            }
        }
        if (constructors.size() > 1) {
            errorCollector.add(
                    new InjectableConstructorDuplicatedException(componentType, constructors));
            return Optional.empty();
        } else if (constructors.size() == 1) {
            final Constructor<?> constructor = constructors.iterator().next();
            final List<InjectionComponentResolver> resolvers = injectionComponentResolverFactory
                    .fromConstructorParameters(constructor);
            final InjectableConstructor injectableConstructor = new InjectableConstructor(
                    constructor, resolvers);
            return Optional.of(injectableConstructor);
        } else if (noArgConstructor == null) {
            errorCollector.add(
                    new InjectableConstructorNotFoundException(componentType));
            return Optional.empty();
        }

        final List<InjectionComponentResolver> resolvers = Collections.emptyList();
        final InjectableConstructor injectableConstructor = new InjectableConstructor(
                noArgConstructor, resolvers);
        return Optional.of(injectableConstructor);
    }

    public InjectableMember createFactoryMethod(final ComponentId id, final Method factoryMethod,
            final ErrorCollector errorCollector) {
        final List<InjectionComponentResolver> resolvers = injectionComponentResolverFactory
                .fromMethodParameters(factoryMethod);
        final InjectableMember injectableFactoryMethod = new InjectableFactoryMethod(id,
                factoryMethod, resolvers);
        return injectableFactoryMethod;
    }

    public Set<InjectableMember> createFieldsAndMethods(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final FieldCollector fieldCollector = new FieldCollector();
        final MethodCollector methodCollector = new MethodCollector();
        final Map<Class<?>, List<Field>> fields = new IdentityHashMap<>();
        final Map<Class<?>, List<Method>> methods = new IdentityHashMap<>();
        final List<Class<?>> classes = new ArrayList<>();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            classes.add(clazz);
            fields.put(clazz, new ArrayList<>());
            methods.put(clazz, new ArrayList<>());
            for (final Field field : clazz.getDeclaredFields()) {
                fieldCollector.addInstanceField(field);
                if (Modifier.isStatic(field.getModifiers())
                        && injectAnnotations.isAnnotationPresent(field)) {
                    errorCollector.add(new StaticInjectionException(componentType, field));
                }
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && injectAnnotations.isAnnotationPresent(method)) {
                    errorCollector.add(new StaticInjectionException(componentType, method));
                }
            }
        }

        for (final Field field : fieldCollector.getFields()) {
            if (injectAnnotations.isAnnotationPresent(field)) {
                final Class<?> key = field.getDeclaringClass();
                final List<Field> list = fields.get(key);
                list.add(field);
            }
        }

        for (final Method method : methodCollector.getMethods()) {
            if (injectAnnotations.isAnnotationPresent(method)) {
                final Class<?> key = method.getDeclaringClass();
                final List<Method> list = methods.get(key);
                list.add(method);
            }
        }

        Collections.reverse(classes);

        final Set<InjectableMember> injectableMembers = new LinkedHashSet<>();
        for (final Class<?> clazz : classes) {
            for (final Field field : fields.get(clazz)) {
                final InjectionComponentResolver resolver = injectionComponentResolverFactory
                        .fromField(field);
                final InjectableField injectableField = new InjectableField(field, resolver);
                injectableMembers.add(injectableField);
            }
            for (final Method method : methods.get(clazz)) {
                final List<InjectionComponentResolver> resolvers = injectionComponentResolverFactory
                        .fromMethodParameters(method);
                final InjectableMethod injectableMethod = new InjectableMethod(method, resolvers);
                injectableMembers.add(injectableMethod);
            }
        }

        return injectableMembers;
    }

    public Set<ObservesMethod> createObservesMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {

        final MethodCollector methodCollector = new MethodCollector();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && observesAnnotations.isAnnotationPresent(method)) {
                    errorCollector.add(new ObserverMethodSignatureException());
                }
            }
        }

        final Set<ObservesMethod> observesMethods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (observesAnnotations.isAnnotationPresent(method)) {
                if (method.getParameterCount() == 1) {
                    final ObservesMethod observesMethod = new DefaultObservesMethod(method);
                    observesMethods.add(observesMethod);
                } else {
                    errorCollector.add(new ObserverMethodSignatureException());
                }
            }
        }

        return observesMethods;
    }

    public Optional<InitMethod> createInitMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {

        final MethodCollector methodCollector = new MethodCollector();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && initAnnotations.isAnnotationPresent(method)) {
                    errorCollector.add(new LifeCycleMethodSignatureException());
                }
            }
        }
        final Set<InitMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (initAnnotations.isAnnotationPresent(method)) {
                if (method.getParameterCount() == 0) {
                    methods.add(new DefaultInitMethod(method));
                } else {
                    errorCollector.add(new LifeCycleMethodSignatureException());
                }
            }
        }
        if (methods.size() > 1) {
            errorCollector.add(new LifeCycleMethodDuplicatedException());
            return Optional.empty();
        } else if (methods.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(methods.iterator().next());
    }

    public Optional<DestroyMethod> createDestroyMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {

        final MethodCollector methodCollector = new MethodCollector();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && destroyAnnotations.isAnnotationPresent(method)) {
                    errorCollector.add(new LifeCycleMethodSignatureException());
                }
            }
        }
        final Set<DestroyMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (destroyAnnotations.isAnnotationPresent(method)) {
                if (method.getParameterCount() == 0) {
                    methods.add(new DefaultDestroyMethod(method));
                } else {
                    errorCollector.add(new LifeCycleMethodSignatureException());
                }
            }
        }
        if (methods.size() > 1) {
            errorCollector.add(new LifeCycleMethodDuplicatedException());
            return Optional.empty();
        } else if (methods.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(methods.iterator().next());
    }

    public Optional<DestroyMethod> createFactoryDestroyMethod(final Method factoryMethod,
            final ErrorCollector errorCollector) {
        return factoryAnnotations.getStringElement(factoryMethod, destroyMethodName)
                .flatMap(destroy -> {
                    if (destroy.isEmpty()) {
                        return Optional.empty();
                    }
                    final Class<?> componentType = factoryMethod.getReturnType();
                    final MethodCollector methodCollector = new MethodCollector();
                    for (final Class<?> clazz : new ClassInheritances(componentType)) {
                        for (final Method method : clazz.getDeclaredMethods()) {
                            methodCollector.addInstanceMethodIfNotOverridden(method);
                            if (Modifier.isStatic(method.getModifiers())
                                    && method.getName().equals(destroy)) {
                                errorCollector.add(new LifeCycleMethodSignatureException());
                                return Optional.empty();
                            }
                        }
                    }
                    final Set<Method> methods = new LinkedHashSet<>();
                    for (final Method method : methodCollector.getMethods()) {
                        if (method.getName().equals(destroy)) {
                            methods.add(method);
                        }
                    }
                    if (methods.isEmpty()) {
                        errorCollector.add(new LifeCycleMethodNotFoundException());
                        return Optional.empty();
                    }
                    for (final Method method : methods) {
                        if (method.getParameterCount() == 0) {
                            final DestroyMethod destroyMethod = new DefaultDestroyMethod(method);
                            return Optional.of(destroyMethod);
                        }
                    }
                    errorCollector.add(new LifeCycleMethodSignatureException());
                    return Optional.empty();
                });
    }

    public Set<FactoryMethod> createFactoryMethods(final ComponentId id,
            final Class<?> componentType,
            final AnnotationComponentDefinitionFactory componentDefinitionFactory,
            final ErrorCollector errorCollector) {
        final MethodCollector methodCollector = new MethodCollector();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && factoryAnnotations.isAnnotationPresent(method)) {
                    errorCollector.add(new FactoryMethodSignatureException());
                }
            }
        }
        final Set<FactoryMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (factoryAnnotations.isAnnotationPresent(method)) {
                if (method.getReturnType() == Void.TYPE) {
                    errorCollector.add(new FactoryMethodSignatureException());
                } else if (method.getParameterCount() != 0) {
                    errorCollector.add(new FactoryMethodSignatureException());
                } else {
                    final ComponentKey<?> key = ComponentKey.fromFactoryMethod(method);
                    final Optional<ComponentDefinition<Object>> definition = componentDefinitionFactory
                            .fromMethod(id, method, errorCollector);
                    definition.ifPresent(a -> methods.add(new DefaultFactoryMethod(key, a)));
                }
            }
        }

        return methods;
    }

    public static AnnotationMemberFactory createDefault() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private AnnotationSet qualifierAnnotations = new AnnotationSet(Qualifier.class);

        private AnnotationSet injectAnnotations = new AnnotationSet(Inject.class);
        private AnnotationSet initAnnotations = new AnnotationSet(Init.class);
        private AnnotationSet destroyAnnotations = new AnnotationSet(Destroy.class);
        private AnnotationSet observesAnnotations = new AnnotationSet(Observes.class);
        private AnnotationSet factoryAnnotations = new AnnotationSet(Factory.class);

        private String destroyMethodName = "destroy";

        private Builder() {
        }

        @SafeVarargs
        public final Builder qualifierAnnotations(
                final Class<? extends Annotation>... annotations) {
            this.qualifierAnnotations = new AnnotationSet(annotations);
            return this;
        }

        @SafeVarargs
        public final Builder injectAnnotations(final Class<? extends Annotation>... annotations) {
            this.injectAnnotations = new AnnotationSet(annotations);
            return this;
        }

        @SafeVarargs
        public final Builder initAnnotations(final Class<? extends Annotation>... annotations) {
            this.initAnnotations = new AnnotationSet(annotations);
            return this;
        }

        @SafeVarargs
        public final Builder destroyAnnotations(final Class<? extends Annotation>... annotations) {
            this.destroyAnnotations = new AnnotationSet(annotations);
            return this;
        }

        @SafeVarargs
        public final Builder observesAnnotations(final Class<? extends Annotation>... annotations) {
            this.observesAnnotations = new AnnotationSet(annotations);
            return this;
        }

        @SafeVarargs
        public final Builder factoryAnnotations(final Class<? extends Annotation>... annotations) {
            this.factoryAnnotations = new AnnotationSet(annotations);
            return this;
        }

        public Builder destroyMethodName(final String destroyMethodName) {
            this.destroyMethodName = destroyMethodName;
            return this;
        }

        public AnnotationMemberFactory build() {
            final AnnotationInjectionComponentResolverFactory injectionComponentResolverFactory = new AnnotationInjectionComponentResolverFactory(
                    qualifierAnnotations);
            return new AnnotationMemberFactory(injectAnnotations, initAnnotations,
                    destroyAnnotations, observesAnnotations, factoryAnnotations,
                    injectionComponentResolverFactory, destroyMethodName);
        }
    }
}
