package nablarch.fw.dicontainer.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import javax.inject.Qualifier;

import nablarch.fw.dicontainer.component.ComponentKey;

/**
 * アノテーションをもとに検索キーを生成するファクトリクラス。
 *
 */
public final class AnnotationComponentKeyFactory {

    /**
     * 限定子のアノテーションセット
     */
    private final AnnotationSet qualifierAnnotations;

    /**
     * インスタンスを生成する。
     * 
     * @param qualifierAnnotations 限定子のアノテーションセット
     */
    private AnnotationComponentKeyFactory(final AnnotationSet qualifierAnnotations) {
        this.qualifierAnnotations = Objects.requireNonNull(qualifierAnnotations);
    }

    /**
     * コンポーネントのクラスをもとに検索キーを生成する。
     * 
     * @param componentType コンポーネントのクラス
     * @return 検索キー
     */
    public <T> ComponentKey<T> fromComponentClass(final Class<T> componentType) {
        final Set<Annotation> qualifiers = qualifierAnnotations
                .filter(componentType.getAnnotations());
        return new ComponentKey<>(componentType, qualifiers);
    }

    /**
     * ファクトリメソッドをもとに検索キーを生成する。
     * 
     * @param factoryMethod ファクトリメソッド
     * @return 検索キー
     */
    public ComponentKey<?> fromFactoryMethod(final Method factoryMethod) {
        final Set<Annotation> qualifiers = qualifierAnnotations
                .filter(factoryMethod.getAnnotations());
        return new ComponentKey<>(factoryMethod.getReturnType(), qualifiers);
    }

    /**
     * デフォルトの設定をしたインスタンスを構築する。
     * 
     * @return インスタンス
     */
    public static AnnotationComponentKeyFactory createDefault() {
        return builder().build();
    }

    /**
     * ビルダーを生成する。
     * 
     * @return ビルダー
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * ビルダー。
     *
     */
    public static final class Builder {

        /**
         * 限定子のアノテーションセット
         */
        private AnnotationSet qualifierAnnotations = new AnnotationSet(Qualifier.class);

        /**
         * インスタンスを生成する。
         */
        private Builder() {
        }

        /**
         * 限定子のアノテーションセットを設定する。
         * 
         * @param qualifierAnnotations 限定子のアノテーションセット
         * @return このビルダー自身
         */
        public Builder qualifierAnnotations(final AnnotationSet qualifierAnnotations) {
            this.qualifierAnnotations = qualifierAnnotations;
            return this;
        }

        /**
         * インスタンスを構築する。
         * 
         * @return 構築されたインスタンス
         */
        public AnnotationComponentKeyFactory build() {
            return new AnnotationComponentKeyFactory(qualifierAnnotations);
        }
    }
}
