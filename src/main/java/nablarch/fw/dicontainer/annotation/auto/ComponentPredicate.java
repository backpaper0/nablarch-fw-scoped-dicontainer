package nablarch.fw.dicontainer.annotation.auto;

/**
 * コンポーネントとみなすための条件を表す述語。
 */
public interface ComponentPredicate {

    /**
     * 与えられた{@link Class}がコンポーネントであるかどうか判定する。
     * @param clazz 対象{@link Class}
     * @return コンポーネントと判定された場合、真
     */
    boolean test(Class<?> clazz);
}
