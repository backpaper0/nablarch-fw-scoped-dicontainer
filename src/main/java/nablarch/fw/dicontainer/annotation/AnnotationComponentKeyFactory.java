package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Qualifier;

import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.component.factory.ComponentKeyFactory;

/**
 * アノテーションをもとに検索キーを生成するファクトリクラス。
 *
 */
public final class AnnotationComponentKeyFactory implements ComponentKeyFactory {

    @Override
    public <T> ComponentKey<T> fromComponentClass(final Class<T> componentType) {
        final Set<Annotation> qualifiers = Arrays.stream(componentType.getAnnotations())
                .filter(a -> a.annotationType().isAnnotationPresent(Qualifier.class))
                .collect(Collectors.toSet());
        return new ComponentKey<>(componentType, qualifiers);
    }
}
