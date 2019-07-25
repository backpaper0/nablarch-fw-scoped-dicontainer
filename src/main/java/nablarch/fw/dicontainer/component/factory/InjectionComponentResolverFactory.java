package nablarch.fw.dicontainer.component.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import nablarch.fw.dicontainer.component.InjectionComponentResolver;
import nablarch.fw.dicontainer.component.impl.InjectionComponentResolvers;

/**
 * 依存コンポーネントのリゾルバを生成するファクトリ。
 *
 */
public interface InjectionComponentResolverFactory {

    /**
     * フィールドをもとに依存コンポーネントのリゾルバを生成する。
     * 
     * @param field フィールド
     * @return 依存コンポーネントのリゾルバ
     */
    InjectionComponentResolver fromField(Field field);

    /**
     * メソッドのパラメータをもとに依存コンポーネントのリゾルバを生成する。
     * 
     * @param method メソッド
     * @return 依存コンポーネントのリゾルバ
     */
    InjectionComponentResolvers fromMethodParameters(Method method);

    /**
     * コンストラクタのパラメータをもとに依存コンポーネントのリゾルバを生成する。
     * 
     * @param constructor コンストラクタ
     * @return 依存コンポーネントのリゾルバ
     */
    InjectionComponentResolvers fromConstructorParameters(
            Constructor<?> constructor);

}