package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.Init;
import nablarch.fw.dicontainer.Observes;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableConstructor;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.MethodCollector;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.factory.InjectionComponentResolverFactory;
import nablarch.fw.dicontainer.component.factory.MemberFactory;
import nablarch.fw.dicontainer.component.impl.DefaultDestroyMethod;
import nablarch.fw.dicontainer.component.impl.DefaultInitMethod;
import nablarch.fw.dicontainer.component.impl.DefaultInjectableConstructor;
import nablarch.fw.dicontainer.component.impl.DefaultObservesMethod;
import nablarch.fw.dicontainer.component.impl.InjectableField;
import nablarch.fw.dicontainer.component.impl.InjectableMethod;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;
import nablarch.fw.dicontainer.exception.InjectableConstructorDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectableConstructorNotFoundException;
import nablarch.fw.dicontainer.exception.LifeCycleMethodDuplicatedException;

/**
 * アノテーションをもとにコンポーネント定義の構成要素を生成するファクトリクラス。
 *
 */
public final class AnnotationMemberFactory implements MemberFactory {

    /**
     * 依存コンポーネントリゾルバのファクトリ
     */
    private final InjectionComponentResolverFactory injectionComponentResolverFactory;

    /**
     * コンストラクタを生成する。
     * 
     * @param injectionComponentResolverFactory 依存コンポーネントリゾルバのファクトリ
     */
    public AnnotationMemberFactory(
            final InjectionComponentResolverFactory injectionComponentResolverFactory) {
        this.injectionComponentResolverFactory = Objects
                .requireNonNull(injectionComponentResolverFactory);
    }

    @Override
    public Optional<InjectableConstructor> createConstructor(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final Set<Constructor<?>> constructors = Arrays
                .stream(componentType.getDeclaredConstructors())
                .filter(a -> a.isAnnotationPresent(Inject.class))
                .collect(Collectors.toSet());

        if (constructors.size() > 1) {
            errorCollector.add(
                    new InjectableConstructorDuplicatedException(
                            "Component [" + componentType.getName()
                                    + "] must be satisfied one of the following:"
                                    + " 1) Contains only default constructor."
                                    + " 2) Contains one constructor with @Inject annotation."));
            return Optional.empty();
        } else if (constructors.size() == 1) {
            final Constructor<?> constructor = constructors.iterator().next();
            final InjectionComponentResolvers resolvers = injectionComponentResolverFactory
                    .fromConstructorParameters(constructor);
            final DefaultInjectableConstructor injectableConstructor = new DefaultInjectableConstructor(
                    constructor, resolvers);
            return Optional.of(injectableConstructor);
        }

        final Constructor<?> noArgConstructor = Arrays
                .stream(componentType.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny().orElse(null);

        if (noArgConstructor == null) {
            errorCollector.add(
                    new InjectableConstructorNotFoundException(
                            "Component [" + componentType.getName()
                                    + "] must be satisfied one of the following:"
                                    + " 1) Contains only default constructor."
                                    + " 2) Contains one constructor with @Inject annotation."));
            return Optional.empty();
        }

        final InjectionComponentResolvers resolvers = InjectionComponentResolvers.empty();
        final DefaultInjectableConstructor injectableConstructor = new DefaultInjectableConstructor(
                noArgConstructor, resolvers);
        return Optional.of(injectableConstructor);
    }

    @Override
    public List<InjectableMember> createFieldsAndMethods(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final MethodCollector methodCollector = new MethodCollector();
        final Map<Class<?>, List<Field>> fields = new IdentityHashMap<>();
        final Map<Class<?>, List<Method>> methods = new IdentityHashMap<>();
        final List<Class<?>> classes = new ArrayList<>();
        for (final Class<?> clazz : new ClassInheritances(componentType)) {
            classes.add(clazz);
            fields.put(clazz, new ArrayList<>());
            methods.put(clazz, new ArrayList<>());
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    final Class<?> key = field.getDeclaringClass();
                    final List<Field> list = fields.get(key);
                    list.add(field);
                }
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addMethodIfNotOverridden(method);
            }
        }

        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Inject.class)) {
                final Class<?> key = method.getDeclaringClass();
                final List<Method> list = methods.get(key);
                list.add(method);
            }
        }

        Collections.reverse(classes);

        final List<InjectableMember> injectableMembers = new ArrayList<>();
        for (final Class<?> clazz : classes) {
            for (final Field field : fields.get(clazz)) {
                final InjectionComponentResolver resolver = injectionComponentResolverFactory
                        .fromField(field);
                final InjectableField injectableField = new InjectableField(field, resolver);
                injectableMembers.add(injectableField);
            }
            for (final Method method : methods.get(clazz)) {
                final InjectionComponentResolvers resolvers = injectionComponentResolverFactory
                        .fromMethodParameters(method);
                final InjectableMethod injectableMethod = new InjectableMethod(method, resolvers);
                injectableMembers.add(injectableMethod);
            }
        }

        return injectableMembers;
    }

    @Override
    public List<ObservesMethod> createObservesMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {

        final MethodCollector methodCollector = MethodCollector.collectFromClass(componentType);

        final List<ObservesMethod> observesMethods = new ArrayList<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Observes.class)) {
                final ObservesMethod observesMethod = new DefaultObservesMethod(method);
                observesMethods.add(observesMethod);
            }
        }

        return observesMethods;
    }

    @Override
    public Optional<InitMethod> createInitMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        return createLifeCycleMethod("Init", Init.class, DefaultInitMethod::new, componentType,
                errorCollector);
    }

    @Override
    public Optional<DestroyMethod> createDestroyMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        return createLifeCycleMethod("Destroy", Destroy.class, DefaultDestroyMethod::new,
                componentType,
                errorCollector);
    }

    private static <T> Optional<T> createLifeCycleMethod(final String name,
            final Class<? extends Annotation> annotationClass,
            final Function<Method, T> factory, final Class<?> componentType,
            final ErrorCollector errorCollector) {

        final MethodCollector methodCollector = MethodCollector.collectFromClass(componentType);
        final List<T> methods = new ArrayList<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                methods.add(factory.apply(method));
            }
        }
        if (methods.size() > 1) {
            errorCollector.add(new LifeCycleMethodDuplicatedException(
                    name + " method must be one per component [" + componentType.getName() + "]."));
            return Optional.empty();
        } else if (methods.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(methods.iterator().next());
    }
}
