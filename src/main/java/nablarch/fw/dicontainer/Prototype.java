package nablarch.fw.dicontainer;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import nablarch.core.util.annotation.Published;

/**
 * コンポーネントがプロトタイプスコープであることを表すアノテーション。
 * 
 * <p>プロトタイプスコープのコンポーネントは破棄のタイミングが自明でないため、
 * 破棄のライフサイクルメソッドは呼び出されない。</p>
 *
 */
@Published
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Prototype {
}
