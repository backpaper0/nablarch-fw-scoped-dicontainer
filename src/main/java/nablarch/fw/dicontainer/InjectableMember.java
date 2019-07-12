package nablarch.fw.dicontainer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public interface InjectableMember {

    Object inject(Container container, Object component);

    static Set<InjectableMember> fromAnnotation(final Class<?> componentType) {

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
            }
            for (final Method method : clazz.getDeclaredMethods()) {
                methodCollector.addInstanceMethodIfNotOverridden(method);
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
                final InjectionComponentResolver resolver = InjectionComponentResolver
                        .fromField(field);
                final InjectableField injectableField = new InjectableField(field, resolver);
                injectableMembers.add(injectableField);
            }
            for (final Method method : methods.get(clazz)) {
                final List<InjectionComponentResolver> resolvers = InjectionComponentResolver
                        .fromMethodParameters(method);
                final InjectableMethod injectableMethod = new InjectableMethod(method, resolvers);
                injectableMembers.add(injectableMethod);
            }
        }

        return injectableMembers;
    }
}
