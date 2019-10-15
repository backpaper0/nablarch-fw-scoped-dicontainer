package nablarch.fw.dicontainer.annotation.auto;

import java.lang.annotation.Annotation;

import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * {@link ComponentPredicate}のデフォルト実装クラス。
 *
 * 対象クラスに{@link Scope}または{@link Qualifier}アノテーションが付与されていた場合、
 * コンポーネントであると判定する。
 */
public final class DefaultComponentPredicate implements ComponentPredicate {

    @Override
    public boolean test(final Class<?> clazz) {
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
