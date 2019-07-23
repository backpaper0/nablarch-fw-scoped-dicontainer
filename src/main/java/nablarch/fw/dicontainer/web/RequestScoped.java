package nablarch.fw.dicontainer.web;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

import nablarch.core.util.annotation.Published;

/**
 * コンポーネントがリクエストスコープであることを表すアノテーション。
 * 
 */
@Published
@Scope
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestScoped {
}
