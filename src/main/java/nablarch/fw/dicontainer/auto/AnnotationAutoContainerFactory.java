package nablarch.fw.dicontainer.auto;

import java.lang.annotation.Annotation;
import java.util.ServiceLoader;

import javax.inject.Qualifier;
import javax.inject.Scope;

import nablarch.fw.dicontainer.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.Container;

public class AnnotationAutoContainerFactory {

    public Container create() {
        final ServiceLoader<TraversalMark> traversalMarks = ServiceLoader.load(TraversalMark.class);
        return create(traversalMarks);
    }

    public Container create(final Iterable<TraversalMark> traversalMarks) {
        final AnnotationContainerBuilder builder = new AnnotationContainerBuilder();
        for (final TraversalMark traversalMark : traversalMarks) {
            final ClassLoader classLoader = traversalMark.getClass().getClassLoader();
            final Class<?> base = traversalMark.getClass();
            final ClassFilter classFilter = ClassFilter.valueOf(traversalMark);
            final ClassTraverser classTraverser = new ClassTraverser(classLoader, base,
                    classFilter);
            classTraverser.traverse(clazz -> {
                if (isTarget(clazz)) {
                    builder.register(clazz);
                }
            });
        }
        return builder.build();
    }

    private static boolean isTarget(final Class<?> clazz) {
        for (final Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(Scope.class)
                    || annotationType.isAnnotationPresent(Qualifier.class)) {
                return true;
            }
        }
        return false;
    }
}
