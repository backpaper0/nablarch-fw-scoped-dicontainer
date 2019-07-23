package nablarch.fw.dicontainer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.core.util.annotation.Published;

/**
 * コンポーネントのメソッドが返す値をコンポーネントとして扱うためのファクトリーメソッドであることを表すアノテーション。
 * 
 * <p>{@code @Factory}を付与するメソッドは次の制約を守らなければならない。</p>
 * <ul>
 * <li>staticメソッドではないこと</li>
 * <li>引数がないこと</li>
 * <li>戻り値が定義されていること（戻り値がコンポーネントとなるため）</li>
 * </ul>
 * 
 */
@Published
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Factory {

    /**
     * コンポーネントの破棄を行うライフサイクルメソッドの名前を設定する。
     * 
     * <p>メソッドは次の制約を守らなければならない。</p>
     * <ul>
     * <li>staticメソッドではないこと</li>
     * <li>引数がないこと</li>
     * </ul>
     * 
     * @return 破棄を行うライフサイクルメソッドの名前
     */
    String destroy() default "";
}
