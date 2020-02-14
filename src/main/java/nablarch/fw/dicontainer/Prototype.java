package nablarch.fw.dicontainer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * コンポーネントがプロトタイプスコープであることを表すアノテーション。
 * 
 * <p>プロトタイプスコープのコンポーネントは破棄のタイミングが自明でないため、
 * 破棄のライフサイクルメソッドは呼び出されない。</p>
 *
 */
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Prototype {
}
