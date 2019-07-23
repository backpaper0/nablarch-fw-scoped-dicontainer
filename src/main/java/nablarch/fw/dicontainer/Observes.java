package nablarch.fw.dicontainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.core.util.annotation.Published;

/**
 * 発火されたイベントをハンドリングするメソッドであることを表すアノテーション。
 * 発火されたイベントの型をもとにしてハンドリングするメソッドが決定される。
 * 
 * <p>{@code @Observes}を付与するメソッドは次の制約を守らなければならない。</p>
 * <ul>
 * <li>staticメソッドではないこと</li>
 * <li>イベントを受け取ること（引数が1つあること）</li>
 * </ul>
 *
 */
@Published
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Observes {
}
