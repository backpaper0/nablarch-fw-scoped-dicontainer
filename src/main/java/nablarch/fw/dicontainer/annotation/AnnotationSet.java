package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nablarch.fw.dicontainer.component.impl.reflect.MethodWrapper;

/**
 * アノテーションのクラスをまとめたクラス。
 *
 */
public final class AnnotationSet {

    /**
     * まとめられたアノテーションのクラス
     */
    private final Set<Class<? extends Annotation>> annotationClasses;

    /**
     * インスタンスを生成する。
     * 
     * @param annotationClasses まとめられたアノテーションのクラス
     */
    @SafeVarargs
    public AnnotationSet(final Class<? extends Annotation>... annotationClasses) {
        this(Stream.of(annotationClasses).collect(Collectors.toSet()));
    }

    /**
     * インスタンスを生成する。
     * 
     * @param annotationClasses まとめられたアノテーションのクラス
     */
    public AnnotationSet(final Set<Class<? extends Annotation>> annotationClasses) {
        this.annotationClasses = Objects.requireNonNull(annotationClasses);
    }

    /**
     * 渡された要素がこのアノテーションセットが持つアノテーションで注釈されているかどうかを返す。
     * 
     * @param annotatedElement 要素
     * @return このアノテーションセットが持つアノテーションで注釈されている場合は{@literal true}
     */
    public boolean isAnnotationPresent(final AnnotatedElement annotatedElement) {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            for (final Class<? extends Annotation> annotationClass : annotationClasses) {
                if (annotation.annotationType().equals(annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * このアノテーションセットが持つアノテーションだけに絞り込む。
     * 
     * @param annotations 絞り込む対象となるアノテーションの配列
     * @return 絞り込まれたアノテーションの集合
     */
    public Set<Annotation> filter(final Annotation[] annotations) {
        return Arrays.stream(annotations).filter(a -> isAnnotationPresent(a.annotationType()))
                .collect(Collectors.toSet());
    }

    /**
     * 渡された要素からアノテーションが持つ値を取得する。
     * 
     * @param annotatedElement 要素
     * @param elementName アノテーションが持つ値の名前
     * @return 値
     */
    public Optional<String> getStringElement(final AnnotatedElement annotatedElement,
            final String elementName) {
        for (final Annotation annotation : annotatedElement.getAnnotations()) {
            for (final Class<? extends Annotation> annotationClass : annotationClasses) {
                if (annotation.annotationType().equals(annotationClass)) {
                    for (final Method elementMethod : annotationClass.getDeclaredMethods()) {
                        if (elementMethod.getName().equals(elementName)) {
                            final String value = (String) new MethodWrapper(elementMethod)
                                    .invoke(annotation);
                            return Optional.of(value);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }
}
