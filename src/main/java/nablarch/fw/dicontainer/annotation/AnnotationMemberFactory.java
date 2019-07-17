package nablarch.fw.dicontainer.annotation;

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
import java.util.Set;

import javax.inject.Inject;

import nablarch.fw.dicontainer.Destroy;
import nablarch.fw.dicontainer.Init;
import nablarch.fw.dicontainer.Observes;
import nablarch.fw.dicontainer.config.DestroyMethod;
import nablarch.fw.dicontainer.config.ErrorCollector;
import nablarch.fw.dicontainer.config.FieldCollector;
import nablarch.fw.dicontainer.config.InitMethod;
import nablarch.fw.dicontainer.config.InjectableConstructor;
import nablarch.fw.dicontainer.config.InjectableField;
import nablarch.fw.dicontainer.config.InjectableMember;
import nablarch.fw.dicontainer.config.InjectableMethod;
import nablarch.fw.dicontainer.config.InjectionComponentResolver;
import nablarch.fw.dicontainer.config.MethodCollector;
import nablarch.fw.dicontainer.config.ObservesMethod;
import nablarch.fw.dicontainer.config.DestroyMethod.DestroyMethodImpl;
import nablarch.fw.dicontainer.config.InitMethod.InitMethodImpl;
import nablarch.fw.dicontainer.exception.InjectableConstructorDuplicatedException;
import nablarch.fw.dicontainer.exception.InjectableConstructorNotFoundException;
import nablarch.fw.dicontainer.exception.StaticInjectionException;

public final class AnnotationMemberFactory {

    private final AnnotationInjectionComponentResolverFactory injectionComponentResolverFactory = new AnnotationInjectionComponentResolverFactory();

    public InjectableMember createConstructor(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final Set<Constructor<?>> constructors = new HashSet<>();
        Constructor<?> noArgConstructor = null;
        for (final Constructor<?> constructor : componentType.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                constructors.add(constructor);
            }
            if (constructor.getParameterCount() == 0) {
                noArgConstructor = constructor;
            }
        }
        if (constructors.size() > 1) {
            errorCollector.add(
                    new InjectableConstructorDuplicatedException(componentType, constructors));
            return InjectableMember.errorMock();
        } else if (constructors.size() == 1) {
            final Constructor<?> constructor = constructors.iterator().next();
            final List<InjectionComponentResolver> resolvers = injectionComponentResolverFactory
                    .fromConstructorParameters(constructor);
            final InjectableConstructor injectableConstructor = new InjectableConstructor(
                    constructor, resolvers);
            return injectableConstructor;
        } else if (noArgConstructor == null) {
            errorCollector.add(
                    new InjectableConstructorNotFoundException(componentType));
            return InjectableMember.errorMock();
        }

        final List<InjectionComponentResolver> resolvers = Collections.emptyList();
        final InjectableConstructor injectableConstructor = new InjectableConstructor(
                noArgConstructor, resolvers);
        return injectableConstructor;
    }

    public Set<InjectableMember> createFieldsAndMethods(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        final FieldCollector fieldCollector = new FieldCollector();
        final MethodCollector methodCollector = new MethodCollector();
        final Map<Class<?>, List<Field>> fields = new IdentityHashMap<>();
        final Map<Class<?>, List<Method>> methods = new IdentityHashMap<>();
        final List<Class<?>> classes = new ArrayList<>();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            classes.add(clazz);
            fields.put(clazz, new ArrayList<>());
            methods.put(clazz, new ArrayList<>());
            for (final Field field : clazz.getDeclaredFields()) {
                fieldCollector.addInstanceField(field);
                if (Modifier.isStatic(field.getModifiers())
                        && field.isAnnotationPresent(Inject.class)) {
                    errorCollector.add(new StaticInjectionException(componentType, field));
                }
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
                if (Modifier.isStatic(method.getModifiers())
                        && method.isAnnotationPresent(Inject.class)) {
                    errorCollector.add(new StaticInjectionException(componentType, method));
                }
            }
        }

        for (final Field field : fieldCollector.getFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                final Class<?> key = field.getDeclaringClass();
                final List<Field> list = fields.get(key);
                list.add(field);
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

        //TODO errorCollector

        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }

        final Set<ObservesMethod> observesMethods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Observes.class)) {
                if (method.getParameterCount() != 1) {
                    //TODO error
                }
                final ObservesMethod observesMethod = new ObservesMethod(method);
                observesMethods.add(observesMethod);
            }
        }

        return observesMethods;
    }

    public InitMethod createInitMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {
        //TODO errorCollector

        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<InitMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Init.class)) {
                methods.add(new InitMethodImpl(method));
            }
        }
        if (methods.size() > 1) {
            //TODO error
            throw new RuntimeException();
        } else if (methods.isEmpty()) {
            return InitMethod.noop();
        }
        return methods.iterator().next();
    }

    public DestroyMethod createDestroyMethod(final Class<?> componentType,
            final ErrorCollector errorCollector) {

        //TODO errorCollector

        final MethodCollector methodCollector = new MethodCollector();
        for (Class<?> clazz = componentType; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
            }
        }
        final Set<DestroyMethod> methods = new LinkedHashSet<>();
        for (final Method method : methodCollector.getMethods()) {
            if (method.isAnnotationPresent(Destroy.class)) {
                methods.add(new DestroyMethodImpl(method));
            }
        }
        if (methods.size() > 1) {
            //TODO error
            throw new RuntimeException();
        } else if (methods.isEmpty()) {
            return DestroyMethod.noop();
        }
        return methods.iterator().next();
    }
}
